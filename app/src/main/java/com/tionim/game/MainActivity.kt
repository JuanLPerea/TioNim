package com.tionim.game

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.values

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val database = FirebaseDatabase.getInstance("https://tionim-8fedb-default-rtdb.europe-west1.firebasedatabase.app")
        val myRef = database.getReference("datos")
        myRef.setValue(("Hola mundo"))

        val dato = myRef.child(("datos")).values<String>()


        val texto = findViewById(R.id.texto) as TextView

        texto.text = dato.toString()

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
}