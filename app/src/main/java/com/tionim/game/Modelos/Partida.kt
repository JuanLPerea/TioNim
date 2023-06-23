package com.tionim.game.Modelos

class Partida {

    private var partidaID: String? = null
    private var jugador1ID: String? = null
    private var jugador2ID: String? = null
    private var jugador1Ready = false
    private var jugador2Ready = false
    private var jugando = false
    private var turno = 0
    private var tablero: Tablero? = null
    private var ganador = 0
    private var numeroSala = 0
    private var level = 0

    fun Partida(
        partidaID: String?,
        numeroSala: Int,
        jugador1ID: String?,
        jugador2ID: String?,
        tablero: Tablero?,
        level: Int
    ) {
        this.partidaID = partidaID
        this.jugador1ID = jugador1ID
        this.jugador2ID = jugador2ID
        this.tablero = tablero
        turno = 1
        ganador = 0
        jugando = false
        jugador1Ready = false
        jugador2Ready = false
        this.numeroSala = numeroSala
        this.level = level
    }

    fun Partida() {
        partidaID = null
        jugador1ID = null
        jugador2ID = null
        tablero = Tablero()
        turno = 1
        ganador = 0
        jugando = false
        jugador1Ready = false
        jugador2Ready = false
    }

    fun getLevel(): Int {
        return level
    }

    fun levelUp() {
        level++
    }

    fun getNumeroSala(): Int {
        return numeroSala
    }

    fun setNumeroSala(numeroSala: Int) {
        this.numeroSala = numeroSala
    }

    fun isJugador1Ready(): Boolean {
        return jugador1Ready
    }

    fun setJugador1Ready(jugador1Ready: Boolean) {
        this.jugador1Ready = jugador1Ready
    }

    fun isJugador2Ready(): Boolean {
        return jugador2Ready
    }

    fun setJugador2Ready(jugador2Ready: Boolean) {
        this.jugador2Ready = jugador2Ready
    }

    fun isJugando(): Boolean {
        return jugando
    }

    fun setJugando(jugando: Boolean) {
        this.jugando = jugando
    }

    fun getPartidaID(): String? {
        return partidaID
    }

    fun setPartidaID(partidaID: String?) {
        this.partidaID = partidaID
    }

    fun getJugador1ID(): String? {
        return jugador1ID
    }

    fun setJugador1ID(jugador1ID: String?) {
        this.jugador1ID = jugador1ID
    }

    fun getJugador2ID(): String? {
        return jugador2ID
    }

    fun setJugador2ID(jugador2ID: String?) {
        this.jugador2ID = jugador2ID
    }

    fun getTurno(): Int {
        return turno
    }

    fun setTurno(turno: Int) {
        this.turno = turno
    }

    fun getTablero(): Tablero? {
        return tablero
    }

    fun setTablero(tablero: Tablero?) {
        this.tablero = tablero
    }

    fun getGanador(): Int {
        return ganador
    }

    fun setGanador(ganador: Int) {
        this.ganador = ganador
    }

    fun turnoToggle() {
        if (getTurno() == 2) {
            setTurno(1)
        } else {
            setTurno(2)
        }
    }
}