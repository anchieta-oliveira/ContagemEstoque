package com.example.controledeestoque.helpers

import android.provider.BaseColumns

object ContratoBDLocal {

    object ContagemEntry: BaseColumns{
        const val TABLE_NAME ="ContagemLocal"
        const val COLUMN_ID = "id"
        const val COLUMN_COD_PROD = "cod_produto"
        const val COLUMN_QTD = "quantidade"
        const val COLUMN_TAG = "qtd_novo"

    }

    object ProdutosContagem: BaseColumns{
        const val TABLE_NAME ="ProdutosContagem"
        const val COLUMN_COD_PROD = "cod_produto"
        const val COLUMN_COD_NOME = "cod_nome"
        const val COLUMN_COD_BARRAS = "cod_barras"
        const val COLUMN_COD_CATEGORIA = "cod_categoria"
        const val COLUMN_COD_SUBCATEGORIA = "cod_subcategoria"

    }
}
