package com.example.controledeestoque.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.controledeestoque.R
import com.example.controledeestoque.divergencia.Divergencia
import com.example.controledeestoque.helpers.BdServer
import com.example.controledeestoque.helpers.Login

class AdapterDivergencia(private val dataListaDivergencia:MutableList<Divergencia>,
                         private val context: Context,
                         private val login: Login,
                         val onClick:(Divergencia) -> Unit): RecyclerView.Adapter<AdapterDivergencia.DivergenciaViewHolder>() {


    inner class DivergenciaViewHolder(view: View): RecyclerView.ViewHolder(view){
        val textNomeProd = itemView.findViewById<TextView>(R.id.text_nome_div)
        val textCdoProd = itemView.findViewById<TextView>(R.id.text_cod_produto_div)
        val textCodBarras = itemView.findViewById<TextView>(R.id.text_cod_produto_div)
        val numeroDiv = itemView.findViewById<TextView>(R.id.text_div)

        fun bind(divergencia: Divergencia){
            textNomeProd.text = divergencia.nomeProd
            textCdoProd.text = "Cod. Prod.: " + divergencia.codProduto.toString()
            textCodBarras.text = "Cod. Bar: " + divergencia.codProduto.toString()
            numeroDiv.text = "Div.: " + divergencia.qtdDivergencia.toString()

            with(itemView){
                setOnClickListener{
                    onClick.invoke(divergencia)
                }
            }
        }
    }

    fun updateList(dataList: MutableList<Divergencia>){
        this.dataListaDivergencia.clear()
        this.dataListaDivergencia.addAll(dataList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DivergenciaViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_divergencia, parent, false)
        return DivergenciaViewHolder(view)
    }

    override fun onBindViewHolder(holder: DivergenciaViewHolder, position: Int) {
        holder.bind(dataListaDivergencia[position])
    }

    override fun getItemCount() = dataListaDivergencia.size

}