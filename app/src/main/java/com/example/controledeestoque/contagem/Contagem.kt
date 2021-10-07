package com.example.controledeestoque.contagem

import com.example.controledeestoque.produto.Produto

class Contagem(
    val id:Int,
    val codProduto:Int,
    val qtd:Int,
    val tag:String,
    val produto: Produto?
               )