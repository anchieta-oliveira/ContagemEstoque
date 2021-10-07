package com.example.controledeestoque

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.controledeestoque.MainActivity.Companion.EXTRA_LOGIN
import com.example.controledeestoque.ResumoContagem.Companion.EXTRA_AGENDA
import com.example.controledeestoque.adapters.AdapterDivergencia
import com.example.controledeestoque.contagem.AgendaContagem
import com.example.controledeestoque.divergencia.Divergencia
import com.example.controledeestoque.helpers.BdServer
import com.example.controledeestoque.helpers.Login
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Text

class DivergenciaActivity : AppCompatActivity() {
    private var divergenciaAtual: String? = null
    private var login: Login? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_divergencia)
        val carregando = findViewById<RelativeLayout>(R.id.conteiner_carregando)
        carregando.visibility = View.VISIBLE
        getCategoriaDivergencia()
        getExtraLogin()
        CoroutineScope(IO).launch {
            val listaDivergencias = getDivergencias()

            withContext(Main){
                recycleDivergencias(listaDivergencias)
                setText(listaDivergencias)
                carregando.visibility = View.GONE
            }
        }
    }

    fun recycleDivergencias(listaDivergencia: MutableList<Divergencia>){
        val recycleDiver = findViewById<RecyclerView>(R.id.recycle_divergencias)
        val adapterDiv = AdapterDivergencia(listaDivergencia, this, login!!){

        }
        recycleDiver.adapter = adapterDiv
    }

    fun getDivergencias(): MutableList<Divergencia> {
        val lista = BdServer().getDivergencia(login!!)
        return lista.filter { it.categoria == divergenciaAtual } as MutableList<Divergencia>

    }

    fun getExtraLogin(){
        login = intent.getParcelableExtra(EXTRA_LOGIN)
    }

    fun getCategoriaDivergencia(){
        divergenciaAtual = intent.getStringExtra("categoria")
    }
    fun setText(data:MutableList<Divergencia>){
        val textCategoria = findViewById<TextView>(R.id.titulo_categoria_divergencia)
        val textNumeroDiv = findViewById<TextView>(R.id.titulo_nuemro_divergencias)
        textCategoria.text = divergenciaAtual
        textNumeroDiv.text = "Nº Divergencias: " + data.size.toString()

    }
}