package com.tionim.game.Adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.tionim.game.Modelos.Records
import com.tionim.game.R
import com.tionim.game.Utilidades.Utilidades
import java.lang.Exception
import java.util.Random

class RecordsAdapter(context: Context?, records: MutableList<Records>) : RecyclerView.Adapter<RecordsAdapter.AdapterViewHolder>(){
     var records: MutableList<Records>? = records
     var context: Context? = context

    init {
        this.context = context
        this.records = records
    }


    override fun onCreateViewHolder( viewGroup: ViewGroup, i: Int): AdapterViewHolder {
        val v: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.row_records, viewGroup, false)
        Log.d("Depurar" , "Creado View Holder")
        return AdapterViewHolder(v)
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, i: Int) {
        val recordRow: Records = records!![i]
        Log.d("Depurar" , "bind: " + recordRow.nickname)
        if (recordRow.idJugador != null) {
            // UtilsFirebase.descargarImagenFirebaseYGuardarla(recordRow.getIdJugador(), holder.avatarRecord);
            //Cargar imagen de los records siempre de los archivos guardados en memoria interna
            // Estos archivos se actualizan cuando se descargan de Firebase al producirse el evento OnDataChange en los Records

            try {
                val imagenRecord: Bitmap =
                    Utilidades.recuperarImagenMemoriaInterna(context!!, recordRow.idJugador)!!
                    holder.avatarRecord.setImageBitmap(imagenRecord)
            } catch (e : Exception){
                Log.d("Depurar" , "No hay imagen de usuario " + e.toString() )
                holder.avatarRecord.setImageResource(R.drawable.picture)
            }

            holder.nickRecord.setText(recordRow.nickname)
            holder.levelRecord.setText(recordRow.level.toString())
            holder.victoriasRecord.setText(recordRow.victorias.toString())
            val rnd = Random()
            holder.linearLayout.setBackgroundColor(
                Color.argb(
                    255,
                    rnd.nextInt(20) + 194,
                    rnd.nextInt(20) + 113,
                    rnd.nextInt(20) + 71
                )
            )
        }
    }

    override fun getItemCount(): Int {
        Log.d("Depurar" , "Adapter size: " + records!!.size.toString())
        return records!!.size
    }

    class AdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nickRecord: TextView
        var levelRecord: TextView
        var victoriasRecord: TextView
        var avatarRecord: ImageView
        var linearLayout: LinearLayout

        init {
            avatarRecord = itemView.findViewById<ImageView>(R.id.avatarRecordIV)
            nickRecord = itemView.findViewById<TextView>(R.id.nickRecordET)
            levelRecord = itemView.findViewById<TextView>(R.id.recordlevelET)
            victoriasRecord = itemView.findViewById<TextView>(R.id.recordVictoriasET)
            linearLayout = itemView.findViewById<LinearLayout>(R.id.rowLinear)
        }
    }
}