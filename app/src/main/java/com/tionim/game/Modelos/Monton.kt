package com.tionim.game.Modelos

import java.util.Random


class Monton(n: Int, level: Int) {

    val MAX_PALOS = 8
    val MIN_PALOS = 1
    var palos: MutableList<Palo>? = null
    var numeroMonton = 0


    fun Monton(numeroMonton: Int, level: Int) {
        palos = ArrayList<Palo>()
        this.numeroMonton = numeroMonton
        val r = Random()
        // int numPalosAleat = r.nextInt(MAX_PALOS - MIN_PALOS) + MIN_PALOS;
        // el número de palos en cada monton depende del nivel y además va variando del primer monton al último de menos palos a mas

//        int numPalosAleat = (numeroMonton * ((level+1) * (r.nextInt(MAX_PALOS - MIN_PALOS) + MIN_PALOS))) + 1;
        var numPalosAleat = 0
        numPalosAleat = when (numeroMonton) {
            0 -> r.nextInt(2) + 1
            1 -> r.nextInt(3) + 2
            2 -> r.nextInt(3) + 3
            else -> r.nextInt(4) + 4
        }
        for (n in 0 until numPalosAleat) {
            palos!!.add(Palo(n))
        }
    }

    fun Monton() {}

    fun getNumeroMonton(): Int {
        return numeroMonton
    }

    fun setNumeroMonton(numeroMonton: Int) {
        this.numeroMonton = numeroMonton
    }


    fun getPalosseleccionados(): Int {
        var palosSeleccionados = 0
        palos!!.forEach {palo ->
            if (palo.isSeleccionado()) palosSeleccionados++
        }
        return palosSeleccionados
    }

    fun deseleccionarTodo() {
        palos!!.forEach {palo ->
            palo.setSeleccionado(false)
        }
    }

    fun renumerarPalos() {
        for (n in palos!!.indices) {
            palos!![n].setNumeroPalo(n)
        }
    }

}