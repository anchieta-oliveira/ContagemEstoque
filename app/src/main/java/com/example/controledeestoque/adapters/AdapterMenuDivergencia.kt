package com.example.controledeestoque.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.controledeestoque.R
import com.example.controledeestoque.divergencia.Divergencia
import com.example.controledeestoque.divergencia.DivergenciasPendentes

class AdapterMenuDivergencia (private val dataListDivergencia:MutableList<DivergenciasPendentes>,
                              private val context: Context,
                              val onClick:(DivergenciasPendentes) -> Unit): RecyclerView.Adapter<AdapterMenuDivergencia.DivergenciaViewHolder>() {


    inner class DivergenciaViewHolder(view: View): RecyclerView.ViewHolder(view){
        val textCategoria = itemView.findViewById<TextView>(R.id.text_categoria)
        val textDesempenho =itemView.findViewById<TextView>(R.id.desempenho_pendencia)
        val textQtdProdDivergencia = itemView.findViewById<TextView>(R.id.text_qtd_produto)
        val textStatus = itemView.findViewById<TextView>(R.id.status_divergencia)

        fun bind(divergencia: DivergenciasPendentes){
            textCategoria.text = divergencia.categoria
            textDesempenho.text = "Desempenho: " + divergencia.desempenho
            textQtdProdDivergencia.text = "Qtd Produtos: " + divergencia.qtdProdutos
            textStatus.text = "Status: " + divergencia.status

            with(itemView){
                setOnClickListener{
                    onClick.invoke(divergencia)
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DivergenciaViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_recycle_divergencia, parent, false)
        return DivergenciaViewHolder(view)
    }

    override fun onBindViewHolder(holder: DivergenciaViewHolder, position: Int) {
        holder.bind(dataListDivergencia[position])
    }

    override fun getItemCount() = dataListDivergencia.size


}