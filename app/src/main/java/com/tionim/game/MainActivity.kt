package com.tionim.game

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.google.firebase.ktx.Firebase
import com.tionim.game.Adapters.RecordsAdapter
import com.tionim.game.Modelos.Jugador
import com.tionim.game.Modelos.Partida
import com.tionim.game.Modelos.Records
import com.tionim.game.Modelos.Tablero
import com.tionim.game.Utilidades.Constantes
import com.tionim.game.Utilidades.SharedPrefs
import com.tionim.game.Utilidades.Sonidos
import com.tionim.game.Utilidades.Utilidades
import com.tionim.game.Utilidades.UtilityNetwork
import com.tionim.game.Utilidades.UtilsFirebase
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.util.Locale
import java.util.Random
import java.util.concurrent.ExecutorService


class MainActivity : AppCompatActivity() {


    private val PERMISOS = arrayOf<String>(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    //  AnimacionTitulo animacionTitulo;
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null
    private var userRef: DatabaseReference? = null
    private var jugadoresRef: DatabaseReference? = null
    private var partidasRef: DatabaseReference? = null
    private var recordsRef: DatabaseReference? = null
    private var nickET: EditText? = null
    private var onlineTV: TextView? = null
    private  var victoriasTV:TextView? = null
    private var botonOnline: Button? = null
    private var avatarJugador: ImageView? = null
    private var palitrokesIV: ImageView? = null
    private  var lemaIV: ImageView? = null
    private  var nombreIV: ImageView? = null
    private var favoritosBTN: ImageButton? = null
    private var jugador: Jugador? = null
    private var recordsRecycler: RecyclerView? = null
    private var adapter: RecyclerView.Adapter<*>? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var fab: FloatingActionButton? = null
    private var records: MutableList<Records>? = null
    private var partidas: MutableList<Partida>? = null
    private var partida: Partida? = null
    private var partidasListener: ValueEventListener? = null
    private val recordsListener: ValueEventListener? = null
    private val executorService: ExecutorService? = null
    private var photo_uri //para almacenar la ruta de la imagen
            : Uri? = null
    private val ruta_foto //nombre fichero creado
            : String? = null
    private var permisosOK = false
    private var soloFavoritos = false
    private var mediaPlayer: MediaPlayer? = null
    private var easterEgg = 0
    private var animacionTimer: CountDownTimer? = null
    private var jugarOnline: Dialog? = null
    private var rivalReady: TextView? = null
    private var jugadorReady: TextView? = null
    private var readyJugadorIMG: ImageView? = null
    private var readyRivalIMG: ImageView? = null
    private var mensajeEstado: TextView? = null
    private var progressBar: ProgressBar? = null
    private var avatarRival: ImageView? = null
    private var favoritoAdd: ImageButton? = null
    private var readyBTN: Button? = null
    private var rivalEncontrado = false
    private var partidaActualizacion: Partida? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //supportActionBar!!.hide()

        // Referencias a las vistas
        nickET = findViewById(R.id.nickET)
        victoriasTV = findViewById(R.id.victoriasET)
        onlineTV = findViewById(R.id.onlineTV)
        recordsRecycler = findViewById(R.id.recordsRecycler)
        layoutManager = LinearLayoutManager(this)
        recordsRecycler!!.setLayoutManager(layoutManager)
        botonOnline = findViewById(R.id.jugaronlineBTN)
        avatarJugador = findViewById(R.id.avatarIV)
        lemaIV = findViewById(R.id.lemaIV)
        favoritosBTN = findViewById(R.id.favoritosBTN)
        nombreIV = findViewById(R.id.nombreIV)
        fab = findViewById(R.id.fab)
        fab!!.bringToFront()

        // Lanzamos el diálogo para esperar a que los 2 jugadores estén preparados
        dialogoJuegoOnline()

        // Mirar si el idioma es Inglés para cambiar el ImageView del título
        val idioma = Locale.getDefault().language // es

        if (idioma != "es") {
            lemaIV!!.setImageResource(R.drawable.lemaen)
            nombreIV!!.setImageResource(R.drawable.logoen)
            Log.d(Constantes.TAG, "El idioma no es español")
        }
        // EasterEgg
        easterEgg = 0

        // Flag para detectar si tenemos rival
        rivalEncontrado = false

        // Animacion del logo
        animacionPalitrokes()

        //Lista de partidas disponibles
        partidas = java.util.ArrayList()
        partida = null
        partidaActualizacion = Partida("",0,"","",Tablero(1),0)

        // Recycler View para los Records
        records = mutableListOf()
        adapter = RecordsAdapter(applicationContext, records!!)
        recordsRecycler!!.setAdapter(adapter)

        jugador = Jugador("","")

        // Pedir permisos para las fotos y avatares
        ActivityCompat.requestPermissions(this, PERMISOS, Constantes.CODIGO_PETICION_PERMISOS)

        // Recuperamos los datos del Shared Preferences
        recuperarDatosSharedPreferences()

    }

