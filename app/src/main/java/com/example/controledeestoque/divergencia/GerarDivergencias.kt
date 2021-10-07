package com.example.controledeestoque.divergencia

import android.content.Context
import com.example.controledeestoque.contagem.AgendaContagem
import com.example.controledeestoque.helpers.BdServer
import com.example.controledeestoque.helpers.HelperBDLocal
import com.example.controledeestoque.helpers.Login

class GerarDivergencias {

    fun gerarDivergencias (login: Login, agendaContagem: AgendaContagem, context: Context): MutableList<Divergencia> {
        // gerar divergencia comparando qtd de produtos esperados com total ca contagem para cada produto
        val contagem = HelperBDLocal(context).getContagem(agendaContagem.id)
        val qtdProduto = BdServer().getQtdProdtutos(login)
        val listaDivergencia = mutableListOf<Divergencia>()
        val prodContagem = HelperBDLocal(context).getProdutosContagem(agendaContagem.categoria)

        prodContagem.forEach {

            val codAtual = it.cod_produto
            val totalProdContagem = contagem.filter { it.codProduto == codAtual}.sumOf { it.qtd }
            val qtdProdutoEsperado = qtdProduto.find { it.codProduto == codAtual}!!.qtdTotalEsperado

            val divergenciaProd = (totalProdContagem - qtdProdutoEsperado)

            if (divergenciaProd != 0){
                listaDivergencia.add(Divergencia(agendaContagem.id,it.cod_produto, agendaContagem.categoria, divergenciaProd))
            }

        }

        return listaDivergencia
    }



    fun getDivergenciasPendentes(listaDivergencia: MutableList<Divergencia>): MutableList<DivergenciasPendentes> {
        val map = HashMap<String, MutableList<Divergencia>>()
        val listaPendencias = mutableListOf<DivergenciasPendentes>()

        listaDivergencia.forEach {
            val key = it.categoria
            if (map.containsKey(key)){
                val list = map.get(key)
                list!!.add(it)
            }else{
                val list = mutableListOf<Divergencia>()
                list.add(it)
                map.put(key, list)
            }
        }

        val categorias = map.keys

        categorias.forEach{
            val listaAtual = map.get(it)
            val qtdProd = listaAtual!!.size
            val totalDivergencia = listaAtual!!.sumOf { it.qtdDivergencia }
            var x:String = ""
            if (totalDivergencia > 10){
                val x = "Critico"
            }else{
                val x = "Moderado"
            }

            listaPendencias.add(DivergenciasPendentes(it, totalDivergencia, qtdProd, x))
        }

        return listaPendencias

    }
}