package com.tionim.game.Modelos

import android.util.Log
import com.tionim.game.Utilidades.Constantes
import java.util.Random


class JugadaCom {

    private var montonesBinario: MutableList<String> = ArrayList()
    private var maxLargoBinario = 0
    private lateinit var sumaColumnas: IntArray

    fun jugadaCom(tablero: Tablero): String? {
        var jugadaSalida: String? = null

        // Posicion perdedora, todas las columnas pares
        // ---------------------------------------------------------------------------------------------
        if (!esGanador(tablero)) {
            Log.d(Constantes.TAG, "Posición perdedora")

            // No podemos hacer nada, jugamos al azar con la esperanza de que se equivoque el otro jugador
            val r = Random()
            val montonAleatorio: Int = r.nextInt(tablero.montones!!.size)
            var palosAleatorios: Int =
                r.nextInt(tablero.montones!!.get(montonAleatorio).palos!!.size)
            // Al menos que quite un palo siempre
            if (palosAleatorios == 0) palosAleatorios = 1
            jugadaSalida = "$montonAleatorio#$palosAleatorios"
            //  jugadaSalida = "0#1";
            Log.d(Constantes.TAG, "Jugada de salida: $jugadaSalida")
            Log.d(Constantes.TAG, "-----------------------------------------------------")
            return jugadaSalida
        } else {
            // Posicion ganadora, alguna columna es impar
            // ---------------------------------------------------------------------------------------------
            Log.d(Constantes.TAG, "Posición ganadora")


            // En la estrategia de que pierde el que se quede con el último palo:
            // Hay que comprobar que solo queda un monton con mas de 1 palo...
            var numeroDeMontonesConMasDeUnPalo = 0
            var montonConMasDeUnPalo = 0
            for (montonTmp in tablero.montones!!) {
                if (montonTmp.palos!!.size > 1) {
                    numeroDeMontonesConMasDeUnPalo++
                    montonConMasDeUnPalo = montonTmp.getNumeroMonton()
                }
            }
            Log.d(
                Constantes.TAG,
                "Quedan " + numeroDeMontonesConMasDeUnPalo + " Montones con mas de 1 palo. Total Montones: " + tablero.getNumMontones()
            )

            // Hay 3 casos:
            // 1) que quede solo un monton con 1 palo
            // 2) que queden todos los montones con 1 palo
            // 3) que queden mas de 1 monton con mas de 1 palo.....
            // -------------------------------------------------------------------------------------------------------------------
            if (numeroDeMontonesConMasDeUnPalo == 1) {
                // Si solo queda un monton con mas de 1 palo,
                // Si el numero de montones es par quitamos todos los palos menos unodel montón que tenga mas de 1 palo
                // Si el numero de montones es impar, quitamos todos los palos del montón que tenga mas de 1 palo
                Log.d(Constantes.TAG, "Queda solo un monton con mas de 1 palo ")
                jugadaSalida = if (tablero.montones!!.size % 2 == 0) {
                    montonConMasDeUnPalo.toString() + "#" + tablero.montones!!.get(
                        montonConMasDeUnPalo
                    ).palos!!.size
                } else {
                    montonConMasDeUnPalo.toString() + "#" + (tablero.montones!!.get(
                        montonConMasDeUnPalo
                    ).palos!!.size - 1)
                }
            } else if (numeroDeMontonesConMasDeUnPalo == 0) {
                // Si todos los montones tienen solo 1 palo
                // Quitamos cualquiera (en este caso palo que queda en el primer montón)
                Log.d(Constantes.TAG, "Todos los montones que quedan tienen solo 1 palo")
                jugadaSalida = "0#1"
            } else if (numeroDeMontonesConMasDeUnPalo > 1) {
                // En este caso usamos la estrategia analizando los montones
                //
                // Seleccionamos el monton que tenga una suma impar en las columnas
                Log.d(Constantes.TAG, "Quedan muchos palos, usamos estrategia")
                var columnaImpar = 0
                for (cnd in 0..maxLargoBinario) {
                    if (sumaColumnas[cnd] % 2 != 0) {
                        columnaImpar = cnd
                        break
                    }
                }
                Log.d(Constantes.TAG, "Columna Impar: $columnaImpar")

                // Nos quedamos con un monton que tenga un 1 en la posicion de la columnaImpar
                var numeroMontonATratar = -1
                for (cnd in montonesBinario.indices) {
                    val montonTmp = montonesBinario[cnd]
                    val buscarMonton = montonTmp[columnaImpar]
                    if (buscarMonton == '1') {
                        numeroMontonATratar = cnd
                    }
                }


                // Tratamos las columnas que eran impares para que sean pares
                // O si son enteramente son unos, quitamos todos los palos de ese montón
                val montonATratar = montonesBinario[numeroMontonATratar]
                var montonTratado = ""
                Log.d(
                    Constantes.TAG,
                    "Monton a tratar: $numeroMontonATratar - $montonATratar"
                )
                for (cnd in sumaColumnas.indices) {
                    // Sacar los caracteres uno a uno
                    val caracter = montonATratar[cnd]
                    // en las posiciones que eran impares cambiamos 0 por 1 y viceversa
                    montonTratado = if (sumaColumnas[cnd] % 2 != 0) {
                        if (caracter == '0') {
                            montonTratado + "1"
                        } else {
                            montonTratado + "0"
                        }
                    } else {
                        // Si la columna es par la dejamos como está
                        montonTratado + caracter
                    }
                }
                Log.d(Constantes.TAG, "Monton tratado: $montonTratado")

                // Si el resultado es 'unos', quitaremos todos los palos del montón elegido
                val todoUnos = montonTratado.indexOf("0")
                jugadaSalida = if (todoUnos == -1) {
                    numeroMontonATratar.toString() + "#" + tablero.montones!!.get(numeroMontonATratar)
                        .palos!!.size
                } else {
                    // Si el resultado tiene algún cero, Convertir monton tratado a decimal
                    // y habrá que restar del total de palos, los que tienen que quedar,
                    // que es el número que sale de tratar el montón elegido pasado a decimal
                    val numeroPalosOriginal: Int =
                        tablero.montones!!.get(numeroMontonATratar).palos!!.size
                    val numeroPalosAQuitar = montonTratado.toInt(2)
                    val numeroPalosSalida = numeroPalosOriginal - numeroPalosAQuitar
                    "$numeroMontonATratar#$numeroPalosSalida"
                }
            }
        }
        Log.d(Constantes.TAG, "Jugada de salida: $jugadaSalida")
        Log.d(Constantes.TAG, "-----------------------------------------------------")
        return jugadaSalida
    }


