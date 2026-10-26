package lucas.labendzs.myapplication

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import lucas.labendzs.myapplication.controller.FilmeController
import lucas.labendzs.myapplication.dao.FilmeDAO
import lucas.labendzs.myapplication.model.Filme
import lucas.labendzs.myapplication.ui.FilmeAdapter

class MainActivity3 : AppCompatActivity() {

    private lateinit var listViewFilmes: ListView
    private lateinit var txtProgresso: TextView
    private lateinit var spinnerFiltro: Spinner
    private lateinit var spinnerOrdenacao: Spinner
    private lateinit var edtBuscar: EditText
    private lateinit var btnAdicionar: Button

    private lateinit var controller: FilmeController
    private lateinit var adapter: FilmeAdapter
    private var filmesCurrent: List<Filme> = listOf()

    private var filtroAtual = FiltroStatus.TODOS
    private var ordenacaoAtual = Ordenacao.A_Z

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        inicializarComponentes()

        val dao = FilmeDAO(this)
        controller = FilmeController(dao)

        filmesCurrent = controller.listarTodos()
        adapter = FilmeAdapter(this, filmesCurrent)
        listViewFilmes.adapter = adapter

        configurarListeners()

        atualizarLista()
    }

    override fun onResume() {
        super.onResume()
        filtroAtual = FiltroStatus.TODOS
        ordenacaoAtual = Ordenacao.A_Z
        spinnerFiltro.setSelection(0)
        spinnerOrdenacao.setSelection(0)
        edtBuscar.text.clear()
        atualizarLista()
    }

    private fun inicializarComponentes() {
        listViewFilmes = findViewById(R.id.listViewFilmes)
        txtProgresso = findViewById(R.id.txtProgresso)
        spinnerFiltro = findViewById(R.id.spinnerFiltro)
        spinnerOrdenacao = findViewById(R.id.spinnerOrdenacao)
        edtBuscar = findViewById(R.id.edtBuscar)
        btnAdicionar = findViewById(R.id.btnAdicionar)
    }

    private fun configurarListeners() {
        val opcoesFilto = arrayOf("Todos", "Assistidos", "Não assistidos")
        spinnerFiltro.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opcoesFilto)
        spinnerFiltro.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                filtroAtual = when (position) {
                    0 -> FiltroStatus.TODOS
                    1 -> FiltroStatus.ASSISTIDOS
                    else -> FiltroStatus.NAO_ASSISTIDOS
                }
                atualizarLista()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val opcoesOrdenacao = arrayOf("A-Z", "Z-A")
        spinnerOrdenacao.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opcoesOrdenacao)
        spinnerOrdenacao.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                ordenacaoAtual = if (position == 0) Ordenacao.A_Z else Ordenacao.Z_A
                atualizarLista()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        edtBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                atualizarLista()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        listViewFilmes.setOnItemClickListener { parent, view, position, id ->
            val filme = adapter.getItem(position)
            val intent = Intent(this, MainActivity2::class.java)
            intent.putExtra("FILME_ID", filme.id)
            startActivity(intent)
        }

        btnAdicionar.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }
    }

    private fun atualizarLista() {
        val textoBusca = edtBuscar.text.toString().trim()

        filmesCurrent = when (filtroAtual) {
            FiltroStatus.TODOS -> {
                controller.listarComOrdenacao(ordenacaoAtual == Ordenacao.A_Z)
            }
            FiltroStatus.ASSISTIDOS -> {
                controller.listarPorStatusComOrdenacao(true, ordenacaoAtual == Ordenacao.A_Z)
            }
            FiltroStatus.NAO_ASSISTIDOS -> {
                controller.listarPorStatusComOrdenacao(false, ordenacaoAtual == Ordenacao.A_Z)
            }
        }

        if (textoBusca.isNotEmpty()) {
            filmesCurrent = filmesCurrent.filter { filme ->
                filme.nome.contains(textoBusca, ignoreCase = true)
            }
        }

        adapter.atualizarLista(filmesCurrent)

        atualizarProgresso()
    }

    private fun atualizarProgresso() {
        val mensagem = controller.obterMensagemEstatisticas()
        txtProgresso.text = mensagem
    }

    private enum class FiltroStatus {
        TODOS, ASSISTIDOS, NAO_ASSISTIDOS
    }

    private enum class Ordenacao {
        A_Z, Z_A
    }
}
