package com.example.controledeestoque

import com.example.controledeestoque.contagem.Contagem
import com.example.controledeestoque.divergencia.Divergencia
import com.example.controledeestoque.divergencia.DivergenciasPendentes
import com.example.controledeestoque.helpers.BdServer
import com.example.controledeestoque.helpers.HelperBDLocal
import com.example.controledeestoque.produto.QtdProduto

fun main (){
    val contagem = mutableListOf<Contagem>()
    val qtdProduto = mutableListOf<QtdProduto>()
    val idContagem = 0
    contagem.add(Contagem(1, 1, 10, "NOVO", null))
    contagem.add(Contagem(1, 1, 3, "DANIFICADO", null))
    contagem.add(Contagem(2, 2, 3, "NOVO", null))
    contagem.add(Contagem(2, 2, 3, "DANIFICADO", null))
    contagem.add(Contagem(3, 3, 3, "DANIFICADO", null))

    qtdProduto.add(QtdProduto(1,0,0,0,15))
    qtdProduto.add(QtdProduto(2,0,0,0,2))
    qtdProduto.add(QtdProduto(3,0,0,0,3))



    val listaDivergencia = mutableListOf<Divergencia>()

    qtdProduto.forEach {
        val codAtual = it.codProduto
        val totalProdContagem = contagem.filter { it.codProduto == codAtual}.sumOf { it.qtd }

        val divergenciaProd = totalProdContagem - it.qtdTotalEsperado

        if (divergenciaProd != 0){
            listaDivergencia.add(Divergencia(idContagem,it.codProduto, "", divergenciaProd))
        }
    }

    listaDivergencia.forEach {
        println(it.qtdDivergencia)
    }

}