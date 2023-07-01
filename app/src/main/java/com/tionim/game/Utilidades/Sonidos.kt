package com.tionim.game.Utilidades

import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import com.tionim.game.R

class Sonidos {

companion object {
    val player = SoundPool.Builder().build()
    enum class Efectos {
        TICK, GANAR, PERDER, BGM, UIIIIU, PLING, START, MAGIA
    }

    fun play(efecto: Efectos?) {
        when (efecto) {
            Efectos.TICK -> player.play(R.raw.toc, 1f, 1f, 0, 0, 1f)
            Efectos.GANAR -> player.play(R.raw.win, 1f, 1f, 0, 0, 1f)
            Efectos.PERDER -> player.play(R.raw.lose, 1f, 1f, 0, 0, 1f)
            Efectos.BGM -> player.play(R.raw.cutebgm, 1f, 1f, 0, 0, 1f)
            Efectos.UIIIIU -> player.play(R.raw.uiiiiu, 1f, 1f, 0, 0, 1f)
            Efectos.PLING -> player.play(R.raw.pling, 1f, 0f, 0, 0, 1f)
            Efectos.START -> player.play(R.raw.fiiiu, 1f, 0f, 0, 0, 1f)
            Efectos.MAGIA -> player.play(R.raw.magia  , 1f, 0f, 0, 0, 1f)
            else -> {}
        }
    }
}

}