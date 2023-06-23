package com.tionim.game.Utilidades

import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import com.tionim.game.R

class Sonidos {

companion object {
    private var instance: Sonidos? = null
    private var player: SoundPool? = null

    enum class Efectos {
        TICK, GANAR, PERDER, BGM, UIIIIU, PLING, START, MAGIA
    }

    private var tick = 0
    private  var ganar:Int = 0
    private  var perder:Int = 0
    private  var bgm:Int = 0
    private  var uiiiu:Int = 0
    private  var pling:Int = 0
    private  var start:Int = 0
    private  var magia:Int = 0
    fun play(efecto: Efectos?) {
        when (efecto) {
            Efectos.TICK -> player!!.play(tick, 1f, 1f, 0, 0, 1f)
            Efectos.GANAR -> player!!.play(ganar, 1f, 1f, 0, 0, 1f)
            Efectos.PERDER -> player!!.play(perder, 1f, 1f, 0, 0, 1f)
            Efectos.BGM -> player!!.play(bgm, 1f, 1f, 0, 0, 1f)
            Efectos.UIIIIU -> player!!.play(uiiiu, 1f, 1f, 0, 0, 1f)
            Efectos.PLING -> player!!.play(pling, 1f, 0f, 0, 0, 1f)
            Efectos.START -> player!!.play(start, 1f, 0f, 0, 0, 1f)
            Efectos.MAGIA -> player!!.play(magia, 1f, 0f, 0, 0, 1f)
            else -> {}
        }
    }
}

}