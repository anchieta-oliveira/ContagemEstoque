package com.example.controledeestoque

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import com.example.controledeestoque.MainActivity.Companion.EXTRA_LOGIN
import com.example.controledeestoque.contagem.AgendaContagem
import com.example.controledeestoque.divergencia.DivergenciasPendentes
import com.example.controledeestoque.divergencia.GerarDivergencias
import com.example.controledeestoque.helpers.BdServer
import com.example.controledeestoque.helpers.Login
import com.example.controledeestoque.helpers.SincronizarBDs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ResumoContagem : AppCompatActivity() {
    private var agendaAtual: AgendaContagem? = null
    private var login: Login? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resumo_contagem)
        getExtraLogin()
        getExtraAgenda()
        sincronizarProdtusoContagem()
        animacaoBotao()
        clickContar()
        clickFinalizar()
    }

    fun clickContar(){
        val buttonContar = findViewById<FloatingActionButton>(R.id.menu_resumo_contar)
        buttonContar.setOnClickListener {
            val irContador = Intent(this, ContagemActivity::class.java)
            irContador.putExtra(EXTRA_AGENDA, agendaAtual)
            startActivity(irContador)
        }
    }

    fun clickFinalizar(){
        val buttonFinalizar = findViewById<FloatingActionButton>(R.id.menu_resumo_finalizar)
        buttonFinalizar.setOnClickListener {
            // colocar ações para finalziar contagem e gerar divergencias, enviar as divergencia para o bd
            val carregando = findViewById<RelativeLayout>(R.id.conteiner_carregando)
            carregando.visibility = View.VISIBLE
            CoroutineScope(IO).launch {
               // adicionar gerador de divergencia e enviar divergencia
                val divergencias = GerarDivergencias().gerarDivergencias(login!!, agendaAtual!!, this@ResumoContagem)
                BdServer().atualizarDivergencias(login!!,divergencias)
                BdServer().setQtdProduto(this@ResumoContagem, login!!,agendaAtual!!)
                withContext(Main){
                    val irFinalizar = Intent(this@ResumoContagem, DivergenciaActivity::class.java)
                    irFinalizar.putExtra(EXTRA_LOGIN, login)
                    irFinalizar.putExtra("categoria", agendaAtual!!.categoria)
                    startActivity(irFinalizar)
                    carregando.visibility = View.GONE
                }
            }
        }
    }

    fun animacaoBotao (){
        val buttonMenu = findViewById<FloatingActionButton>(R.id.manu_resumo_contagem)
        val buttonContar = findViewById<FloatingActionButton>(R.id.menu_resumo_contar)
        val buttonFinalizar = findViewById<FloatingActionButton>(R.id.menu_resumo_finalizar)
        var estadoButton = false

        buttonMenu.setOnClickListener {
            if (!estadoButton){
                buttonMenu.animate().rotation(45f)
                buttonContar.apply {
                    alpha = 0f
                    visibility = View.VISIBLE
                    animate()
                        .translationY(-20f)
                        .alpha(1f)
                        .setDuration(100)
                }
                buttonFinalizar.apply {
                    alpha = 0f
                    visibility = View.VISIBLE
                    animate()
                        .translationY(-20f)
                        .alpha(1f)
                        .setDuration(200)
                }
                estadoButton = true

            }else{
                buttonMenu.animate().rotation(0f)
                buttonContar.apply {
                    animate()
                        .translationY(10f)
                        .alpha(0f)
                        .setDuration(200)
                }
                buttonFinalizar.apply {

                    animate()
                        .translationY(10f)
                        .alpha(0f)
                        .setDuration(100)
                }
                estadoButton = false
            }

        }
    }

    fun getExtraAgenda(){
        agendaAtual = intent.getParcelableExtra(EXTRA_AGENDA)
    }

    fun sincronizarProdtusoContagem (){
        val carregando = findViewById<RelativeLayout>(R.id.conteiner_carregando)
        carregando.visibility = View.VISIBLE
        CoroutineScope(IO).launch{
            SincronizarBDs().sinProdutosContagem(login!!, this@ResumoContagem, agendaAtual!!)
            withContext(Main){
                carregando.visibility = View.GONE
                Toast.makeText(this@ResumoContagem, "BD sincronizado", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun getExtraLogin(){
        login = intent.getParcelableExtra(EXTRA_LOGIN)
    }

    companion object{
        const val EXTRA_AGENDA: String = "EXTRA_AGENDA"
    }

}