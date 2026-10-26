package lucas.labendzs.myapplication

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import lucas.labendzs.myapplication.controller.FilmeController
import lucas.labendzs.myapplication.dao.FilmeDAO
import lucas.labendzs.myapplication.model.Filme
import java.io.File
import java.io.FileOutputStream

class MainActivity2 : AppCompatActivity() {

    private lateinit var txtTitulo: TextView
    private lateinit var edtNome: EditText
    private lateinit var spinnerTipo: Spinner
    private lateinit var cbAssistido: CheckBox
    private lateinit var txtData: TextView
    private lateinit var imgPreview: ImageView
    private lateinit var btnSelecionarImagem: Button
    private lateinit var btnSalvar: Button
    private lateinit var btnCancelar: Button
    private lateinit var btnExcluir: Button

    private lateinit var controller: FilmeController
    private var filmeAtual: Filme? = null
    private var caminhoImagemSelecionada: String = ""

    companion object {
        private const val REQUEST_CODE_ESCOLHER_IMAGEM = 100
        private const val DIRETORIO_IMAGENS = "imagens_filmes"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        inicializarComponentes()

        val dao = FilmeDAO(this)
        controller = FilmeController(dao)

        val tipos = arrayOf("Filme", "Série")
        spinnerTipo.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, tipos)

        val filmeId = intent.getIntExtra("FILME_ID", -1)

        if (filmeId != -1) {
            modoEdicao(filmeId)
        } else {
            modoNovo()
        }

        configurarListeners()
    }

    private fun inicializarComponentes() {
        txtTitulo = findViewById(R.id.txtTitulo)
        edtNome = findViewById(R.id.edtNome)
        spinnerTipo = findViewById(R.id.spinnerTipo)
        cbAssistido = findViewById(R.id.cbAssistido)
        txtData = findViewById(R.id.txtData)
        imgPreview = findViewById(R.id.imgPreview)
        btnSelecionarImagem = findViewById(R.id.btnSelecionarImagem)
        btnSalvar = findViewById(R.id.btnSalvar)
        btnCancelar = findViewById(R.id.btnCancelar)
        btnExcluir = findViewById(R.id.btnExcluir)
    }

    private fun modoNovo() {
        txtTitulo.text = "Novo Filme"
        btnExcluir.visibility = android.view.View.GONE
        filmeAtual = null
        caminhoImagemSelecionada = ""

        imgPreview.setImageResource(R.drawable.ic_launcher_foreground)
        txtData.text = "--"
    }

    private fun modoEdicao(filmeId: Int) {
        txtTitulo.text = "Editar Filme"
        btnExcluir.visibility = android.view.View.VISIBLE

        filmeAtual = controller.buscarPorId(filmeId)

        if (filmeAtual != null) {
            val filme = filmeAtual!!

            edtNome.setText(filme.nome)
            spinnerTipo.setSelection(if (filme.tipo == "Série") 1 else 0)
            cbAssistido.isChecked = filme.assistido
            txtData.text = filme.dataAssistido ?: "--"
            caminhoImagemSelecionada = filme.imagem

            if (filme.imagem.isNotEmpty()) {
                carregarImagem(filme.imagem)
            } else {
                imgPreview.setImageResource(R.drawable.ic_launcher_foreground)
            }
        }
    }

    private fun configurarListeners() {
        btnSelecionarImagem.setOnClickListener {
            abrirSeletorImagem()
        }

        btnSalvar.setOnClickListener {
            salvarFilme()
        }

        btnCancelar.setOnClickListener {
            finish()
        }

        btnExcluir.setOnClickListener {
            mostrarDialogoExclusao()
        }

        cbAssistido.setOnCheckedChangeListener { buttonView, isChecked ->
        }
    }

    private fun abrirSeletorImagem() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_ESCOLHER_IMAGEM)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_ESCOLHER_IMAGEM && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                try {
                    caminhoImagemSelecionada = salvarImagemNoStorage(uri)
                    carregarImagem(caminhoImagemSelecionada)
                    Toast.makeText(this, "Imagem selecionada!", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(this, "Erro ao processar imagem: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun salvarImagemNoStorage(uri: Uri): String {
        val dirImagens = File(filesDir, DIRETORIO_IMAGENS)
        if (!dirImagens.exists()) {
            dirImagens.mkdirs()
        }

        val nomeArquivo = "filme_${System.currentTimeMillis()}.jpg"
        val arquivoImagem = File(dirImagens, nomeArquivo)

        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        val outputStream = FileOutputStream(arquivoImagem)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        outputStream.close()

        return arquivoImagem.absolutePath
    }

    private fun carregarImagem(caminhoImagem: String) {
        try {
            val arquivo = File(caminhoImagem)
            if (arquivo.exists()) {
                val bitmap = BitmapFactory.decodeFile(caminhoImagem)
                imgPreview.setImageBitmap(bitmap)
            } else {
                imgPreview.setImageResource(R.drawable.ic_launcher_foreground)
            }
        } catch (e: Exception) {
            imgPreview.setImageResource(R.drawable.ic_launcher_foreground)
        }
    }

    private fun salvarFilme() {
        val nome = edtNome.text.toString().trim()
        val tipo = spinnerTipo.selectedItem.toString()
        val assistido = cbAssistido.isChecked

        val filme = Filme(
            id = filmeAtual?.id ?: 0,
            nome = nome,
            tipo = tipo,
            imagem = caminhoImagemSelecionada,
            assistido = assistido,
            dataAssistido = filmeAtual?.dataAssistido
        )

        val filmeParaSalvar = if (filmeAtual != null) {
            filme.copy(id = filmeAtual!!.id)
        } else {
            filme
        }

        val resultado = if (filmeAtual != null) {
            controller.atualizar(filmeParaSalvar)
        } else {
            controller.inserir(filmeParaSalvar)
        }

        val (sucesso, mensagem) = resultado

        if (sucesso) {
            Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
            finish()
        } else {
            exibirErros(mensagem, filme)
        }
    }

    private fun exibirErros(erros: String, filme: Filme) {
        Toast.makeText(this, erros, Toast.LENGTH_LONG).show()

        if (erros.contains("Nome", ignoreCase = true)) {
            edtNome.requestFocus()
            edtNome.setError("Campo obrigatório")
        }
    }

    private fun mostrarDialogoExclusao() {
        if (filmeAtual == null) return

        AlertDialog.Builder(this)
            .setTitle("Confirmar exclusão")
            .setMessage("Tem certeza que deseja excluir '${filmeAtual!!.nome}'?")
            .setPositiveButton("Excluir") { dialog, which ->
                excluirFilme()
            }
            .setNegativeButton("Cancelar") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun excluirFilme() {
        if (filmeAtual == null) return

        val (sucesso, mensagem) = controller.excluir(filmeAtual!!.id)

        if (sucesso) {
            Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
        }
    }
}
