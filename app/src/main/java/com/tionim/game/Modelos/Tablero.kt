package com.tionim.game.Modelos

import java.util.Random


class Tablero(level: Int) {

    // En el tablero habrá un numero de montones de monedas aleatorio de 3 a 6

    // En el tablero habrá un numero de montones de monedas aleatorio de 3 a 6
    private val MAX_MONTONES = 6
    private val MIN_MONTONES = 3


    var montones: MutableList<Monton>? = null
    private var numMontones = 0
    private var montonSeleccionado = 0

    // Constructor generamos un nuevo tablero con montones y monedas aleatoriamente
    init {
        val r = Random()

        //this.numMontones = r.nextInt(MAX_MONTONES - MIN_MONTONES) + MIN_MONTONES;

        // El número de montones depende del nivel que le pasemos
        if (level > 10) {
            numMontones = 6
        } else if (level > 6) {
            numMontones = 5
        } else if (level > 3) {
            numMontones = 4
        } else {
            numMontones = 3
        }
        montonSeleccionado = -1
        montones = ArrayList()
        for (n in 0 until numMontones) {
            montones!!.add(Monton(n, level))
        }
    }

    fun Tablero() {}

    fun palosSeleccionadosTotal(): Int {
        var totalPalosSeleccionados = 0
        for (montonTmp in montones!!) {
            totalPalosSeleccionados += montonTmp.getPalosseleccionados()
        }
        return totalPalosSeleccionados
    }

    fun eliminarSeleccionados() {

        // En el montón seleccionado, eliminamos tantos palos como indique que tenemos seleccionados (Por el final de la lista).
        val newPalos: MutableList<Palo> = ArrayList()
        newPalos.removeAll(newPalos)
        for (paloTmp in montones!![montonSeleccionado].palos!!) {
            if (!paloTmp.isSeleccionado()) {
                newPalos.add(paloTmp)
            }
        }
        montones!!.get(montonSeleccionado).palos = newPalos

        // Si el montón está vacío, quitar también el montón
        if (montones!![montonSeleccionado].palos!!.size == 0) {
            montones!!.removeAt(montonSeleccionado)
            renumerarMontones()
        } else {
            // Si el montón todavía no está vacío indicar que no tenemos nada seleccionado
            //  this.montones.get(montonSeleccionado).deseleccionarTodo();
            montones!![montonSeleccionado].renumerarPalos()
        }
        montonSeleccionado = -1
    }

    fun renumerarMontones() {
        for (n in montones!!.indices) {
            montones!![n].numeroMonton = n
        }
    }

    fun maxPalosMontones(): Int {
        var maxPalos = 0
        for (montonTmp in montones!!) {
            if (montonTmp.palos != null) {
                if (montonTmp.palos!!.size > maxPalos) maxPalos = montonTmp.palos!!.size
            }
        }
        return maxPalos
    }

    fun palosTotales(): Int {
        var numPalos = 0
        for (montonTmp in montones!!) {
            numPalos += montonTmp.palos!!.size
        }
        return numPalos
    }


}