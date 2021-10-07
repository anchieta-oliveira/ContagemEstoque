package com.example.controledeestoque.divergencia

import com.example.controledeestoque.helpers.BdServer
import com.example.controledeestoque.produto.Produto

class Divergencia (val id:Int,
                   val codProduto:Int,
                   val categoria:String,
                   val qtdDivergencia:Int) {

    var nomeProd:String = "Erro"
}