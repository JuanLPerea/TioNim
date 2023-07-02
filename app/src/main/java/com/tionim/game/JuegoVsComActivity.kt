package com.tionim.game

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tionim.game.Modelos.JugadaCom
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
import java.util.Random

class JuegoVsComActivity : AppCompatActivity() {


     var linearBase: LinearLayout? = null
     var partida: Partida? = null
     var jugador: Jugador? = null
     var avatarJ1: ImageView? = null
     var avatarJ2:android.widget.ImageView? = null
     var nickJ1: TextView? = null
     var nickJ2:TextView? = null
     var winsJ1:TextView? = null
     var winsJ2:TextView? = null
     var tiempoJ1:TextView? = null
     var tiempoJ2:TextView? = null
     var level:TextView? = null
     var okJ1: ImageButton? = null
     var okJ2:ImageButton? = null
     lateinit var colores: IntArray
     var cronometro1: CountDownTimer? = null
     var cronometro2: CountDownTimer? = null
     var jugadaComTimer: CountDownTimer? = null
     var finTiempo = false
     var palosQuitados = 0
     var abandono = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_juego)
//        supportActionBar!!.hide()

        // recuperamos las Views
        linearBase = findViewById(R.id.tableroLL)
        avatarJ1 = findViewById(R.id.avatarJ1_TV)
        avatarJ2 = findViewById(R.id.avatarJ2_TV)
        nickJ1 = findViewById(R.id.nicknameJ1_TV)
        nickJ2 = findViewById(R.id.nicknameJ2_TV)
        winsJ1 = findViewById(R.id.vitoriasJ1_TV)
        winsJ2 = findViewById(R.id.vitoriasJ2_TV)
        tiempoJ1 = findViewById(R.id.tiempoJ1_TV)
        tiempoJ2 = findViewById(R.id.tiempoJ2_TV)
        okJ1 = findViewById(R.id.ok_J1_BTN)
        okJ2 = findViewById(R.id.ok_J2_BTN)
        level = findViewById(R.id.levelTV)

        okJ1!!.setOnClickListener(View.OnClickListener { okJugada(null) })
        okJ2!!.setOnClickListener(View.OnClickListener { okJugada(null) })

        avatarJ2!!.setImageResource(R.drawable.pic34)
        finTiempo = false

        // Recuperamos los datos del jugador de Shared Preferences
        // Seteamos el jugador
        jugador = Jugador("","")
        jugador = SharedPrefs.getJugadorPrefs(applicationContext)
        jugador!!.numeroJugador = (1)
        actualizarVistaJugador()
        nickJ2!!.setText(R.string.nombreprota)
        winsJ2!!.setText(R.string.victorias8)


        // Cronometro para el jugador 1
        cronometro1 =
            object : CountDownTimer(Constantes.TIEMPOTURNO.toLong(),
                Constantes.TIEMPOACTUALIZACRONO.toLong()
            ) {
                override fun onTick(millisUntilFinished: Long) {
                    tiempoJ1!!.setText((millisUntilFinished / 1000).toString() + "")
                }

                override fun onFinish() {
                    finTiempo = true
                    cronometro1!!.cancel()
                    okJugada(null)
                }
            }

        // Cronometro para el jugador 2
        cronometro2 =
            object : CountDownTimer(Constantes.TIEMPOTURNO.toLong(),
                Constantes.TIEMPOACTUALIZACRONO.toLong()
            ) {
                override fun onTick(millisUntilFinished: Long) {
                    tiempoJ2!!.setText((millisUntilFinished / 1000).toString() + "")
                }

                override fun onFinish() {
                    finTiempo = true
                    cronometro2!!.cancel()
                    okJugada(null)
                }
            }


        // Creamos una nueva partida
        // El turno será aleatorio para que no haya ventajas
        partida = Partida("",0,"","",Tablero(0),0)
        // Asegurarnos que en la primera jugada, el jugador tenga las de ganar siempre
        var nuevoTablero: Tablero?
        do {
            nuevoTablero = Tablero(partida!!.level)
        } while (!JugadaCom().esGanador(nuevoTablero!!))
        partida!!.tablero = (nuevoTablero)
        partida!!.turno = (1)
        partida!!.jugador1ID = (jugador!!.jugadorId)
        inicializarColores()
        mostrarLevel()
    }

    private fun actualizarVistaJugador() {
        avatarJ1!!.setImageBitmap(
            Utilidades.recuperarImagenMemoriaInterna(
                applicationContext,
                jugador!!.jugadorId
            )
        )
        winsJ1!!.setText(getString(R.string.victorias2) + jugador!!.victorias)
        nickJ1!!.setText(jugador!!.nickname)
    }

    private fun visualizarTablero(tablero: Tablero) {
        linearBase!!.removeAllViews()
        val pesoMonton: Int = 100 / tablero.numMontones

        // Calcular el alto de los palos en función del tamaño de la pantalla
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        //int width = metrics.widthPixels; // ancho absoluto en pixels
        val height = metrics.heightPixels // alto absoluto en pixels


        // Miramos la cantidad de palos mas alta de los montones
        val maxpalos: Int = partida!!.tablero!!.maxPalosMontones()

        // Hacemos el alto del ImageView se reparta entre el espacio disponible
        val alturaPalo = height / 2 / (maxpalos + 1)
        for (montonTMP in tablero!!.montones!!) {
            val montonLL = LinearLayout(this)
            montonLL.layoutParams =
                LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    pesoMonton.toFloat()
                )
            montonLL.orientation = LinearLayout.VERTICAL
            montonLL.id = newId()
            montonLL.setPadding(10, 0, 10, 0)
            montonLL.setBackgroundColor(colores[montonTMP.numeroMonton])

//            int numeroPalos = montonTMP.palos!!.size();

            //     Log.d(Constantes.TAG, "palos monton " + numeroPalos + " altura " + alturaPalo);
            for (paloTmp in montonTMP.palos!!) {
                val newImageView = ImageView(this)
                val params =
                    LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, alturaPalo)
                params.setMargins(5, 0, 5, 5)
                newImageView.layoutParams = params
                if (paloTmp.seleccionado) {
                    newImageView.alpha = 0.5f
                } else {
                    newImageView.alpha = 1f
                }
                newImageView.setImageResource(R.drawable.palo)
                newImageView.id = newId()
                newImageView.tag = montonTMP.numeroMonton.toString() + "#" + paloTmp.numeroPalo
                newImageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
                newImageView.setOnClickListener { seleccionarPalo(newImageView) }
                montonLL.addView(newImageView)
            }
            linearBase!!.addView(montonLL)
        }
    }


    private fun seleccionarPalo(imageView: ImageView) {
      //  Log.d("Depurar", "Seleccionado palo " + imageView.id)

        // Si es nuestro actualizarViewsCambioTurno podemos seleccionar cosas en la pantalla...
        if (partida!!.turno == jugador!!.numeroJugador) {
            val montonTocado =
                imageView.tag.toString().split("#".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()[0].toInt()
            val paloTocado =
                imageView.tag.toString().split("#".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()[1].toInt()

              Log.d("Depurar", "Tag: " + imageView.tag +" - Palo tocado: " + montonTocado + "-" + paloTocado);

            // Si el palo que hemos tocado está en el mismo montón o no hay ningún montón seleccionado (-1)
            // Cambiamos el estado del palo entre seleccionado o no seleccionado
            if (partida!!.tablero!!.montonSeleccionado == partida!!.tablero!!.montones!!.get(montonTocado).numeroMonton ||
                partida!!.tablero!!.montonSeleccionado == -1
            ) {
                if (partida!!.tablero!!.montones!!.get(montonTocado).palos!!.get(paloTocado)
                        .seleccionado
                ) {
                    partida!!.tablero!!.montones!!.get(montonTocado).palos!!.get(paloTocado)
                        .seleccionado = (false)
                    if (partida!!.tablero!!.montones!!.get(montonTocado)
                            .getPalosseleccionados() == 0
                    ) {
                        partida!!.tablero!!.montonSeleccionado = (-1)
                    }
                } else {
                    partida!!.tablero!!.montones!!.get(montonTocado).palos!!.get(paloTocado)
                        .seleccionado = (true)
                    partida!!.tablero!!.montones!!.get(montonTocado)
                        .numeroMonton = (montonTocado)
                    partida!!.tablero!!.montonSeleccionado = (montonTocado)
                }
                Sonidos.play(Sonidos.Companion.Efectos.TICK)
            }
        }
        visualizarTablero(partida!!.tablero!!)
    }

    private fun actualizarViewsCambioTurno() {
        // Si somos el jugador 1 (Estamos arriba)
        // El jugador 2 está abajo en la pantalla
        //
        when (partida!!.turno) {
            1 -> {
                if (jugador!!.numeroJugador == 1) {
                    // si somos el jugador 1
                    okJ1!!.visibility = View.VISIBLE //Botón del jugador 1 visible, puede jugar
                    okJ2!!.setVisibility(View.INVISIBLE) //Botón del jugador 2 invisible
                } else {
                    // si somos el jugador 2
                    okJ1!!.visibility =
                        View.INVISIBLE //Botón del jugador 1 invisible (No hay botón en ningun lado porque el turno es del otro)
                    okJ2!!.setVisibility(View.INVISIBLE) //Botón del jugador 2 invisible (No hay botón en ningun lado porque el turno es del otro)
                }
                // El tiempo estará visible en el jugador 1 que es el que tiene el crono
                tiempoJ1!!.setVisibility(View.VISIBLE) //Tiempo del jugador 1 visible
                tiempoJ2!!.setVisibility(View.INVISIBLE) //Tiempo del jugador 2 invisible
                // Seamos el jugador 1 o el 2 el crono lo tiene el jugador 1, porque es su turno
                cronometro1!!.start()
            }

            2 -> {
                // Es el turno del jugador 2
                if (jugador!!.numeroJugador == 1) {
                    // si somos el jugador 1
                    okJ1!!.visibility =
                        View.INVISIBLE //Botón del jugador 1 invisible (No hay botón en ningun lado porque el turno es del otro)
                    okJ2!!.setVisibility(View.INVISIBLE) //Botón del jugador 2 invisible (No hay botón en ningun lado porque el turno es del otro)
                } else {
                    // si somos el jugador 2
                    okJ1!!.visibility = View.INVISIBLE //Botón del jugador 1 invisible
                    okJ2!!.setVisibility(View.VISIBLE) //Botón del jugador 2 activado, podemos jugar
                }
                // El tiempo estará visible en el jugador 1 que es el que tiene el crono
                tiempoJ1!!.setVisibility(View.INVISIBLE) //Tiempo del jugador 1 INvisible
                tiempoJ2!!.setVisibility(View.VISIBLE) //Tiempo del jugador 2 invisible
                // Seamos el jugador 1 o el 2 el crono lo tiene el jugador 1, porque es su turno
                cronometro2!!.start()
            }
        }
    }


    fun okJugada(v: View?) {
        // No puede seleccionar todos los palos si solo queda 1 monton
        // Tiene que haber seleccionado al menos 1 palo
        if (partida!!.tablero!!.palosSeleccionadosTotal() != partida!!.tablero!!
                .palosTotales()
        ) {
            if (partida!!.tablero!!.palosSeleccionadosTotal() > 0) {
                finTiempo = false
                // Eliminamos los palos seleccionados del Tablero
                partida!!.tablero!!.eliminarSeleccionados()
                Log.d(Constantes.TAG, "Aceptar jugada")
                // comprobamos, si solo queda uno, hemos ganado!!
                if (partida!!.tablero!!.palosTotales() == 1) {
                    //    Toast.makeText(this, "¡¡Has Ganado!!", Toast.LENGTH_LONG).show();
                    avatarJ2!!.setImageResource(R.drawable.pic109)
                    Log.d(Constantes.TAG, getString(R.string.ganador) + partida!!.turno)
                    partida!!.ganador = (partida!!.turno)
                } else {
                    // Si aún quedan palos Cambiamos el turno y se lo pasamos al rival
                    Log.d(Constantes.TAG, "Fin de turno botón OK")
                }
                finTurno()
            } else {
                Toast.makeText(this, R.string.selecciona, Toast.LENGTH_LONG).show()
                if (finTiempo) {
                    Log.d(Constantes.TAG, "Ha terminado el tiempo sin seleccionar ningun palo")
                    abandono = true
                    partida!!.ganador = (2)
                    finTurno()
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.unpalo), Toast.LENGTH_LONG).show()
        }
        visualizarTablero(partida!!.tablero!!)
    }


    private fun finTurno() {
        Sonidos.play(Sonidos.Companion.Efectos.PLING)
        if (partida!!.ganador == 0) {
            partida!!.turnoToggle()
            actualizarViewsCambioTurno()
            if (partida!!.turno == 2) {
                Log.d(Constantes.TAG, "Turno del ordenador")
                //  Pasar el tablero a la Clase
                //  JugadaCom Nos devuelve un String con la
                //  jugada que va ha hacer el ordenador

                // Hacemos los cambios que sean
                val jugadaCom = JugadaCom()
                hacerJugadaCom(jugadaCom.jugadaCom(partida!!.tablero!!)!!)
            }
        } else {
            Log.d(Constantes.TAG, "Fin juego")
            finJuego()
        }
    }

    private fun hacerJugadaCom(jugadaCom: String) {
        cronometro1!!.cancel()
        finTiempo = false
        val montonEnJuego =
            jugadaCom.split("#".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0].toInt()
        val palosAQuitar =
            jugadaCom.split("#".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1].toInt()
        palosQuitados = 0
        partida!!.tablero!!.montonSeleccionado = (montonEnJuego)
        pausa(500)

        // Simulamos que se seleccionan los palos y se quitan
        // Haciendo una pausa de medio segundo entre palo y palo
        if (palosAQuitar == 1) {
            // Si solo es un palo lo que va a quitar no hacemos lo del crono
            cambiarImagenPalitrokes()
            partida!!.tablero!!.montones!!.get(montonEnJuego).palos!!.get(palosQuitados)
                .seleccionado = (true)
            Sonidos.play(Sonidos.Companion.Efectos.TICK)
            val pausaVerJugada: CountDownTimer = object : CountDownTimer(1000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    visualizarTablero(partida!!.tablero!!)
                }

                override fun onFinish() {
                    finJugadaCom()
                    cancel()
                }
            }
            pausaVerJugada.start()
        } else {
            // Ponemos un cronómetro que nos vale como si fuera un AsyncTask
            // Cada segundo seleccionamos un palo de los que nos hayan salido
            // en el cálculo de la jugada.
            //
            // Cuando acaba el cronómetro, quitamos los palos y lo mostramos
            // también cambiará el turno pasando al jugador
            //
            jugadaComTimer = object : CountDownTimer((1100 * palosAQuitar).toLong(), 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    // Seleccionamos un palo del montón
                    // Y lo visualizamos
                    if (palosQuitados < palosAQuitar) {
                        cambiarImagenPalitrokes()
                        partida!!.tablero!!.montones!!.get(montonEnJuego).palos!!
                            .get(palosQuitados).seleccionado = (true)
                        palosQuitados++
                        Sonidos.play(Sonidos.Companion.Efectos.TICK)
                        visualizarTablero(partida!!.tablero!!)
                    }
                }

                override fun onFinish() {
                    finJugadaCom()
                }
            }
            jugadaComTimer!!.start()
        }
    }

    private fun finJugadaCom() {
        // Paramos este crono
        if (jugadaComTimer != null) {
            jugadaComTimer!!.cancel()
        }
        cronometro2!!.cancel()


        // Eliminamos el palos seleccionados del Tablero
        partida!!.tablero!!.eliminarSeleccionados()
        Sonidos.play(Sonidos.Companion.Efectos.PLING)
        Log.d(Constantes.TAG, "Palos que quedan: " + partida!!.tablero!!.palosTotales())
        // Detectar si solo queda 1 palo, le queda al jugador y ...
        if (partida!!.tablero!!.palosTotales() == 1) {
            // Solo queda un palo. Ha ganado el ordenador
            partida!!.ganador = (2)
            finTurno()
        }
        pausa(500)

        // Cambiamos el turno y Actualizamos el tablero y los views
        visualizarTablero(partida!!.tablero!!)
        partida!!.turnoToggle()
        actualizarViewsCambioTurno()
    }

    private fun pausa(i: Int) {
        // Dejamos una pausa
        try {
            Thread.sleep(i.toLong())
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }


    private fun finJuego() {

        // Quitar los cronómetros
        cronometro1!!.cancel()
        cronometro2!!.cancel()
        //   Intent volverIntent = new Intent(this, MainActivity.class);
        //  volverIntent.putExtra("SALA_ANTERIOR", partida!!.getPartidaID());
        var resultado = ""
        if (abandono) {
            resultado = getString(R.string.abandono)
        }
        Log.d(Constantes.TAG, "Ganador: " + partida!!.ganador)
        if (partida!!.ganador == jugador!!.numeroJugador) {
            resultado += getString(R.string.win)
            Sonidos.play(Sonidos.Companion.Efectos.GANAR)
            siguienteNivel()
        } else {
            resultado += getString(R.string.resultado) + partida!!.level
            Sonidos.play(Sonidos.Companion.Efectos.PERDER)
            // Guardar record si hay internet, podemos mirar si hemos entrado en los records del juego
            // Si no hay internet, solo sumamos la victoria a nuestras estadísticas
            if (UtilityNetwork.isNetworkAvailable(this) || UtilityNetwork.isWifiAvailable(this)) {
                UtilsFirebase.guardarRecords(applicationContext, jugador!!, partida!!.level)
            }
            SharedPrefs.updateRecordsPrefs(
                applicationContext,
                Records(
                    jugador!!.jugadorId,
                    jugador!!.nickname,
                    jugador!!.victorias,
                    partida!!.level
                )
            )
            SharedPrefs.saveJugadorPrefs(applicationContext, jugador!!)
            finish()
            // Lanzamos el intent del MainActivity
            //  startActivity(volverIntent);
        }
        Toast.makeText(this, resultado, Toast.LENGTH_LONG).show()
        Log.d(Constantes.TAG, "Esperamos 1 segundo")
        // Dejamos una pausa para que se actualice la sala
        pausa(1000)
    }


    private fun newId(): Int {
        val r = Random()
        var resultado = -1
        do {
            resultado = r.nextInt(Int.MAX_VALUE)
        } while (findViewById<View>(resultado) != null)
        return resultado
    }

    override fun onBackPressed() {
        abandono = true
        finJuego()
    }


    override fun onPause() {
        super.onPause()
        if (partida!!.ganador == 0) {
            abandono = true
            finJuego()
        }
    }

    override fun onStop() {
        cronometro1!!.cancel()
        cronometro2!!.cancel()
        //  cronometro1 = null;
        //  cronometro2 = null;
        avatarJ2!!.setImageDrawable(null)
        avatarJ1!!.setImageDrawable(null)
        linearBase = null
        if (jugadaComTimer != null) {
            jugadaComTimer!!.cancel()
        }
        super.onStop()
    }


    private fun mostrarLevel() {
        linearBase!!.visibility = View.INVISIBLE
        level!!.setVisibility(View.VISIBLE)
        level!!.setText(getString(R.string.nivel) + (partida!!.level + 1))
        val levelCrono: CountDownTimer = object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                level!!.setVisibility(View.INVISIBLE)
                linearBase!!.visibility = View.VISIBLE
                visualizarTablero(partida!!.tablero!!)
                actualizarViewsCambioTurno()
                Sonidos.play(Sonidos.Companion.Efectos.UIIIIU)
            }
        }
        levelCrono.start()
    }

    private fun siguienteNivel() {
        partida!!.levelUp()

        // Asegurarnos que en la primera jugada, el jugador tenga las de ganar siempre
        var nuevoTablero: Tablero?
        do {
            nuevoTablero = Tablero(partida!!.level)
        } while (!JugadaCom().esGanador(nuevoTablero!!))
        partida!!.tablero = (nuevoTablero)
        partida!!.ganador = (0)
        partida!!.turno = (1)
        jugador!!.victorias = (1)
        inicializarColores()
        actualizarVistaJugador()
        mostrarLevel()
    }


    private fun inicializarColores() {
        // Inicializamos el array de colores aleatorios para el fondo de la pantalla
        colores = IntArray(6)
        val rnd = Random()
        for (n in colores.indices) {
            colores[n] = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        }
    }


    fun cambiarImagenPalitrokes() {
        val rnd = Random()
        val name = "pic" + (rnd.nextInt(116) + 34)
        val resource = resources.getIdentifier(name, "drawable", "com.game.palitrokes")
        avatarJ2!!.setImageResource(resource)
    }

    override fun onDestroy() {
        avatarJ1!!.setImageDrawable(null)
        avatarJ2!!.setImageDrawable(null)
        super.onDestroy()
    }

    override fun onResume() {
        if (partida!!.turno == 1) {
            cronometro1!!.start()
        } else {
            cronometro2!!.start()
        }
        super.onResume()
    }
}