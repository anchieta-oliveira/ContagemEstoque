package com.example.controledeestoque.helpers

import android.content.Context
import com.example.controledeestoque.contagem.AgendaContagem
import com.example.controledeestoque.divergencia.Divergencia

class SincronizarBDs() {
    //Baixar infos do BDserver e inserir no BD local: (1) ProdutosContagem;
    //(2) ...

    fun sinProdutosContagem(login: Login, context: Context,agendaContagem: AgendaContagem){
       try {
           val listaProdServer = BdServer().getProdutosContagem(login, agendaContagem.categoria)
           HelperBDLocal(context).addProdutosContagem(listaProdServer)
       }catch (e:Exception){
           e.printStackTrace()
       }
    }

}