package lucas.labendzs.myapplication.controller

import android.text.TextUtils
import lucas.labendzs.myapplication.dao.FilmeDAO
import lucas.labendzs.myapplication.model.Filme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FilmeController(private val dao: FilmeDAO) {

    companion object {
        private const val NOME_MIN_LENGTH = 1
        private const val NOME_MAX_LENGTH = 100
        private const val FORMATO_DATA = "dd/MM/yyyy"
    }

    fun validar(filme: Filme, isEdicao: Boolean = false): Map<String, String> {
        val erros = mutableMapOf<String, String>()

        if (filme.nome.isBlank()) {
            erros["nome"] = "Nome é obrigatório"
        } else if (filme.nome.length < NOME_MIN_LENGTH) {
            erros["nome"] = "Nome deve ter pelo menos $NOME_MIN_LENGTH caractere"
        } else if (filme.nome.length > NOME_MAX_LENGTH) {
            erros["nome"] = "Nome não pode ultrapassar $NOME_MAX_LENGTH caracteres"
        }

        if (filme.nome.isNotBlank()) {
            val nomeLowercase = filme.nome.lowercase()
            val filmeExistente = dao.buscarPorNome(filme.nome).firstOrNull()

            if (!isEdicao) {
                if (filmeExistente != null) {
                    erros["nome"] = "Já existe um filme/série com este nome"
                }
            } else {
                if (filmeExistente != null && filmeExistente.id != filme.id) {
                    erros["nome"] = "Já existe outro filme/série com este nome"
                }
            }
        }

        if (filme.tipo.isBlank()) {
            erros["tipo"] = "Tipo é obrigatório"
        } else if (filme.tipo !in listOf("Filme", "Série")) {
            erros["tipo"] = "Tipo deve ser 'Filme' ou 'Série'"
        }

        if (!filme.dataAssistido.isNullOrBlank()) {
            if (!validarFormatoData(filme.dataAssistido)) {
                erros["data"] = "Data deve estar no formato dd/MM/yyyy"
            }
        }

        return erros
    }

    private fun validarFormatoData(data: String): Boolean {
        return try {
            val formato = SimpleDateFormat(FORMATO_DATA, Locale("pt", "BR"))
            formato.isLenient = false
            formato.parse(data)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun inserir(filme: Filme): Pair<Boolean, String> {
        val erros = validar(filme, isEdicao = false)
        if (erros.isNotEmpty()) {
            val mensagem = erros.values.joinToString("\n")
            return Pair(false, mensagem)
        }

        val filmeComData = if (filme.assistido && filme.dataAssistido == null) {
            filme.copy(dataAssistido = obterDataAtual())
        } else {
            filme
        }

        val id = dao.inserir(filmeComData)
        return if (id > 0) {
            Pair(true, "Filme adicionado com sucesso!")
        } else {
            Pair(false, "Erro ao adicionar filme. Tente novamente.")
        }
    }

    fun atualizar(filme: Filme): Pair<Boolean, String> {
        val erros = validar(filme, isEdicao = true)
        if (erros.isNotEmpty()) {
            val mensagem = erros.values.joinToString("\n")
            return Pair(false, mensagem)
        }

        val filmeAtualizado = when {
            filme.assistido && filme.dataAssistido == null -> {
                filme.copy(dataAssistido = obterDataAtual())
            }
            !filme.assistido && filme.dataAssistido != null -> {
                filme.copy(dataAssistido = null)
            }
            else -> filme
        }

        val linhasAtualizadas = dao.atualizar(filmeAtualizado)
        return if (linhasAtualizadas > 0) {
            Pair(true, "Filme atualizado com sucesso!")
        } else {
            Pair(false, "Erro ao atualizar filme.")
        }
    }

    fun excluir(id: Int): Pair<Boolean, String> {
        val linhasExcluidas = dao.excluir(id)
        return if (linhasExcluidas > 0) {
            Pair(true, "Filme deletado com sucesso!")
        } else {
            Pair(false, "Erro ao deletar filme.")
        }
    }

    fun listarTodos(): List<Filme> {
        return dao.listarTodos()
    }

    fun buscarPorNome(nome: String): List<Filme> {
        return if (nome.isBlank()) {
            listOf()
        } else {
            dao.buscarPorNome(nome)
        }
    }

    fun listarPorStatus(assistido: Boolean): List<Filme> {
        return dao.listarPorStatus(assistido)
    }

    fun listarComOrdenacao(ascendente: Boolean): List<Filme> {
        return dao.listarComOrdenacao(ascendente)
    }

    fun listarPorStatusComOrdenacao(assistido: Boolean, ascendente: Boolean): List<Filme> {
        return dao.listarPorStatusComOrdenacao(assistido, ascendente)
    }

    fun buscarPorId(id: Int): Filme? {
        return dao.buscarPorId(id)
    }

    fun obterEstatisticas(): EstatisticasFilme {
        val total = dao.contarTotal()
        val assistidos = dao.contarAssistidos()

        val percentual = if (total > 0) {
            (assistidos * 100) / total
        } else {
            0
        }

        return EstatisticasFilme(
            totalFilmes = total,
            filmesAssistidos = assistidos,
            percentualAssistido = percentual
        )
    }

    fun obterMensagemEstatisticas(): String {
        val stats = obterEstatisticas()
        return "${stats.filmesAssistidos} de ${stats.totalFilmes} assistidos — ${stats.percentualAssistido}%"
    }

    private fun obterDataAtual(): String {
        val calendario = Calendar.getInstance()
        val formato = SimpleDateFormat(FORMATO_DATA, Locale("pt", "BR"))
        return formato.format(calendario.time)
    }

    data class EstatisticasFilme(
        val totalFilmes: Int,
        val filmesAssistidos: Int,
        val percentualAssistido: Int
    )
}

