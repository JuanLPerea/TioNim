package com.tionim.game.Adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
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
import java.util.Random

class RecordsAdapter(context: Context?, records: MutableList<Records>) : RecyclerView.Adapter<RecordsAdapter.AdapterViewHolder>(){
     var records: MutableList<Records>? = records
     var context: Context? = context

    fun RecordsAdapter() {}

    override fun onCreateViewHolder(@NonNull viewGroup: ViewGroup, i: Int): AdapterViewHolder {
        val v: View =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.row_records, viewGroup, false)
        return AdapterViewHolder(v)
    }

    override fun onBindViewHolder(@NonNull holder: AdapterViewHolder, i: Int) {
        val recordRow: Records = records!![i]

        // UtilsFirebase.descargarImagenFirebaseYGuardarla(recordRow.getIdJugador(), holder.avatarRecord);
        //Cargar imagen de los records siempre de los archivos guardados en memoria interna
        // Estos archivos se actualizan cuando se descargan de Firebase al producirse el evento OnDataChange en los Records
        val imagenRecord: Bitmap =
            Utilidades.recuperarImagenMemoriaInterna(context!!, recordRow.idJugador)!!
        if (imagenRecord != null && recordRow.idJugador !== "idJugador") {
            holder.avatarRecord.setImageBitmap(imagenRecord)
        } else {
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

    override fun getItemCount(): Int {
        return records!!.size
    }

    class AdapterViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
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