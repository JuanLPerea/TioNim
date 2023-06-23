package com.tionim.game

import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.values
import com.google.firebase.ktx.Firebase
import com.tionim.game.Modelos.Jugador
import com.tionim.game.Modelos.Partida
import com.tionim.game.Modelos.Records
import com.tionim.game.Utilidades.Constantes
import com.tionim.game.Utilidades.SharedPrefs
import com.tionim.game.Utilidades.Sonidos
import com.tionim.game.Utilidades.Utilidades
import com.tionim.game.Utilidades.UtilityNetwork
import com.tionim.game.Utilidades.UtilsFirebase
import java.util.concurrent.ExecutorService


class MainActivity : AppCompatActivity() {

/*
    private val PERMISOS = arrayOf<String>(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
*/
    //  AnimacionTitulo animacionTitulo;
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null
    private var userRef: DatabaseReference? = null
    private var jugadoresRef: DatabaseReference? = null
    private var partidasRef: DatabaseReference? = null
    private var recordsRef: DatabaseReference? = null
    private val nickET: EditText? = null
    private val onlineTV: TextView? = null
    private  var victoriasTV:TextView? = null
    private val botonOnline: Button? = null
    private val avatarJugador: ImageView? = null
    private val palitrokesIV: ImageView? = null
    private  var lemaIV:android.widget.ImageView? = null
    private  var nombreIV:android.widget.ImageView? = null
    private val favoritosBTN: ImageButton? = null
    private var jugador: Jugador? = null
    private val recordsRecycler: RecyclerView? = null
    private val adapter: RecyclerView.Adapter<*>? = null
    private val layoutManager: RecyclerView.LayoutManager? = null
    private val fab: FloatingActionButton? = null
    private val records: MutableList<Records>? = null
    private val partidas: MutableList<Partida>? = null
    private var partida: Partida? = null
    private var partidasListener: ValueEventListener? = null
    private val recordsListener: ValueEventListener? = null
    private val executorService: ExecutorService? = null
    private val photo_uri //para almacenar la ruta de la imagen
            : Uri? = null
    private val ruta_foto //nombre fichero creado
            : String? = null
    private val permisosOK = false
    private val soloFavoritos = false
    private val mediaPlayer: MediaPlayer? = null
    private val easterEgg = 0
    private val animacionTimer: CountDownTimer? = null
    private val jugarOnline: Dialog? = null
    private val rivalReady: TextView? = null
    private val jugadorReady: TextView? = null
    private val readyJugadorIMG: ImageView? = null
    private val readyRivalIMG: ImageView? = null
    private val mensajeEstado: TextView? = null
    private val progressBar: ProgressBar? = null
    private val avatarRival: ImageView? = null
    private val favoritoAdd: ImageButton? = null
    private val readyBTN: Button? = null
    private var rivalEncontrado = false
    private val partidaActualizacion: Partida? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = Firebase.auth

        val database = FirebaseDatabase.getInstance("https://tionim-8fedb-default-rtdb.europe-west1.firebasedatabase.app")
        val myRef = database.getReference("datos")
        myRef.setValue(("Hola mundo"))

        val dato = myRef.child(("datos")).values<String>()


