package com.tionim.game.Utilidades

import android.content.Context
import android.net.ConnectivityManager


class UtilityNetwork {

companion object {
    fun isNetworkAvailable(context: Context): Boolean {
        var br = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        br = activeNetworkInfo != null && activeNetworkInfo.isConnected
        return br
    }

    fun isWifiAvailable(context: Context): Boolean {
        var bdev = false
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        bdev =
            ((activeNetwork != null) && activeNetwork.isConnected() && (activeNetwork.type) == ConnectivityManager.TYPE_WIFI)
        return bdev
    }
}

}