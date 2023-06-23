package com.tionim.game.Utilidades

import android.content.Context
import com.tionim.game.Modelos.Jugador
import com.tionim.game.Modelos.Records
import java.util.Collections

class SharedPrefs {

    companion object {

        fun saveJugadorPrefs(context: Context, jugador: Jugador) {
            val amigos: MutableSet<String> = HashSet()
            if (jugador.getFavoritosID() != null) {
                amigos.addAll(jugador.getFavoritosID()!!)
            }
            val sharedPreferences =
                context.getSharedPreferences(Constantes.ARCHIVO_PREFS, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(Constantes.NICKNAME_PREFS, jugador.getNickname())
            editor.putString(Constantes.JUGADORID_PREFS, jugador.getJugadorId())
            editor.putInt(Constantes.VICTORIAS_PREFS, jugador.getVictorias())
            editor.putBoolean(Constantes.FIRST_RUN, jugador.isFirstRun())
            editor.putStringSet(Constantes.AMIGOS, amigos)
            editor.putInt(Constantes.NUMEROJUGADOR, jugador.getNumeroJugador())
            editor.commit()
        }

        fun getJugadorPrefs(context: Context): Jugador? {
            val jugador = Jugador("", "")
            val sharedPreferences =
                context.getSharedPreferences(Constantes.ARCHIVO_PREFS, Context.MODE_PRIVATE)
            jugador.setNickname(sharedPreferences.getString(Constantes.NICKNAME_PREFS, "Jugador"))
            jugador.setJugadorId(sharedPreferences.getString(Constantes.JUGADORID_PREFS, null))
            jugador.setVictorias(sharedPreferences.getInt(Constantes.VICTORIAS_PREFS, 0))
            jugador.setFirstRun(sharedPreferences.getBoolean(Constantes.FIRST_RUN, true))
            jugador.setNumeroJugador(sharedPreferences.getInt(Constantes.NUMEROJUGADOR, 0))
            val amigos = sharedPreferences.getStringSet(Constantes.AMIGOS, null)
            val amigosList: MutableList<String> = ArrayList()
            if (amigos != null) {
                amigosList.addAll(amigos)
                jugador.setFavoritosID(amigosList)
            }
            return jugador
        }

        fun saveRecordsPrefs(context: Context, records: List<Records>) {
            val sharedPreferences =
                context.getSharedPreferences(Constantes.ARCHIVO_PREFS, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            var n = 0
            for (record in records) {
                editor.putString(
                    Constantes.RECORDS_PREFS + n,
                    ((record.getIdJugador() + "#" + record.getNickname()).toString() + "#" + record.getLevel()).toString() + "#" + record.getVictorias()
                )
                n++
            }
            editor.commit()
        }

        fun getRecordsPrefs(context: Context): MutableList<Records> {
            val records: MutableList<Records> = ArrayList<Records>()
            val sharedPreferences =
                context.getSharedPreferences(Constantes.ARCHIVO_PREFS, Context.MODE_PRIVATE)
            for (n in 0..9) {
                val recordGuardado = sharedPreferences.getString(Constantes.RECORDS_PREFS + n, null)
                if (recordGuardado != null) {
                    val recordSplit =
                        recordGuardado.split("#".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                    if (recordSplit != null) {
                        records.add(
                            Records(
                                recordSplit[0],
                                recordSplit[1], recordSplit[3].toInt(), recordSplit[2].toInt()
                            )
                        )
                    }
                } else {
                    records.add(Records("idJugador", "Jugador $n", 0, 0))
                }
            }
            return records
        }

        fun updateRecordsPrefs(context: Context, record: Records) {
            val oldRecords: MutableList<Records> = getRecordsPrefs(context)
            oldRecords.add(record)
            oldRecords.sortBy { record.getVictorias() }
            val newRecords: MutableList<Records> = ArrayList<Records>()
            for (n in 0..9) {
                newRecords.add(oldRecords[n])
                if (n == oldRecords.size) break
            }
            saveRecordsPrefs(context, newRecords)
        }
    }

}