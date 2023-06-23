package com.tionim.game.Modelos

class Records(jugadorId: String?, nickname: String?, victorias: Int, level: Int)  {

    private var idJugador: String? = null
    private var nickname: String? = null
    private var victorias = 0
    private var level = 0


    fun Records(idJugador: String?, nickname: String?, victorias: Int, level: Int) {
        this.idJugador = idJugador
        this.nickname = nickname
        this.victorias = victorias
        this.level = level
    }

    fun Records() {}


    fun getLevel(): Int {
        return level
    }

    fun setLevel(level: Int) {
        this.level = level
    }

    fun getIdJugador(): String? {
        return idJugador
    }

    fun setIdJugador(idJugador: String?) {
        this.idJugador = idJugador
    }

    fun getNickname(): String? {
        return nickname
    }

    fun setNickname(nickname: String?) {
        this.nickname = nickname
    }

    fun getVictorias(): Int {
        return victorias
    }

    fun setVictorias(victorias: Int) {
        this.victorias = victorias
    }

}