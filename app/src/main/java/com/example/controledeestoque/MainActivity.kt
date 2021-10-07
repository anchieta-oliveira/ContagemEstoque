package com.example.controledeestoque

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.controledeestoque.helpers.BdServer
import com.example.controledeestoque.helpers.HelperBDLocal
import com.example.controledeestoque.helpers.Login
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        limparBDLocal()
        entrarClick()
    }

    fun entrarClick(){
        val buttonEntrar = findViewById<Button>(R.id.button_entrar)
        buttonEntrar.setOnClickListener {
           CoroutineScope(IO).launch {
               try {
                   conectarBD()
               }catch(e:Exception){

               }

           }
        }
    }

    fun conectarBD() {
        val editUsuario = findViewById<EditText>(R.id.edit_usuario)
        val editSenha = findViewById<EditText>(R.id.edit_senha)
        val usuario = editUsuario.text.toString()
        val senha = editSenha.text.toString()
        val login = Login(usuario, senha)

        val con = BdServer().conexao(login)

        val checkCon = con?.isValid(10)


        if (checkCon == true){
            val irHome = Intent(this@MainActivity, Home::class.java)
            irHome.putExtra(EXTRA_LOGIN, login)
            startActivity(irHome)
        }


    }
    fun limparBDLocal(){
        HelperBDLocal(this).deletarProdutosContagem()
    }


    companion object{
        const val EXTRA_LOGIN: String = "EXTRA_LOGIN"
    }

}