package com.example.controledeestoque.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.controledeestoque.R
import com.example.controledeestoque.contagem.AgendaContagem

class AdapterAgenda (private val dataListAgenda:MutableList<AgendaContagem>,
                     private val context: Context,
                     val onClick:(AgendaContagem) -> Unit): RecyclerView.Adapter<AdapterAgenda.AgendaViewholder>() {



    inner class AgendaViewholder(view: View): RecyclerView.ViewHolder(view) {
        val textCategoria = itemView.findViewById<TextView>(R.id.item_categoria)
        val textData = itemView.findViewById<TextView>(R.id.data_contagem)
        val textQtd = itemView.findViewById<TextView>(R.id.item_qtd_contagem)

        fun bind(agenda: AgendaContagem){
            textCategoria.text = agenda.categoria
            textData.text = "Data: " + agenda.data
            textQtd.text = "Qtd Produtos: " + agenda.qtdProduto

            with(itemView){
                setOnClickListener{
                    onClick.invoke(agenda)
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgendaViewholder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_recycle_agenda_contagem, parent, false)
        return  AgendaViewholder(view)
    }

    override fun onBindViewHolder(holder: AgendaViewholder, position: Int) {
        holder.bind(dataListAgenda[position])
    }

    override fun getItemCount() = dataListAgenda.size

}