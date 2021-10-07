package com.example.controledeestoque.helpers

import android.content.Context
import android.util.Log
import com.example.controledeestoque.contagem.AgendaContagem
import com.example.controledeestoque.contagem.Contagem
import com.example.controledeestoque.contagem.ListaProdutosContagem
import com.example.controledeestoque.divergencia.Divergencia
import com.example.controledeestoque.produto.Produto
import com.example.controledeestoque.produto.QtdProduto
import java.lang.Exception
import java.sql.Connection
import java.sql.DriverManager

class BdServer() {

    fun conexao(login: Login): Connection? {
        Class.forName("org.postgresql.Driver")
        return try {
            val conexao = DriverManager.getConnection(DB_URL, login.usuario, login.senha)
            Log.i("Conecção:", "Conecção bem sucedida")
            return conexao

        }catch (e:Exception){
            Log.i("Conecção:", "Falha na conecção")
                e.printStackTrace()
            return  null
        }
    }



    fun getAgendaContagem(login: Login): MutableList<AgendaContagem> {
        val conexao = conexao(login)
        val sql = "SELECT * FROM agenda_contagem;"
        val agendaQuery = conexao!!.prepareStatement(sql)
        val resultado = agendaQuery.executeQuery()
        val listaAgenda = mutableListOf<AgendaContagem>()

        while (resultado.next()){
            val agendaContagem = AgendaContagem(
                resultado.getInt(1),
                resultado.getString(2),
                0,
                resultado.getString(4)
            )
            listaAgenda.add(agendaContagem)
        }
        return listaAgenda
        conexao.close()

    }

    fun getProdutosContagem(login: Login, filtro:String): MutableList<Produto> {
        val conexao = conexao(login)
        val sql = "SELECT * FROM produtos WHERE categoria = '${filtro}';"
        val produtosQuery = conexao!!.prepareStatement(sql)
        val resultado = produtosQuery.executeQuery()
        val listaProdutosContagem = mutableListOf<Produto>()

        while (resultado.next()){
            val produtoContagem = Produto(
                resultado.getInt(1),
                resultado.getInt(2),
                resultado.getLong(3),
                resultado.getString(4),
                resultado.getString(5),
                resultado.getString(6)
            )
            listaProdutosContagem.add(produtoContagem)
        }
        return listaProdutosContagem
        conexao.close()

    }


    fun getDivergencia(login: Login): MutableList<Divergencia> {
        val conexao = conexao(login)
        val sql = "SELECT * FROM divergencias;"

        val divergencias = conexao!!.prepareStatement(sql)
        val resultado = divergencias.executeQuery()
        val listaDivergencias = mutableListOf<Divergencia>()

        while (resultado.next()){
            val divergencia = Divergencia(
                resultado.getInt(1),
                resultado.getInt(2),
                resultado.getString(3),
                resultado.getInt(4)
            )
            divergencia.nomeProd = BdServer().getNomeProd(conexao, divergencia.codProduto.toString()).toString()
            listaDivergencias.add(divergencia)
        }
        return listaDivergencias
        conexao.close()

    }

    fun getQtdProdtutos(login: Login): MutableList<QtdProduto> {
        val conexao = conexao(login)
        val sql = "SELECT * FROM quantidade_produto;"
        val quantidadeQuery = conexao!!.prepareStatement(sql)
        val resultado = quantidadeQuery.executeQuery()
        val listaQtdProduto = mutableListOf<QtdProduto>()

        while (resultado.next()){
            val qtdProduto = QtdProduto(
                resultado.getInt(1),
                resultado.getInt(2),
                resultado.getInt(3),
                resultado.getInt(4),
                resultado.getInt(5)
            )
            listaQtdProduto.add(qtdProduto)
        }
        return listaQtdProduto
        conexao.close()
    }

    fun addContagem(){
        //Adiciona a contagem no servidor
    }

    fun atualizarDivergencias(login: Login, lista:MutableList<Divergencia>){
        //adiciona novas divergencias caso não existam. Comparar pelo codigo do produto e qtd divergente,
        //se for diferente adiciona nova divergencia.
        val conexao = conexao(login)
        try {
            deleteDivergencias(conexao!!, lista[0])
        }catch (e:Exception){
            e.printStackTrace()}

        try {

            val insereDivergenciaSql = "INSERT INTO divergencias (id, cod_produto, categoria, qtd_divergencia)" +
                    " VALUES (?, ?, ?, ?);"
            val queryInserirDivergencia = conexao!!.prepareStatement(insereDivergenciaSql)
            lista.forEach {
                queryInserirDivergencia.setInt(1,it.id)
                queryInserirDivergencia.setInt(2, it.codProduto)
                queryInserirDivergencia.setString(3, it.categoria)
                queryInserirDivergencia.setInt(4, it.qtdDivergencia)
                queryInserirDivergencia.execute()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }finally {
            conexao!!.close()
        }

    }

    fun deleteDivergencias(conexao: Connection, divergencia: Divergencia){
        val sql = "DELETE FROM divergencias WHERE categoria = '${divergencia.categoria}';"
        val deleteDivergencia = conexao.prepareStatement(sql)
        deleteDivergencia.executeUpdate()
    }

    fun setQtdProduto(context: Context, login: Login, agendaContagem: AgendaContagem){
        val conexao = conexao(login)
        val listaProd = HelperBDLocal(context).getProdutosContagem(agendaContagem.categoria)
        val listaContagem = HelperBDLocal(context).getContagem(agendaContagem.id)

        listaProd.forEach {
            val codProd = it.cod_produto
            val listaContagemAtual = listaContagem.filter { it.codProduto == codProd }

            val totalNovo = listaContagemAtual.filter { it.tag == "NOVO" }.sumOf { it.qtd }
            val totalDanificado = listaContagemAtual.filter { it.tag =="DANIFICADO" }.sumOf { it.qtd }
            val totalMostruario = listaContagemAtual.filter { it.tag == "MOSTRUARIO" }.sumOf { it.qtd }

            if(listaContagemAtual.size != 0){
                val sql = "UPDATE quantidade_produto SET novo = ${totalNovo}, " +
                        "danificado = $totalDanificado, " +
                        "mostruario = $totalMostruario " +
                        "WHERE cod_produto = '${it.cod_produto}';"

                val quantidadeSet = conexao!!.prepareStatement(sql)
                quantidadeSet.executeUpdate()
            }else{

                val sql = "UPDATE quantidade_produto SET novo = 0, " +
                        "danificado = 0, " +
                        "mostruario = 0 " +
                        "WHERE cod_produto = '${it.cod_produto}';"

                val quantidadeSet = conexao!!.prepareStatement(sql)
                quantidadeSet.executeUpdate()
            }
        }
        conexao!!.close()
    }


    fun getNomeProd(conexao: Connection,filtro: String): String? {
        val sql = "SELECT nome FROM produtos WHERE cod_produto = ${filtro};"
        val produtosQuery = conexao!!.prepareStatement(sql)
        val resultado = produtosQuery.executeQuery()
        resultado.next()
        return resultado.getString(1)
        conexao.close()

    }

    companion object {
        //private const val DB_URL = "jdbc:postgresql://seuEndereço"
    }
}