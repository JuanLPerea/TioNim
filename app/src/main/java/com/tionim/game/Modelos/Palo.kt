package com.tionim.game.Modelos

class Palo(n: Int) {

    private var numeroPalo = 0
    private var seleccionado = false

    fun Palo(numeroPalo: Int) {
        this.numeroPalo = numeroPalo
        seleccionado = false
    }

    fun Palo() {}

    fun getNumeroPalo(): Int {
        return numeroPalo
    }

    fun setNumeroPalo(numeroPalo: Int) {
        this.numeroPalo = numeroPalo
    }

    fun isSeleccionado(): Boolean {
        return seleccionado
    }

    fun setSeleccionado(seleccionado: Boolean) {
        this.seleccionado = seleccionado
    }


}