    fun decimalABinario(numero: Int): String {
        var numero = numero
        var binario = ""
        if (numero > 0) {
            while (numero > 0) {
                binario = if (numero % 2 == 0) {
                    "0$binario"
                } else {
                    "1$binario"
                }
                numero = numero / 2
            }
        } else if (numero == 0) {
            binario = "0"
        }
        return binario
    }

    fun esGanador(tablero: Tablero): Boolean {
        var esGanador = false

        // Analizar el tablero para saber si estamos en una posición ganadora o perdedora
        // ----------------------------------------------------------------------------------------------------


        // Convertir el número de palos de cada montón a binario y
        // Guardarlo en una lista de Strings
        for (montonTmp in tablero.montones!!) {
            val cadenaBinario = decimalABinario(montonTmp.palos!!.size)
            // Guardamos el largo máximo de las cadenas en formato binario
            if (cadenaBinario.length > maxLargoBinario) {
                maxLargoBinario = cadenaBinario.length - 1
            }
            montonesBinario.add(cadenaBinario)
            Log.d(Constantes.TAG, montonTmp.palos!!.size.toString() + "")
        }
        Log.d(Constantes.TAG, "Numero de columnas: $maxLargoBinario")

        // añadir ceros a la izquierda si es necesario
        val nuevoBinarioString: MutableList<String> = ArrayList()
        for (binarioString in montonesBinario) {
            var nuevoString = binarioString
            if (binarioString.length <= maxLargoBinario) {
                for (cnd in 0 until maxLargoBinario - binarioString.length + 1) {
                    nuevoString = "0$nuevoString"
                }
            }
            nuevoBinarioString.add(nuevoString)
        }
        montonesBinario = nuevoBinarioString
        for (binarioString in montonesBinario) {
            Log.d(Constantes.TAG, "Montones en binario: $binarioString")
        }


        // Sumar las columnas
        var numeroDePares = 0
        sumaColumnas = IntArray(maxLargoBinario + 1)
        for (posicion in 0..maxLargoBinario) {
            for (binarioString in montonesBinario) {
                if (binarioString.length - 1 - posicion >= 0) {
                    sumaColumnas[maxLargoBinario - posicion] += (binarioString[binarioString.length - posicion - 1].toString() + "").toInt()
                }
            }
        }
        for (cnd in 0..maxLargoBinario) {
            Log.d(Constantes.TAG, "Suma Columnas: " + sumaColumnas[cnd])
            if (sumaColumnas[cnd] % 2 == 0) numeroDePares++
        }
        Log.d(Constantes.TAG, "Numero de pares: $numeroDePares")
        esGanador = if (numeroDePares == maxLargoBinario + 1) {
            false
        } else {
            true
        }
        return esGanador
    }


}