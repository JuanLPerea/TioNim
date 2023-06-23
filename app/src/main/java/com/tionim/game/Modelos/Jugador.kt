package com.tionim.game.Modelos

class Jugador(uid: String, nickName: String) {

    private var jugadorId: String? = null
    private var nickname: String? = null
    private var partida: String? = null
    private var victorias = 0
    private var online = false
    private var numeroJugador = 0
    private var firstRun = false
    private var favoritosID: List<String>? = null

    fun Jugador(jugadorId: String?, nickname: String?) {
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

    fun getNumeroJugador(): Int {
        return numeroJugador
    }

    fun setNumeroJugador(numeroJugador: Int) {
        this.numeroJugador = numeroJugador
    }

    fun getFavoritosID(): List<String>? {
        return favoritosID
    }

    fun setFavoritosID(favoritosID: List<String>?) {
        this.favoritosID = favoritosID
    }

    fun getJugadorId(): String? {
        return jugadorId
    }

    fun setJugadorId(jugadorId: String?) {
        this.jugadorId = jugadorId
    }

    fun getNickname(): String? {
        return nickname
    }

    fun setNickname(nickname: String?) {
        this.nickname = nickname
    }

    fun getPartida(): String? {
        return partida
    }

    fun setPartida(partida: String?) {
        this.partida = partida
    }

    fun getVictorias(): Int {
        return victorias
    }

    fun setVictorias(victorias: Int) {
        this.victorias += victorias
    }

    fun isOnline(): Boolean {
        return online
    }

    fun setOnline(online: Boolean) {
        this.online = online
    }

    fun isFirstRun(): Boolean {
        return firstRun
    }

    fun setFirstRun(firstRun: Boolean) {
        this.firstRun = firstRun
    }
}