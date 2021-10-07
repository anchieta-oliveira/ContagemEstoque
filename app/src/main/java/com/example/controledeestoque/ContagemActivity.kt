package com.example.controledeestoque

import android.Manifest.permission.CAMERA
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.controledeestoque.adapters.AdapterContador
import com.example.controledeestoque.barcode.BarcodeAnalyzer
import com.example.controledeestoque.contagem.AgendaContagem
import com.example.controledeestoque.contagem.Contagem
import com.example.controledeestoque.helpers.HelperBDLocal
import com.example.controledeestoque.produto.Produto
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_contagem.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

class ContagemActivity : AppCompatActivity() {
    private var estadobotao = false
    private var tagContagem:String = "NOVO"
    private var processingBarcode = AtomicBoolean(false)
    private lateinit var cameraExecutor: ExecutorService
    private val listaContagem = mutableListOf<Contagem>()
    private var agendaAtual: AgendaContagem? = null
    private val listaProdutos = mutableListOf<Produto>()


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contagem)

        getExtraAgenda()
        getListaProdutosContagem()
        clickMenu()
        clickTag()
        iniciarCamera()
        clickSalvar()

    }


    fun getListaProdutosContagem() {
       val lista = HelperBDLocal(this).getProdutosContagem(agendaAtual!!.categoria)
        listaProdutos.addAll(lista)
    }


    fun iniciarRecycle() {
        val recycleContagem = findViewById<RecyclerView>(R.id.recycle_contador)
        val linearLayoutManeger = LinearLayoutManager(this)

        val contadorAdapter = AdapterContador(mutableListOf(), this){
            // events de click, será uma caixa de dialogo para alterar a tag e qtd.
            clickEdit(it)

        }

        contadorAdapter.updateList(listaContagem.asReversed())
        recycleContagem.adapter = contadorAdapter

    }

    fun verificaProdutoContagem(codBarLeitura:String) {
        // verifica se o codigo de barras é equivalente a algum produto da lista de produtos de contagem
        val produto = listaProdutos.filter { it.cod_barras.toString() == codBarLeitura }
        if (produto.isNotEmpty()){
            addProdutoContagem(produto[0])
            Toast.makeText(this, "Produto ${produto[0].nome} adicionado!", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "Produto não faz parte da contagem!", Toast.LENGTH_SHORT).show()

        }
        Thread{
            Thread.sleep(1000)
            processingBarcode.set(false)
        }.start()
    }


    fun addProdutoContagem(produto:Produto) {
        // adiciona produto a lista de contagem
        val contagem = Contagem(agendaAtual!!.id, produto.cod_produto, 1, tagContagem, produto) // montando contagem que vai para o bd, falta o id da agenda
        listaContagem.add(contagem)
        iniciarRecycle()
    }


    @RequiresApi(Build.VERSION_CODES.M)
    fun iniciarCamera(){
        if (allPermissionsGranted()) {
            camera()
        } else {
            requestPermissions(
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                camera()
            } else {
                Toast.makeText(
                    this,
                    "Permissão negada!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    fun camera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(this))
    }

    fun bindPreview(cameraProvider : ProcessCameraProvider) {
        // ler codigo de barras
        cameraExecutor = Executors.newSingleThreadExecutor()
        val imageAnalysis = ImageAnalysis.Builder()
            .build()
            .also {
                it.setAnalyzer(cameraExecutor, BarcodeAnalyzer { barCode ->
                    if (processingBarcode.compareAndSet(false, true)) {
                        //atividade a ser executada quando for indentificado um bar code
                        verificaProdutoContagem(barCode)
                    }
                })
            }
        // bindPreview propriamente dito
        var preview : Preview = Preview.Builder()
            .build()

        var cameraSelector : CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        preview.setSurfaceProvider(scan_contagem.getSurfaceProvider())

        var camera = cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview, imageAnalysis)
    }

    fun clickSalvar(){
        // clique para salavr a lista de produtos contados ao BD local de contagem e voltar para tela resumo
        val botoaSalvar = findViewById<FloatingActionButton>(R.id.menu_contagem_salvar)
        botoaSalvar.setOnClickListener {
            Toast.makeText(this@ContagemActivity, "Salvando", Toast.LENGTH_SHORT).show()
            CoroutineScope(IO).launch {
                HelperBDLocal(this@ContagemActivity).salvarContagem(listaContagem)
                withContext(Main){
                    finish()
                }
            }
        }


    }

    fun clickTag(){
        val tagNovo = findViewById<FloatingActionButton>(R.id.tag_novo)
        val tagDanificado = findViewById<FloatingActionButton>(R.id.tag_danificado)
        val tagMostruario = findViewById<FloatingActionButton>(R.id.tag_mostruario)

        tagNovo.setOnClickListener {
            tagContagem = "NOVO"
            fecharotao()
        }

        tagDanificado.setOnClickListener {
            tagContagem = "DANIFICADO"
            fecharotao()
        }

        tagMostruario.setOnClickListener {
            tagContagem = "MOSTRUARIO"
            fecharotao()
        }

    }

    fun clickMenu() {
        val botaoMenu = findViewById<FloatingActionButton>(R.id.menu_contagem)

        botaoMenu.setOnClickListener {
            if (!estadobotao) {
                expandirBotao()
            } else {
                fecharotao()
            }

        }

    }


    fun expandirBotao() {
        val tagNovo = findViewById<FloatingActionButton>(R.id.tag_novo)
        val tagDanificado = findViewById<FloatingActionButton>(R.id.tag_danificado)
        val tagMostruario = findViewById<FloatingActionButton>(R.id.tag_mostruario)
        val botoaSalvar = findViewById<FloatingActionButton>(R.id.menu_contagem_salvar)
        val telaEscura = findViewById<LinearLayout>(R.id.conteiner_escuro)
        val botaoMenu = findViewById<FloatingActionButton>(R.id.menu_contagem)

        estadobotao = true

        botaoMenu.animate().rotation(45f)
        telaEscura.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .alpha(0.15f)
                .setDuration(20)
        }


        tagNovo.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .translationY(-16f)
                .alpha(1f)
                .setDuration(100)
        }
        tagDanificado.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .translationY(-16f)
                .alpha(1f)
                .setDuration(150)
        }

        tagMostruario.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .translationY(-16f)
                .alpha(1f)
                .setDuration(200)

            botoaSalvar.apply {
                alpha = 0f
                visibility = View.VISIBLE
                animate()
                    .translationX(-16f)
                    .alpha(1f)
                    .setDuration(150)
            }

        }

    }

    fun fecharotao() {
        val tagNovo = findViewById<FloatingActionButton>(R.id.tag_novo)
        val tagDanificado = findViewById<FloatingActionButton>(R.id.tag_danificado)
        val tagMostruario = findViewById<FloatingActionButton>(R.id.tag_mostruario)
        val botoaSalvar = findViewById<FloatingActionButton>(R.id.menu_contagem_salvar)
        val telaEscura = findViewById<LinearLayout>(R.id.conteiner_escuro)
        val botaoMenu = findViewById<FloatingActionButton>(R.id.menu_contagem)

        botaoMenu.animate().rotation(0f)
        estadobotao = false
        telaEscura.apply {
            alpha = 1f
            animate()
                .alpha(0f)
                .setDuration(200)
            visibility = View.GONE
        }


        tagNovo.apply {
            animate()
                .translationY(10f)
                .alpha(0f)
                .setDuration(200)
        }
        tagDanificado.apply {
            animate()
                .translationY(10f)
                .alpha(0f)
                .setDuration(150)
        }

        tagMostruario.apply {
            animate()
                .translationY(10f)
                .alpha(0f)
                .setDuration(100)

            botoaSalvar.apply {
                animate()
                    .translationX(10f)
                    .alpha(0f)
                    .setDuration(200)
            }
        }

    }

    fun clickEdit(contagem: Contagem){
        val conteiner = findViewById<ConstraintLayout>(R.id.conteiner_edit_contagem_geral)
        val textNome = findViewById<TextView>(R.id.edit_contagem_Nome_prod)
        val editQtd = findViewById<EditText>(R.id.edit_qtd)
        val botExcluir = findViewById<Button>(R.id.edit_cont_bot_excluir)
        val botSalvar = findViewById<Button>(R.id.edit_cont_bot_salvar)
        val botCancelar =findViewById<Button>(R.id.edit_cont_bot_cancelar)
        val labelNovo = findViewById<Button>(R.id.edit_label_novo)
        val labelDanificado = findViewById<Button>(R.id.edit_label_danificado)
        val labelMostruarioo = findViewById<Button>(R.id.edit_label_mostruario)


        textNome.text = contagem.produto!!.nome
        editQtd.setText(contagem.qtd.toString())
        fun fecharConteiner(){
            conteiner.apply {
                animate().alpha(0f).setDuration(200)
                visibility = View.GONE
            }
        }

        botExcluir.setOnClickListener {
            listaContagem.remove(contagem)
            iniciarRecycle()
            fecharConteiner()
        }

        botSalvar.setOnClickListener {
            listaContagem.remove(contagem)
            listaContagem.add(Contagem(contagem.id, contagem.codProduto, editQtd.text.toString().toInt(), contagem.tag, contagem.produto))
            iniciarRecycle()
            fecharConteiner()
        }

        labelNovo.setOnClickListener {
            listaContagem.remove(contagem)
            listaContagem.add(Contagem(contagem.id, contagem.codProduto, editQtd.text.toString().toInt(), "NOVO", contagem.produto))
            iniciarRecycle()
            fecharConteiner()
        }

        labelDanificado.setOnClickListener {
            listaContagem.remove(contagem)
            listaContagem.add(Contagem(contagem.id, contagem.codProduto, editQtd.text.toString().toInt(), "DANIFICADO", contagem.produto))
            iniciarRecycle()
            fecharConteiner()
        }

        labelMostruarioo.setOnClickListener {
            listaContagem.remove(contagem)
            listaContagem.add(Contagem(contagem.id, contagem.codProduto, editQtd.text.toString().toInt(), "MOSTRUARIO", contagem.produto))
            iniciarRecycle()
            fecharConteiner()

        }

        botCancelar.setOnClickListener {
            fecharConteiner()
        }
        conteiner.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate().alpha(1f).setDuration(200)
        }

    }
    fun getExtraAgenda(){
        agendaAtual = intent.getParcelableExtra(ResumoContagem.EXTRA_AGENDA)
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}