package com.example.controledeestoque.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.controledeestoque.R
import com.example.controledeestoque.contagem.Contagem

class AdapterContador(private val dataListContagem: MutableList<Contagem>, private val context: Context,
                      val onClick:(Contagem) -> Unit): RecyclerView.Adapter<AdapterContador.ContadorViewHolder>() {


    inner class ContadorViewHolder(view: View): RecyclerView.ViewHolder(view){
        val textNome = itemView.findViewById<TextView>(R.id.text_nome)
        val textCodProduto = itemView.findViewById<TextView>(R.id.text_cod_barras)
        val textCodbarras = itemView.findViewById<TextView>(R.id.text_cod_produto)
        val textQtd = itemView.findViewById<TextView>(R.id.text_qtd_contados)
        val tagStatus = itemView.findViewById<LinearLayout>(R.id.tag_status)


        fun bind(contagem: Contagem){
            textNome.text = contagem.produto!!.nome
            textCodProduto.text = "Cod: " + contagem.produto.cod_produto.toString()
            textCodbarras.text = "Cod. Barras: " + contagem.produto.cod_barras.toString()
            textQtd.text = "Qtd.: " + contagem.qtd

            when(contagem.tag) {
                "NOVO" ->  tagStatus.setBackgroundColor(Color.GREEN)
                "DANIFICADO" ->  tagStatus.setBackgroundColor(Color.RED)
                "MOSTRUARIO" ->  tagStatus.setBackgroundColor(Color.YELLOW)
            }


            with(itemView){
                setOnClickListener{
                    onClick.invoke(contagem)
                }
            }
        }
   }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContadorViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_recycle_contagem, parent, false)
        return ContadorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContadorViewHolder, position: Int) {
        holder.bind(dataListContagem[position])
    }

    override fun getItemCount() = dataListContagem.size

    fun updateList(dataListAtivos: MutableList<Contagem>){
        this.dataListContagem.clear()
        this.dataListContagem.addAll(dataListAtivos)
        notifyDataSetChanged()
    }

    fun addContagem(contagem: Contagem){
        this.dataListContagem.add(contagem)
        notifyItemInserted(dataListContagem.lastIndex)
    }

    fun retornarLista(): MutableList<Contagem> {
        return dataListContagem
    }

}