        myRef.addValueEventListener(object : ValueEventListener {
          override fun onDataChange(dataSnapshot: DataSnapshot) {
              val dato = dataSnapshot.value
              Log.d("Miapp" , dato.toString())
          }
            override fun onCancelled(error : DatabaseError) {
                Log.d("Miapp" , error.toString())
            }
        } )




    }

    public override fun onStart() {
        super.onStart()

    }

    private fun signIn() {
        // Comprobar si tenemos internet
        if (UtilityNetwork.isNetworkAvailable(this) || UtilityNetwork.isWifiAvailable(this)) {

            // Si tenemos internet recuperamos los datos del usuario de Firebase
            // Nos autenticamos de forma anónima en Firebase
            mAuth = FirebaseAuth.getInstance()
            mAuth!!.signInAnonymously()
                .addOnCompleteListener(this,
                    OnCompleteListener<AuthResult?> { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(Constantes.TAG, "signInAnonymously:success")
                            val user: FirebaseUser? = mAuth!!.getCurrentUser()
                            endSignIn(user!!)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(Constantes.TAG, "signInAnonymously:failure", task.exception)
                            Toast.makeText(
                                this@MainActivity,
                                R.string.fallo_auth,
                                Toast.LENGTH_SHORT
                            ).show()
                            endSignIn(null)
                        }
                    })
        }
    }

    private fun endSignIn(currentUser: FirebaseUser?) {
        mDatabase = FirebaseDatabase.getInstance().reference
        userRef = mDatabase!!.child("USUARIOS").child(currentUser!!.uid)
        partidasRef = mDatabase!!.child("PARTIDAS")
        jugadoresRef = mDatabase!!.child("USUARIOS")

        // Cargamos los datos del usuario o los creamos si no existen (La primera vez que instalamos la APP)
        val userListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                jugador = dataSnapshot.getValue<Jugador>(Jugador::class.java)
                if (jugador == null) {
                    // Si el jugador es nuevo lo creamos
                    var nickName = ""
                    if (nickET!!.getText() == null) {
                        nickName = getString(R.string.jugador)
                    } else {
                        nickName = nickET!!.getText().toString()
                        if (Utilidades.eliminarPalabrotas(nickName)) {
                            Toast.makeText(
                                applicationContext,
                                getString(R.string.palabrota),
                                Toast.LENGTH_LONG
                            ).show()
                            nickName = getString(R.string.jugador)
                        }
                    }
                    jugador = Jugador(currentUser!!.uid, nickName)

                    // Subimos una imagen a Firebase Storage con el nombre del ID del jugador
                    // para usarla como avatar
                    val avatarNuevo = BitmapFactory.decodeResource(
                        applicationContext.resources, R.drawable.picture
                    )
                    UtilsFirebase.subirImagenFirebase(currentUser!!.uid, avatarNuevo)
                    Utilidades.guardarImagenMemoriaInterna(
                        applicationContext,
                        Constantes.ARCHIVO_IMAGEN_JUGADOR,
                        Utilidades.bitmapToArrayBytes(avatarNuevo)
                    )
                    Utilidades.guardarImagenMemoriaInterna(
                        applicationContext,
                        jugador!!.getJugadorId()!!,
                        Utilidades.bitmapToArrayBytes(avatarNuevo)
                    )
                    avatarJugador!!.setImageBitmap(avatarNuevo)
                    SharedPrefs.saveJugadorPrefs(applicationContext, jugador!!)
                    userRef!!.setValue(jugador)
                } else {
                    jugador = SharedPrefs.getJugadorPrefs(applicationContext)
                    userRef!!.setValue(jugador)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(Constantes.TAG, "Error Usuario BBDD o Crear nuevo")
            }
        }
        userRef!!.addListenerForSingleValueEvent(userListener)
        cargarRecords()


        // Hacemos una lista con las partidas existentes
        // (La partida se crea cuando un jugador da al botón jugar online)
        // y se destruye al terminar de jugar la partida o al abandonar el dialogo de esperar rival
        // Ponemos un listener que llenará la lista actualizando cada vez que los datos cambien en Firebase
        //
        partidasListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                partidas!!.removeAll(partidas)
                var n = 0
                for (snapshot in dataSnapshot.children) {
                    val partidaTmp = snapshot.getValue(Partida::class.java)
                    partidaTmp!!.setNumeroSala(n)

                    // Si el jugador tiene una partida creada o seleccionada, aquí hacemos
                    // lo que corresponda si hay cambios en su estado
                    if (jugador!!.getPartida() != null) {
                        // Si esta es nuestra partida!!...
                        if (jugador!!.getPartida().equals(partidaTmp.getPartidaID())) {
                            partida = partidaTmp
                            actualizarDialogOnline()
                        }
                    }


                    // Mirar si hay un hueco disponible en la sala
                    if (partidaTmp.getJugador1ID().equals("0") || partidaTmp.getJugador2ID()
                            .equals("0")
                    ) {
                        // Mirar si solo queremos jugar con amigos
                        if (soloFavoritos) {
                            // si está en nuestra lista de favoritos lo añadimos a la lista
                            if (jugador!!.getFavoritosID()!!
                                    .contains(partidaTmp.getJugador1ID()) || jugador!!.getFavoritosID()!!
                                    .contains(partidaTmp.getJugador2ID())
                            ) {
                                partidas.add(partidaTmp)
                            }
                        } else {
                            // Si queremos jugar con cualquiera que esté disponible sea amigo o no, lo añadimos aquí
                            partidas.add(partidaTmp)
                        }
                    }
                    n++
                }

                // Mostrar las partidas disponibles, distinguiendo si queremos solo amigos o todos
                var textopartidas = ""
                textopartidas = if (soloFavoritos) {
                    getString(R.string.amigos_online)
                } else {
                    getString(R.string.jugadores_online)
                }
                onlineTV!!.setText(textopartidas + partidas.size)
                botonOnline!!.setEnabled(true)
                favoritosBTN!!.setEnabled(true)
                botonOnline.setVisibility(View.VISIBLE)
                favoritosBTN.setVisibility(View.VISIBLE)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }
        partidasRef!!.addValueEventListener(partidasListener!!)
    }

    private fun actualizarDialogOnline() {
        if (partida != null) {

            // Si los dos huecos están ocupados, cargamos la imagen del rival
            // También actualizamos nuestro estado 'ready'
            if (!partida!!.getJugador1ID().equals("0") && !partida!!.getJugador2ID().equals("0")) {
                // Encontrado rival, desactivamos progressbar
                progressBar!!.setVisibility(View.INVISIBLE)
                rivalEncontrado = true
                // Seteamos el botón del corazón dependiendo si es amigo o no
                favoritoAdd!!.setVisibility(View.VISIBLE)
                if (jugador!!.getNumeroJugador() === 1) {
                    if (jugador!!.getFavoritosID()!!.contains(partida!!.getJugador2ID())) {
                        favoritoAdd.setImageResource(R.drawable.corazonrojo)
                    }
                } else {
                    if (jugador!!.getFavoritosID()!!.contains(partida!!.getJugador1ID())) {
                        favoritoAdd.setImageResource(R.drawable.corazonrojo)
                    }
                }


                // Y descargamos imagen
                if (jugador!!.getNumeroJugador() === 1) {
                    UtilsFirebase.descargarImagenFirebaseView(
                        applicationContext,
                        partida!!.getJugador2ID(),
                        avatarRival!!
                    )
                } else {
                    UtilsFirebase.descargarImagenFirebaseView(
                        applicationContext,
                        partida!!.getJugador1ID(),
                        avatarRival!!
                    )
                }
            } else {
                avatarRival!!.setImageResource(R.drawable.search)
                progressBar!!.setVisibility(View.VISIBLE)
                rivalEncontrado = false
                favoritoAdd!!.setVisibility(View.INVISIBLE)
            }

            // Actualizar imagen 'preparado' y mensajes
            if (jugador!!.getNumeroJugador() === 1) {
                if (partida!!.isJugador1Ready()) {
                    readyJugadorIMG!!.setImageResource(R.drawable.tick)
                    readyJugadorIMG!!.setBackgroundColor(resources.getColor(R.color.verde))
                    jugadorReady!!.setText(R.string.preparado)
                } else {
                    readyJugadorIMG!!.setImageResource(R.drawable.update)
                    readyJugadorIMG!!.setBackgroundColor(resources.getColor(R.color.rojo))
                    jugadorReady!!.setText(R.string.buscando)
                }
                if (partida!!.isJugador2Ready()) {
                    readyRivalIMG!!.setImageResource(R.drawable.tick)
                    readyRivalIMG!!.setBackgroundColor(resources.getColor(R.color.verde))
                    rivalReady!!.setText(R.string.preparado)
                } else {
                    readyRivalIMG!!.setImageResource(R.drawable.update)
                    readyRivalIMG!!.setBackgroundColor(resources.getColor(R.color.rojo))
                    rivalReady!!.setText(R.string.buscando)
                }
            } else {
                if (partida!!.isJugador2Ready()) {
                    readyJugadorIMG!!.setImageResource(R.drawable.tick)
                    readyJugadorIMG!!.setBackgroundColor(resources.getColor(R.color.verde))
                    jugadorReady!!.setText(R.string.preparado)
                } else {
                    readyJugadorIMG!!.setImageResource(R.drawable.update)
                    readyJugadorIMG!!.setBackgroundColor(resources.getColor(R.color.rojo))
                    jugadorReady!!.setText(R.string.buscando)
                }
                if (partida!!.isJugador1Ready()) {
                    readyRivalIMG!!.setImageResource(R.drawable.tick)
                    readyRivalIMG!!.setBackgroundColor(resources.getColor(R.color.verde))
                    rivalReady!!.setText(R.string.preparado)
                } else {
                    readyRivalIMG!!.setImageResource(R.drawable.update)
                    readyRivalIMG!!.setBackgroundColor(resources.getColor(R.color.rojo))
                    rivalReady!!.setText(R.string.buscando)
                }
            }

            // Si los 2 jugadores están preparados, lanzamos el juego
            if (partida!!.isJugador1Ready() && partida!!.isJugador2Ready()) {
                // Quitar el listener de las partidas
                partidasRef!!.removeEventListener(partidasListener!!)

                // Guardar datos en el Shared Preferences
                SharedPrefs.saveJugadorPrefs(applicationContext, jugador!!)

                // Actualizar también Firebase
                Log.d(Constantes.TAG, "Numero jugador antes intent: " + jugador!!.getNumeroJugador())
                userRef!!.setValue(jugador)

                // Establecer la partida como que ha lanzado el dialogo juego online, para
                // diferenciar en el onStop y borrar la sala si llega porque ha cerrado la aplicación
                partida!!.setJugando(true)

                // Los 2 estamos listos. Lanzar Intent de juego
                val jugar = Intent(jugarOnline!!.getContext(), JuegoOnlineActivity::class.java)
                jugar.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                jugar.putExtra(Constantes.PARTIDA, partida!!.getPartidaID())
                when (jugador!!.getNumeroJugador()) {
                    1 -> jugar.putExtra(Constantes.RIVALIDONLINE, partida!!.getJugador2ID())
                    2 -> jugar.putExtra(Constantes.RIVALIDONLINE, partida!!.getJugador1ID())
                    else -> Log.d(Constantes.TAG, "Error en numero de jugador")
                }

                //animacionTitulo.cancel(true);
                animacionTimer!!.cancel()
                //   finish();
                Sonidos.play(Sonidos.Companion.Efectos.GANAR)
                mediaPlayer!!.stop()
                startActivity(jugar)
                jugarOnline!!.dismiss()
            }
        }
    }

    private fun cargarRecords() {

        // Cargar los records y mostrarlos en el Recycler
        recordsRef = FirebaseDatabase.getInstance().reference.child("RECORDS")
        val cargarRecords: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                var n = 0
                records!!.removeAll(records)
                for (snapshot in dataSnapshot.children) {
                    val recordTmp = snapshot.getValue(Records::class.java)
                    records!!.add(recordTmp!!)
                    // Descargamos imagen de Firebase y la guardamos en el dispositivo para usarla mas tarde
                    Utilidades.eliminarArchivo(applicationContext, "RECORDIMG$n.jpg")
                    UtilsFirebase.descargarImagenFirebaseYGuardarla(
                        applicationContext,
                        recordTmp!!.getIdJugador()
                    )
                    n++
                }
                adapter!!.notifyDataSetChanged()
                // Guardamos los records actualizados de Firebase en el Shared Preferences
                SharedPrefs.saveRecordsPrefs(applicationContext, records!!)
            }

            override fun onCancelled(@NonNull databaseError: DatabaseError) {}
        }
        recordsRef!!.addListenerForSingleValueEvent(cargarRecords)
    }


}