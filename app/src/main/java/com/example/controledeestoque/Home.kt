package com.example.controledeestoque

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.controledeestoque.MainActivity.Companion.EXTRA_LOGIN
import com.example.controledeestoque.ResumoContagem.Companion.EXTRA_AGENDA
import com.example.controledeestoque.adapters.AdapterAgenda
import com.example.controledeestoque.adapters.AdapterMenuDivergencia
import com.example.controledeestoque.contagem.AgendaContagem
import com.example.controledeestoque.divergencia.DivergenciasPendentes
import com.example.controledeestoque.divergencia.GerarDivergencias
import com.example.controledeestoque.helpers.BdServer
import com.example.controledeestoque.helpers.GraficoResumo
import com.example.controledeestoque.helpers.Login
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.Main

class Home : AppCompatActivity() {
    private var login:Login? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val carregando = findViewById<RelativeLayout>(R.id.conteiner_carregando)
        carregando.visibility = View.VISIBLE
        getExtraLogin()

        CoroutineScope(Default).launch{
            val agenda= carregarAgendaContagem()
            val divergencias = carregarDivergenciasPendentes()
            withContext(Main){
                carregarRecycleDivergencias(divergencias)
                setText(divergencias)
                carregarRecycleAgenda(agenda)
                graficoResumo(divergencias)
                carregando.visibility = View.GONE
            }
        }
    }

    fun carregarRecycleDivergencias(listaDivergencias:MutableList<DivergenciasPendentes>){
        val recycleDivergencia = findViewById<RecyclerView>(R.id.recycle_divergencias_pendentes)
        val divergenciaAdapter = AdapterMenuDivergencia(listaDivergencias, this){
            // eventos de click
            val irDivergencias = Intent(this, DivergenciaActivity::class.java)
            irDivergencias.putExtra(EXTRA_LOGIN, login)
            irDivergencias.putExtra("categoria", it.categoria)
            startActivity(irDivergencias)
        }
        recycleDivergencia.adapter = divergenciaAdapter
    }

    fun carregarRecycleAgenda(listaAgenda: MutableList<AgendaContagem>){
        val recycleAgendaContagem = findViewById<RecyclerView>(R.id.recycle_agenda_conategem)
        val agendaContagem = AdapterAgenda(listaAgenda, this){
            //eventos de click
            val irResumoContagem = Intent(this, ResumoContagem::class.java)
            //enviar infos da contagem para o resumo
            irResumoContagem.putExtra(EXTRA_AGENDA, it)
            irResumoContagem.putExtra(EXTRA_LOGIN, login)
            startActivity(irResumoContagem)
        }
        recycleAgendaContagem.adapter = agendaContagem
    }

    fun carregarDivergenciasPendentes(): MutableList<DivergenciasPendentes> {
        val lista = BdServer().getDivergencia(login!!)
        val divergenciasPendentes = GerarDivergencias().getDivergenciasPendentes(lista)
        return divergenciasPendentes
    }

    fun carregarAgendaContagem():MutableList<AgendaContagem>{
        return BdServer().getAgendaContagem(login!!)
    }

    fun getExtraLogin(){
        login = intent.getParcelableExtra(EXTRA_LOGIN)
    }
    fun setText(divergenciasPendentes: MutableList<DivergenciasPendentes>){
        val textDivTotal = findViewById<TextView>(R.id.text_desempenho_total)
        textDivTotal.text = "Divergencia Total:" + divergenciasPendentes.sumOf { it.desempenho }.toString()

    }

    fun graficoResumo(data:MutableList<DivergenciasPendentes>){
        val graficoResumo = findViewById<AAChartView>(R.id.grafico_pizza_divergencia_setor)
        graficoResumo.aa_drawChartWithChartModel(GraficoResumo().graficoPizza(data))
    }
}