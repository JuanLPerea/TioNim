package com.tionim.game.Modelos

class Jugador(jugadorId: String?, nickname: String?) {

     var jugadorId: String? = null
     var nickname: String? = null
     var partida: String? = null
     var victorias = 0
     var online = false
     var numeroJugador = 0
     var firstRun = false
     var favoritosID: MutableList<String>? = null

    init {
        this.jugadorId = jugadorId
        this.nickname = nickname
        partida = ""
        victorias = 0
        online = false
        numeroJugador = 0
        firstRun = true
        favoritosID = ArrayList()
    }

    fun Jugador() {}

    fun isOnline(): Boolean {
        return online
    }

    fun isFirstRun(): Boolean {
        return firstRun
    }

}