    public override fun onStart() {
        super.onStart()
        signIn()
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
                            cargarRecords()
                            endSignIn(user!!)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d(Constantes.TAG, "signInAnonymously:failure", task.exception)
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
                        jugador!!.jugadorId!!,
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



        // Hacemos una lista con las partidas existentes
        // (La partida se crea cuando un jugador da al botón jugar online)
        // y se destruye al terminar de jugar la partida o al abandonar el dialogo de esperar rival
        // Ponemos un listener que llenará la lista actualizando cada vez que los datos cambien en Firebase
        //
        partidasListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                partidas!!.removeAll(partidas!!)
                var n = 0
                for (snapshot in dataSnapshot.children) {
                    val partidaTmp = snapshot.getValue(Partida::class.java)
                    partidaTmp!!.numeroSala = n

                    // Si el jugador tiene una partida creada o seleccionada, aquí hacemos
                    // lo que corresponda si hay cambios en su estado
                    if (jugador!!.partida != null) {
                        // Si esta es nuestra partida!!...
                        if (jugador!!.partida.equals(partidaTmp.partidaID)) {
                            partida = partidaTmp
                            actualizarDialogOnline()
                        }
                    }


                    // Mirar si hay un hueco disponible en la sala
                    if (partidaTmp.jugador1ID.equals("0") || partidaTmp.jugador2ID
                            .equals("0")
                    ) {
                        // Mirar si solo queremos jugar con amigos
                        if (soloFavoritos) {
                            // si está en nuestra lista de favoritos lo añadimos a la lista
                            if (jugador!!.favoritosID!!
                                    .contains(partidaTmp.jugador1ID) || jugador!!.favoritosID!!
                                    .contains(partidaTmp.jugador2ID)
                            ) {
                                partidas!!.add(partidaTmp)
                            }
                        } else {
                            // Si queremos jugar con cualquiera que esté disponible sea amigo o no, lo añadimos aquí
                            partidas!!.add(partidaTmp)
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
                onlineTV!!.setText(textopartidas + partidas!!.size)
                botonOnline!!.setEnabled(true)
                favoritosBTN!!.setEnabled(true)
                botonOnline!!.setVisibility(View.VISIBLE)
                favoritosBTN!!.setVisibility(View.VISIBLE)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }
        partidasRef!!.addValueEventListener(partidasListener!!)
    }

    private fun actualizarDialogOnline() {
        if (partida != null) {

            // Si los dos huecos están ocupados, cargamos la imagen del rival
            // También actualizamos nuestro estado 'ready'
            if (!partida!!.jugador1ID.equals("0") && !partida!!.jugador2ID.equals("0")) {
                // Encontrado rival, desactivamos progressbar
                progressBar!!.setVisibility(View.INVISIBLE)
                rivalEncontrado = true
                // Seteamos el botón del corazón dependiendo si es amigo o no
                favoritoAdd!!.setVisibility(View.VISIBLE)
                if (jugador!!.numeroJugador === 1) {
                    if (jugador!!.favoritosID!!.contains(partida!!.jugador2ID)) {
                        favoritoAdd!!.setImageResource(R.drawable.corazonrojo)
                    }
                } else {
                    if (jugador!!.favoritosID!!.contains(partida!!.jugador1ID)) {
                        favoritoAdd!!.setImageResource(R.drawable.corazonrojo)
                    }
                }


                // Y descargamos imagen
                if (jugador!!.numeroJugador === 1) {
                    UtilsFirebase.descargarImagenFirebaseView(
                        applicationContext,
                        partida!!.jugador2ID,
                        avatarRival!!
                    )
                } else {
                    UtilsFirebase.descargarImagenFirebaseView(
                        applicationContext,
                        partida!!.jugador1ID,
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
            if (jugador!!.numeroJugador === 1) {
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
                Log.d(Constantes.TAG, "Numero jugador antes intent: " + jugador!!.numeroJugador)
                userRef!!.setValue(jugador)

                // Establecer la partida como que ha lanzado el dialogo juego online, para
                // diferenciar en el onStop y borrar la sala si llega porque ha cerrado la aplicación
                partida!!.jugando = true

                // Los 2 estamos listos. Lanzar Intent de juego
                val jugar = Intent(jugarOnline!!.getContext(), JuegoOnlineActivity::class.java)
                jugar.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                jugar.putExtra(Constantes.PARTIDA, partida!!.partidaID)
                when (jugador!!.numeroJugador) {
                    1 -> jugar.putExtra(Constantes.RIVALIDONLINE, partida!!.jugador2ID)
                    2 -> jugar.putExtra(Constantes.RIVALIDONLINE, partida!!.jugador1ID)
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


    // ------------------------------------------------------------------------------------------------------------------
    // Cuando pulsamos el botón de Jugar Online hacemos esto ...
    fun jugarOnline(view: View?) {

        // Tenemos un listener que apunte a las salas siempre escuchando
        // Creamos la sala cuando el jugador da al botón de jugar online
        // O si hay otra persona esperando rival (Ya hay sala creada con hueco libre), lo ocupamos
        //Subimos nuestro avatar a Firebase (Aquí es seguro que tenemos internet)
        SharedPrefs.saveJugadorPrefs(applicationContext, jugador!!)
        UtilsFirebase.subirImagenFirebase(
            mAuth!!.currentUser!!.uid,
            Utilidades.recuperarImagenMemoriaInterna(applicationContext, jugador!!.jugadorId)!!
        )
        jugarOnline!!.show()

        // Limitar a 20 partidas simultáneas
        if (partidas!!.size > 20) {
            Toast.makeText(this@MainActivity, R.string.sin_salas, Toast.LENGTH_LONG).show()
        } else {
            // Si no hay mas jugadores esperando partida, lo avisamos
            // Y creamos una sala con un hueco disponible
            if (partidas!!.size == 0) {
                Toast.makeText(this@MainActivity, R.string.noplayers, Toast.LENGTH_LONG).show()
                // Creamos partida con hueco vacío
                partida = Partida("SALA" + System.currentTimeMillis(), 0, jugador!!.jugadorId, "0", Tablero(12), 12)
                // Añadimos a Firebase la nueva partida
                partidasRef!!.child(partida!!.partidaID!!).setValue(partida)
                // En nuestro jugador indicamos que tenemos partida
                jugador!!.partida = (partida!!.partidaID)
                jugador!!.numeroJugador = (1)
            } else {
                // Hay jugadores disponibles online, ocupamos un hueco libre en la primera sala que encontremos
                // Seleccionamos una partida y llenamos el hueco disponible con nuestro id (La primera disponible)
                partida = partidas!!.get(0)
                // Mirar si el hueco disponible es el 1 o el 2
                if (partida!!.jugador1ID.equals("0")) {
                    partida!!.jugador1ID = (jugador!!.jugadorId)
                    jugador!!.numeroJugador = (1)
                } else {
                    partida!!.jugador2ID = (jugador!!.jugadorId)
                    jugador!!.numeroJugador = (2)
                }
                // Actualizamos a Firebase con la nueva partida
                partidasRef!!.child(partida!!.partidaID!!).setValue(partida)
                // En nuestro jugador indicamos que tenemos partida
                jugador!!.partida = (partida!!.partidaID)
            }
        }
    }

    private fun dialogoJuegoOnline() {

        // Dialog jugar online
        jugarOnline = Dialog(this)
        jugarOnline!!.setContentView(R.layout.dialog_jugar)
        jugarOnline!!.setTitle(R.string.jugar_online)
        rivalReady = jugarOnline!!.findViewById(R.id.estadoRivalTV)
        jugadorReady = jugarOnline!!.findViewById(R.id.estadoJugadorTV)
        readyJugadorIMG = jugarOnline!!.findViewById(R.id.imageReadyJugadorTV)
        readyRivalIMG = jugarOnline!!.findViewById(R.id.imageReadyRivalIV)
        mensajeEstado = jugarOnline!!.findViewById(R.id.mensajeEstadoTV)
        progressBar = jugarOnline!!.findViewById(R.id.progressBar2)
        avatarRival = jugarOnline!!.findViewById(R.id.rivalImageIV)
        favoritoAdd = jugarOnline!!.findViewById(R.id.dialog_favoritosBTN)
        readyBTN = jugarOnline!!.findViewById(R.id.readyBTN)

        // Inicializamos vistas
        mensajeEstado!!.setText(R.string.buscando)
        jugadorReady!!.setText(R.string.jugador)
        readyJugadorIMG!!.setImageResource(R.drawable.update)
        readyJugadorIMG!!.setBackgroundColor(resources.getColor(R.color.rojo))
        rivalReady!!.setText(R.string.rival)
        readyRivalIMG!!.setImageResource(R.drawable.update)
        readyRivalIMG!!.setBackgroundColor(resources.getColor(R.color.rojo))

        //  Controlar si el usuario pulsa back en el dispositivo:
        //  Actualizar Firebase para notificar los cambios y cerrar el diálogo
        jugarOnline!!.setOnCancelListener(DialogInterface.OnCancelListener {
            Log.d(Constantes.TAG, "Cancelado Dialog")
            if (partida != null) {
                // borramos nuestro id de jugador de  la partida y actualizamos Firebase
                if (jugador!!.numeroJugador === 1) {
                    partida!!.jugador1ID = ("0")
                    partida!!.jugador1Ready = (false)
                    jugador!!.partida = (null)
                } else {
                    partida!!.jugador2ID = ("0")
                    partida!!.jugador2Ready = (false)
                    jugador!!.partida = (null)
                }
                partida!!.jugando = (false)
                partidasRef!!.child(partida!!.partidaID!!).setValue(partida)
            }
            limpiarSala()
            jugarOnline!!.dismiss()
        })


        // Si hacemos click en el botón 'Preparado' notificamos a Firebase,
        // para que salte el evento correspondiente
        readyBTN!!.setOnClickListener(View.OnClickListener {
            when (jugador!!.numeroJugador) {
                1 -> partida!!.jugador1Ready = (true)
                2 -> partida!!.jugador2Ready = (true)
                else -> Log.d(Constantes.TAG, "Error, el jugador no tiene asignado número")
            }
            partidasRef!!.child(partida!!.partidaID!!).setValue(partida)
        })
        favoritoAdd!!.setOnClickListener(View.OnClickListener {
            if (rivalEncontrado) {
                amigosSwitch()
            }
        })
    }

    private fun amigosSwitch() {
        Log.d(Constantes.TAG, "Favoritos")
        if (jugador!!.favoritosID == null) jugador!!.favoritosID = (ArrayList())
        if (jugador!!.numeroJugador === 1) {
            // Si el rival encontrado ya lo teníamos como favorito, lo borramos
            if (jugador!!.favoritosID!!.contains(partida!!.jugador2ID!!)) {
                jugador!!.favoritosID!!.remove(partida!!.jugador2ID)
                Toast.makeText(this@MainActivity, R.string.amigo_del, Toast.LENGTH_LONG).show()
                favoritoAdd!!.setImageResource(R.drawable.corazon)
            } else {
                // Si no era nuestro amigo, lo añadimos
                jugador!!.favoritosID!!.add(partida!!.jugador2ID!!)
                Toast.makeText(this@MainActivity, R.string.amigo_add, Toast.LENGTH_LONG).show()
                favoritoAdd!!.setImageResource(R.drawable.corazonrojo)
            }
        } else {
            // Si el rival encontrado ya lo teníamos como favorito, lo borramos
            if (jugador!!.favoritosID!!.contains(partida!!.jugador1ID!!)) {
                jugador!!.favoritosID!!.remove(partida!!.jugador1ID!!)
                Toast.makeText(this@MainActivity, R.string.amigo_del, Toast.LENGTH_LONG).show()
                favoritoAdd!!.setImageResource(R.drawable.corazon)
            } else {
                // Si no era nuestro amigo, lo añadimos
                jugador!!.favoritosID!!.add(partida!!.jugador1ID!!)
                Toast.makeText(this@MainActivity, R.string.amigo_add, Toast.LENGTH_LONG).show()
                favoritoAdd!!.setImageResource(R.drawable.corazonrojo)
            }
        }
    }

    // ------------------------------------------------------------------------------------------------------------------

    // Controlar que si cerramos la aplicación y el jugador tiene
    //  asignado una sala, borrarlo de Firebase para que no se quede pillada la sala

    // ------------------------------------------------------------------------------------------------------------------
    // Controlar que si cerramos la aplicación y el jugador tiene
    //  asignado una sala, borrarlo de Firebase para que no se quede pillada la sala
    override fun onStop() {
        if (partida != null) {
            if (!partida!!.isJugando()) {
                partidasRef!!.child(partida!!.partidaID!!).removeValue()
            }
        }
        jugarOnline!!.dismiss()
        mediaPlayer!!.stop()
        super.onStop()
    }


    //
    // Aquí lanzamos el juego contra el ordenador (Móvil en este caso)
    //
    fun jugar(view: View?) {
        if (nickET!!.text != null) {
            var nickName = nickET!!.text.toString()
            if (Utilidades.eliminarPalabrotas(nickName)) {
                Toast.makeText(applicationContext, getString(R.string.palabrota), Toast.LENGTH_LONG)
                    .show()
                nickET!!.setText(getString(R.string.jugador))
                nickName = getString(R.string.jugador)
            }
            jugador!!.nickname = (nickName)
        }
        SharedPrefs.saveJugadorPrefs(applicationContext, jugador!!)
        val intentvscom = Intent(this, JuegoVsComActivity::class.java)
        intentvscom.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        // animacionTitulo.cancel(true);
        animacionTimer!!.cancel()
        mediaPlayer!!.stop()
        // finish();
        Sonidos.play(Sonidos.Companion.Efectos.GANAR)
        startActivity(intentvscom)
    }

    fun borrarJugadorSalaFirebase() {
        // Si el jugador ya tenía partida asignada, la borramos de Firebase
        if (!jugador!!.partida.equals("0")) {
            if (jugador!!.numeroJugador === 1) {
                mDatabase!!.child("PARTIDAS").child(jugador!!.partida!!).child("jugador1ID")
                    .setValue("0")
                mDatabase!!.child("PARTIDAS").child(jugador!!.partida!!).child("jugador1Ready")
                    .setValue(false)
            } else {
                mDatabase!!.child("PARTIDAS").child(jugador!!.partida!!).child("jugador2ID")
                    .setValue("0")
                mDatabase!!.child("PARTIDAS").child(jugador!!.partida!!).child("jugador2Ready")
                    .setValue(false)
            }
        }
    }


    fun personalizarAvatar(view: View?) {
        val popup = PopupMenu(this, view)
        //Inflating the Popup using xml file
        popup.menuInflater.inflate(R.menu.personaliza_avatar_menu, popup.menu)

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.foto_camara -> {
                    tomarFoto()
                    return@OnMenuItemClickListener true
                }

                R.id.foto_galeria -> {
                    seleccionarFoto()
                    return@OnMenuItemClickListener true
                }
            }
            true
        })
        popup.show() //showing popup menu
    }


    fun crearSalas(view: View?) {
        easterEgg++
        Sonidos.play(Sonidos.Companion.Efectos.TICK)
        if (easterEgg == 10) {
            easterEgg = 0
            Sonidos.play(Sonidos.Companion.Efectos.MAGIA)
            palitrokesIV!!.setImageDrawable(null)
            palitrokesIV!!.setImageResource(R.drawable.pic149)
            val rotate = RotateAnimation(
                0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f
            )
            rotate.duration = 2000
            rotate.repeatCount = 0
            nombreIV!!.startAnimation(rotate)
        }

        //  resetearRecords();
        /*
        // Crear Salas en Firebase
        for (int n = 0; n < 5; n++) {
            mDatabase.child("PARTIDAS").child("Sala " + n).setValue(new Partida("Sala " + n, n, "0", "0", new Tablero(0), 0));
        }
*/
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Log.d("MIAPP", "ME ha concecido los permisos")
        } else {
            Log.d("MIAPP", "NO ME ha concecido los permisos")
            Toast.makeText(this, R.string.mensaje_permisos, Toast.LENGTH_SHORT).show()
            permisosOK = false
        }
    }


    fun tomarFoto() {
        Log.d("MIAPP", "Quiere hacer una foto")
        val intent_foto = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photo_uri = Utilidades.crearFicheroImagen()
        intent_foto.putExtra(MediaStore.EXTRA_OUTPUT, photo_uri)
        Utilidades.desactivarModoEstricto()
        // animacionTitulo.cancel(true);
        animacionTimer!!.cancel()
        mediaPlayer!!.stop()
        startActivityForResult(intent_foto, Constantes.CODIGO_PETICION_HACER_FOTO)
    }

    fun seleccionarFoto() {
        Log.d("MIAPP", "Quiere seleccionar una foto")
        // animacionTitulo.cancel(true);
        animacionTimer!!.cancel()
        val intent_pide_foto = Intent()
        //intent_pide_foto.setAction(Intent.ACTION_PICK);//seteo la acción para galeria
        intent_pide_foto.action = Intent.ACTION_GET_CONTENT //seteo la acción
        intent_pide_foto.type = "image/*" //tipo mime
        mediaPlayer!!.stop()
        startActivityForResult(intent_pide_foto, Constantes.CODIGO_PETICION_SELECCIONAR_FOTO)
    }

    private fun setearImagenDesdeArchivo(resultado: Int, data: Intent) {
        when (resultado) {
            RESULT_OK -> {
                Log.d("MIAPP", "La foto ha sido seleccionada")
                photo_uri = data.data //obtenemos la uri de la foto seleccionada
                Log.d(Constantes.TAG, photo_uri!!.path!!)
                var imageStream: InputStream? = null
                try {
                    imageStream = contentResolver.openInputStream(photo_uri!!)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                var selectedImage = BitmapFactory.decodeStream(imageStream)
                selectedImage = Utilidades.getResizedBitmap(
                    selectedImage!!,
                    128
                ) // 400 is for example, replace with desired size
                avatarJugador!!.setImageBitmap(selectedImage)


                // De paso guardamos los datos del jugador (Nickname, id, victorias en el Shared Preferences)
                if (jugador == null) {
                    jugador = Jugador("","")
                }
                if (nickET!!.text.toString() != "") {
                    var nickName = nickET!!.text.toString()
                    if (Utilidades.eliminarPalabrotas(nickName)) {
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.palabrota),
                            Toast.LENGTH_LONG
                        ).show()
                        nickET!!.setText(getString(R.string.jugador))
                        nickName = getString(R.string.jugador)
                    }
                    jugador!!.nickname = (nickName)
                }
                SharedPrefs.saveJugadorPrefs(applicationContext, jugador!!)

                // Guardamos una copia del archivo en el dispositivo para utilizarlo mas tarde
                Utilidades.guardarImagenMemoriaInterna(
                    applicationContext,
                    jugador!!.jugadorId!!, Utilidades.bitmapToArrayBytes(
                        selectedImage!!
                    )
                )
            }

            RESULT_CANCELED -> Log.d("MIAPP", "La foto NO ha sido seleccionada canceló")
        }
    }

    private fun setearImagenDesdeCamara(resultado: Int, intent: Intent) {
        when (resultado) {
            RESULT_OK -> {
                Log.d("MIAPP", "Tiró la foto bien")
                var imageStream: InputStream? = null
                try {
                    imageStream = contentResolver.openInputStream(photo_uri!!)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }


                // De paso guardamos los datos del jugador (Nickname, id, victorias en el Shared Preferences)
                if (jugador == null) {
                    jugador = Jugador("","")
                }
                if (nickET!!.text.toString() != "") {
                    var nickName = nickET!!.text.toString()
                    if (Utilidades.eliminarPalabrotas(nickName)) {
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.palabrota),
                            Toast.LENGTH_LONG
                        ).show()
                        nickET!!.setText(getString(R.string.jugador))
                        nickName = getString(R.string.jugador)
                    }
                    jugador!!.nickname = (nickName)
                }
                SharedPrefs.saveJugadorPrefs(applicationContext, jugador!!)
                var selectedImage = BitmapFactory.decodeStream(imageStream)
                selectedImage = Utilidades.getResizedBitmap(
                    selectedImage!!,
                    128
                ) // 400 is for example, replace with desired size
                avatarJugador!!.setImageBitmap(selectedImage)

                // Guardamos una copia del archivo en el dispositivo para utilizarlo mas tarde
                Utilidades.guardarImagenMemoriaInterna(
                    applicationContext,
                    jugador!!.jugadorId!!, Utilidades.bitmapToArrayBytes(
                        selectedImage!!
                    )
                )


                // Actualizamos la galería
                sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, photo_uri))
            }

            RESULT_CANCELED -> Log.d("MIAPP", "Canceló la foto")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constantes.CODIGO_PETICION_SELECCIONAR_FOTO) {
            setearImagenDesdeArchivo(resultCode, data!!)
        } else if (requestCode == Constantes.CODIGO_PETICION_HACER_FOTO) {
            setearImagenDesdeCamara(resultCode, data!!)
        }
    }



