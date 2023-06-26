package com.tionim.game.Modelos

class Partida (
    partidaID: String?,
    numeroSala: Int,
    jugador1ID: String?,
    jugador2ID: String?,
    tablero: Tablero?,
    level: Int
) {

     var partidaID: String? = null
     var jugador1ID: String? = null
     var jugador2ID: String? = null
     var jugador1Ready = false
     var jugador2Ready = false
     var jugando = false
     var turno = 0
     var tablero: Tablero? = null
     var ganador = 0
     var numeroSala = 0
     var level = 0



    fun Partida() {
        partidaID = null
        jugador1ID = null
        jugador2ID = null
        tablero = Tablero(1)
        turno = 1
        ganador = 0
        jugando = false
        jugador1Ready = false
        jugador2Ready = false
    }



    fun levelUp() {
        level++
    }

    fun isJugador1Ready(): Boolean {
        return jugador1Ready
    }


    fun isJugador2Ready(): Boolean {
        return jugador2Ready
    }


    fun isJugando(): Boolean {
        return jugando
    }


    fun turnoToggle() {
        if (turno == 2) {
            turno = 1
        } else {
            turno = 2
        }
    }
}