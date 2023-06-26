package com.tionim.game.Utilidades

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import androidx.annotation.NonNull
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.tionim.game.Modelos.Jugador
import com.tionim.game.Modelos.Records
import java.io.ByteArrayOutputStream
import java.util.Collections

class UtilsFirebase {

    companion object {

        fun subirImagenFirebase(jugadorID: String?, bitmap: Bitmap) {
            val storage: FirebaseStorage
            val storageRef: StorageReference

            // Referencia a Firebase Storage
            storage = FirebaseStorage.getInstance()
            storageRef = storage.getReference()

            // Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            val uploadTask: UploadTask =
                storageRef.child("AVATAR").child(jugadorID!!).putBytes(data)
            uploadTask.addOnFailureListener(OnFailureListener { exception -> // Handle unsuccessful uploads
                Log.d(Constantes.TAG, "Error Subiendo imagen $exception")
            })
                .addOnSuccessListener(OnSuccessListener<Any?> { // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                    Log.d(Constantes.TAG, "Foto Subida Correctamente")
                })
        }


        fun descargarImagenFirebaseYGuardarla(context: Context?, jugadorID: String?) {
            val storage: FirebaseStorage
            val storageRef: StorageReference
            val imagen = arrayOfNulls<Bitmap>(1)
            storage = FirebaseStorage.getInstance()
            storageRef = storage.getReference()
            val avatarRef: StorageReference = storageRef.child("AVATAR").child(jugadorID!!)
            // imagen[0] = null;
            val ONE_MEGABYTE = (1024 * 1024).toLong()
            avatarRef.getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(OnSuccessListener<ByteArray?> { bytes -> // Data for "images/island.jpg" is returns, use this as needed
                    // imagen[0] = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    Utilidades.guardarImagenMemoriaInterna(context!!, jugadorID, bytes)
                }).addOnFailureListener(OnFailureListener { exception ->
                    // Handle any errors
                    Log.d(Constantes.TAG, "Error Descargando Imagen $exception")
                    //   imageView.setImageDrawable(   R.drawable.camera);
                })
        }


        fun descargarImagenFirebaseView(context: Context?, jugadorID: String?, view: ImageView) {
            val storage: FirebaseStorage
            val storageRef: StorageReference
            val imagen = arrayOfNulls<Bitmap>(1)
            storage = FirebaseStorage.getInstance()
            storageRef = storage.getReference()
            val avatarRef: StorageReference = storageRef.child("AVATAR").child(jugadorID!!)
            // imagen[0] = null;
            val ONE_MEGABYTE = (1024 * 1024).toLong()
            avatarRef.getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(OnSuccessListener<ByteArray> { bytes -> // Data for "images/island.jpg" is returns, use this as needed
                    imagen[0] = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    view.setImageBitmap(imagen[0])
                }).addOnFailureListener(OnFailureListener { exception ->
                    // Handle any errors
                    Log.d(Constantes.TAG, "Error Descargando Imagen $exception")
                    //   imageView.setImageDrawable(   R.drawable.camera);
                })
        }

        fun guardarRecords(context: Context?, jugador: Jugador, level: Int) {
            val recordsRef = FirebaseDatabase.getInstance().reference.child("RECORDS")

            //Subimos nuestro avatar a Firebase (Aquí es seguro que tenemos internet)
            subirImagenFirebase(
                jugador.jugadorId,
                Utilidades.recuperarImagenMemoriaInterna(context!!, jugador.jugadorId)!!
            )

            // Recuperar la lista de records guardada en Firebase
            // Cargar los records y mostrarlos en el Recycler
            val cargarRecords: ValueEventListener = object : ValueEventListener {
                override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                    val listaRecordsNueva: MutableList<Records?> = ArrayList<Records?>()
                    for (snapshot in dataSnapshot.children) {
                        val recordTmp: Records? = snapshot.getValue(Records::class.java)
                        listaRecordsNueva.add(recordTmp)
                    }
                    listaRecordsNueva.add(
                        Records(
                            jugador.jugadorId,
                            jugador.nickname,
                            jugador.victorias,
                            level
                        )
                    )
                    listaRecordsNueva.sortBy { jugador.victorias }

                    // Guardamos en Firebase
                    for (n in listaRecordsNueva.indices) {
                        recordsRef.child("" + n).setValue(listaRecordsNueva[n])
                        if (n == 9) break
                    }
                }

                override fun onCancelled(@NonNull databaseError: DatabaseError) {}
            }
            recordsRef.addListenerForSingleValueEvent(cargarRecords)
        }
    }

}