    private fun resetearRecords() {
        // Crear Records en Firebase
        for (n in 0..9) {
            mDatabase!!.child("RECORDS").child("" + n).setValue(
                Records(
                    "adfadfadfasdfadf",
                    "Jugador $n", 0, n
                )
            )
        }
    }

    private fun limpiarSala() {
        if (partida!!.jugador2ID.equals("0") && partida!!.jugador1ID.equals("0")) {
            partidasRef!!.child(partida!!.partidaID!!).removeValue()
        }
    }


    private fun pausa(tiempo: Int) {
        // Dejamos una pausa para que se actualice la sala
        try {
            Thread.sleep(tiempo.toLong())
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }


    private fun recuperarDatosSharedPreferences() {
        // Recuperamos datos del Shared Preferences
        // Cargamos datos del jugador
        jugador = SharedPrefs.getJugadorPrefs(applicationContext)
        if (jugador!!.isFirstRun()) {
            // La primera vez que instalamos la aplicación lanzamos la actividad de Info
            // Para explicar el juego
            Log.d(Constantes.TAG, "Lanzar info")
            jugador!!.firstRun = false
            SharedPrefs.saveJugadorPrefs(applicationContext, jugador!!)
            //            Utilidades.guardarImagenMemoriaInterna(getApplicationContext(), jugador.jugadorId, Utilidades.bitmapToArrayBytes());
            //  animacionTitulo.cancel(true);
            //animacionTimer!!.cancel()
            val infointent = Intent(applicationContext, InfoActivity::class.java)
            //mediaPlayer.stop();
            //    finish();
            startActivity(infointent)
        }

        // Seteamos la imagen del avatar con el archivo guardado localmente en el dispositivo
        // Este archivo se actualiza cada vez que lo personalizamos con una imagen de la galería o la cámara
        val recuperaImagen = Utilidades.recuperarImagenMemoriaInterna(
            applicationContext, jugador!!.jugadorId
        )
        if (recuperaImagen != null) {
            avatarJugador!!.setImageBitmap(recuperaImagen)
        } else {
            avatarJugador!!.setImageResource(R.drawable.picture)
        }
        // Mostramos el nick del jugador y las victorias
        nickET!!.setText(jugador!!.nickname)
        victoriasTV!!.text = getString(R.string.victorias2) + jugador!!.victorias

        // Cargamos records y los mostramos
        records = SharedPrefs.getRecordsPrefs(applicationContext)
        adapter = RecordsAdapter(applicationContext, records!!)
        recordsRecycler!!.adapter = adapter
        adapter!!.notifyDataSetChanged()
    }


    override fun onPause() {
        mediaPlayer!!.stop()
        super.onPause()
        //   animacionTitulo.cancel(true);

        //    finish();
    }

    override fun onDestroy() {
        avatarJugador!!.background = null
        avatarJugador!!.setImageDrawable(null)
        //        palitrokesIV.setImageDrawable(null);
        mediaPlayer = null
        System.gc()
        super.onDestroy()
    }

    override fun onResume() {
        Log.d(Constantes.TAG, "On resume")
        recuperarDatosSharedPreferences()
        // Iniciar música
        bgm()
        mediaPlayer!!.start()
        animacionTimer!!.start()
        signIn()
        super.onResume()
    }


    override fun onBackPressed() {
        if (UtilityNetwork.isWifiAvailable(this) || UtilityNetwork.isNetworkAvailable(this)) {
            if (jugador!!.partida != null) {
                partidasRef!!.child(partida!!.partidaID!!).removeValue()
            }
        }

        //  changeImage.cancel();
        mediaPlayer!!.stop()
        finish()
        // super.onBackPressed();
    }


    fun infoButton(view: View?) {
        Log.d(Constantes.TAG, "Tocado info")
        //  animacionTitulo.cancel(true);
        animacionTimer!!.cancel()
        val infointent = Intent(applicationContext, InfoActivity::class.java)
        mediaPlayer!!.stop()
        //  finish();
        startActivity(infointent)
    }


    fun animacionPalitrokes() {
        // Cambiar la imagen del personaje cada tiempo en un asynctask
        palitrokesIV = findViewById(R.id.palitrokesIV)
        animacionTimer = object : CountDownTimer(60000, 3000) {
            override fun onTick(millisUntilFinished: Long) {
                val rnd = Random()
                val name = "pic" + (rnd.nextInt(116) + 34)
                val resource = resources.getIdentifier(name, "drawable", "com.tionim.game")
                palitrokesIV!!.setImageDrawable(null)
                palitrokesIV!!.setImageResource(resource)
            }

            override fun onFinish() {
                start()
            }
        }
        animacionTimer!!.start()


        /*
        animacionTitulo = new AnimacionTitulo();
        animacionTitulo.recuperarImageView(getApplicationContext(), palitrokesIV);
        animacionTitulo.executeOnExecutor(executorService);
*/
    }

    fun favoritosToggle(view: View?) {
        if (soloFavoritos) {
            soloFavoritos = false
            favoritosBTN!!.setImageResource(R.drawable.corazon)
        } else {
            soloFavoritos = true
            favoritosBTN!!.setImageResource(R.drawable.corazonrojo)
        }

        // Refrescamos la base de datos si tenemos internet
        if (UtilityNetwork.isNetworkAvailable(this) || UtilityNetwork.isWifiAvailable(this)) {
            partidaActualizacion!!.partidaID = (System.currentTimeMillis().toString() + "")
            partidaActualizacion!!.jugador1ID = ("SALA ACTUALIZACIONES")
            partidaActualizacion!!.jugador2ID = ("SALA ACTUALIZACIONES")
            partidasRef!!.child("SALA_ACTUALIZACIONES").setValue(partidaActualizacion)
        }
    }


    private fun cargarRecords() {
        // Cargar los records y mostrarlos en el Recycler
        recordsRef = FirebaseDatabase.getInstance("https://tionim-8fedb-default-rtdb.europe-west1.firebasedatabase.app").getReference("RECORDS")
        recordsRef!!.addListenerForSingleValueEvent( object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var n = 0
                records!!.removeAll(records!!)
                for (snapshot in dataSnapshot.children) {
                    val recordTmp = snapshot.getValue(Records::class.java)
                    records!!.add(recordTmp!!)
                  //  Log.d("Depurar", recordTmp!!.nickname.toString())
                    // Descargamos imagen de Firebase y la guardamos en el dispositivo para usarla mas tarde
                    Utilidades.eliminarArchivo(applicationContext, "RECORDIMG$n.jpg")
                    UtilsFirebase.descargarImagenFirebaseYGuardarla(applicationContext, recordTmp!!.idJugador)
                    n++
                }
                adapter!!.notifyDataSetChanged()
                // Guardamos los records actualizados de Firebase en el Shared Preferences
                SharedPrefs.saveRecordsPrefs(applicationContext, records!!)
            }
            override fun onCancelled(@NonNull databaseError: DatabaseError) {}
        })
    }

    private fun bgm() {
        val rnd = Random()
        when (rnd.nextInt(3)) {
            0 -> mediaPlayer = MediaPlayer.create(this, R.raw.cutebgm)
            1 -> mediaPlayer = MediaPlayer.create(this, R.raw.sunny)
            2 -> mediaPlayer = MediaPlayer.create(this, R.raw.ukelele)
        }
    }
}