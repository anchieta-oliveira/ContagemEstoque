package com.example.controledeestoque.helpers

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.controledeestoque.contagem.Contagem
import com.example.controledeestoque.produto.Produto

class HelperBDLocal(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object{
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "BDContagemLocal.db"

    }

    private val CRETE_TABLE_CONTAGEM = "CREATE TABLE ${ContratoBDLocal.ContagemEntry.TABLE_NAME} (" +
            "${ContratoBDLocal.ContagemEntry.COLUMN_ID} integer," +
            "${ContratoBDLocal.ContagemEntry.COLUMN_COD_PROD} integer," +
            "${ContratoBDLocal.ContagemEntry.COLUMN_QTD} integer," +
            "${ContratoBDLocal.ContagemEntry.COLUMN_TAG} TEXT)"


    private val CREATE_TABLE_PRODUTOS_CONTAGEM = "CREATE TABLE ${ContratoBDLocal.ProdutosContagem.TABLE_NAME} (" +
            "${ContratoBDLocal.ProdutosContagem.COLUMN_COD_PROD} integer PRIMARY KEY," +
            "${ContratoBDLocal.ProdutosContagem.COLUMN_COD_NOME} integer," +
            "${ContratoBDLocal.ProdutosContagem.COLUMN_COD_BARRAS} integer," +
            "${ContratoBDLocal.ProdutosContagem.COLUMN_COD_CATEGORIA} TEXT," +
            "${ContratoBDLocal.ProdutosContagem.COLUMN_COD_SUBCATEGORIA} TEXT)"



    private val DROP_TABELA_CONTAGEM = "DROP TABLE IF EXISTS ${ContratoBDLocal.ContagemEntry.TABLE_NAME}"
    private val DROP_TABLE_PRODUTOS_CONTAGEM = "DROP TABLE IF EXISTS ${ContratoBDLocal.ProdutosContagem.TABLE_NAME}"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CRETE_TABLE_CONTAGEM)
        db?.execSQL(CREATE_TABLE_PRODUTOS_CONTAGEM)
    }

    override fun onUpgrade(db: SQLiteDatabase?, versaoAntiga: Int, novaVersao: Int) {
        if (versaoAntiga != novaVersao){
            db?.execSQL(DROP_TABELA_CONTAGEM)
            db?.execSQL(DROP_TABLE_PRODUTOS_CONTAGEM)
        }
    }
   fun deletarProdutosContagem(){
       val db = writableDatabase
       db?.execSQL(DROP_TABELA_CONTAGEM)
       db?.execSQL(CRETE_TABLE_CONTAGEM)
       db?.execSQL(DROP_TABLE_PRODUTOS_CONTAGEM)
       db?.execSQL(CREATE_TABLE_PRODUTOS_CONTAGEM)
   }

    fun getProdutosContagem(categoriaFiltro:String): MutableList<Produto> {
        val db = readableDatabase ?: return mutableListOf<Produto>()
        val where = "${ContratoBDLocal.ProdutosContagem.COLUMN_COD_CATEGORIA} LIKE ?"
        var args: Array<String> = arrayOf()
        args = arrayOf(categoriaFiltro)
        val lista = mutableListOf<Produto>()
        val cursor = db.query(ContratoBDLocal.ProdutosContagem.TABLE_NAME, null, where, args, null, null, null)

        while (cursor.moveToNext()) {
            var produto = Produto(
                cursor.getInt(cursor.getColumnIndexOrThrow(ContratoBDLocal.ProdutosContagem.COLUMN_COD_PROD)),
                cursor.getInt(cursor.getColumnIndexOrThrow(ContratoBDLocal.ProdutosContagem.COLUMN_COD_PROD)),
                cursor.getLong(cursor.getColumnIndexOrThrow(ContratoBDLocal.ProdutosContagem.COLUMN_COD_BARRAS)),
                cursor.getString(cursor.getColumnIndexOrThrow(ContratoBDLocal.ProdutosContagem.COLUMN_COD_NOME)),
                cursor.getString(cursor.getColumnIndexOrThrow(ContratoBDLocal.ProdutosContagem.COLUMN_COD_CATEGORIA)),
                cursor.getString(cursor.getColumnIndexOrThrow(ContratoBDLocal.ProdutosContagem.COLUMN_COD_SUBCATEGORIA))
            )
            lista.add(produto)
        }
        db.close()
        return lista
    }


    fun addProdutosContagem (produtos: MutableList<Produto>){
        //BCopiar do servidor as informaçõs dos produtos da contagem que será realizda
        val db = writableDatabase ?: return
        produtos.forEach {
            var valores = ContentValues().apply {
                put(ContratoBDLocal.ProdutosContagem.COLUMN_COD_PROD, it.cod_produto)
                put(ContratoBDLocal.ProdutosContagem.COLUMN_COD_NOME, it.nome)
                put(ContratoBDLocal.ProdutosContagem.COLUMN_COD_BARRAS, it.cod_barras)
                put(ContratoBDLocal.ProdutosContagem.COLUMN_COD_CATEGORIA, it.categoria)
                put(ContratoBDLocal.ProdutosContagem.COLUMN_COD_SUBCATEGORIA, it.subCategoria)
            }
            db.insert(ContratoBDLocal.ProdutosContagem.TABLE_NAME, null, valores)
        }
    }

    fun salvarContagem (contagem: MutableList<Contagem>){
        //adicionar lista de produtos contados quando selecionar a opção salvar
        val db = writableDatabase ?: return

        contagem.forEach {
            var valores = ContentValues().apply {
                put(ContratoBDLocal.ContagemEntry.COLUMN_ID, it.id)
                put(ContratoBDLocal.ContagemEntry.COLUMN_COD_PROD, it.codProduto)
                put(ContratoBDLocal.ContagemEntry.COLUMN_QTD, it.qtd)
                put(ContratoBDLocal.ContagemEntry.COLUMN_TAG, it.tag)
            }
            db.insert(ContratoBDLocal.ContagemEntry.TABLE_NAME, null, valores)
        }
    }

    fun getContagem(idContagem: Int): MutableList<Contagem> {
        val db = readableDatabase ?: return mutableListOf<Contagem>()
        val where = "${ContratoBDLocal.ContagemEntry.COLUMN_ID} LIKE ?"
        var args: Array<String> = arrayOf()
        args = arrayOf(idContagem.toString())
        val lista = mutableListOf<Contagem>()
        val cursor = db.query(ContratoBDLocal.ContagemEntry.TABLE_NAME, null, where, args, null, null, null)

        while (cursor.moveToNext()) {
            var contagem = Contagem(
                cursor.getInt(cursor.getColumnIndexOrThrow(ContratoBDLocal.ContagemEntry.COLUMN_ID)),
                cursor.getInt(cursor.getColumnIndexOrThrow(ContratoBDLocal.ContagemEntry.COLUMN_COD_PROD)),
                cursor.getInt(cursor.getColumnIndexOrThrow(ContratoBDLocal.ContagemEntry.COLUMN_QTD)),
                cursor.getString(cursor.getColumnIndexOrThrow(ContratoBDLocal.ContagemEntry.COLUMN_TAG)),
                null
            )
            lista.add(contagem)
        }
        db.close()
        return lista
    }

}