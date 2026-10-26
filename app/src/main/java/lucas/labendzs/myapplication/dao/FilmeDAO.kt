package lucas.labendzs.myapplication.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import lucas.labendzs.myapplication.database.FilmeDBHelper
import lucas.labendzs.myapplication.model.Filme

class FilmeDAO(context: Context) {

    private val dbHelper = FilmeDBHelper(context)

    fun inserir(filme: Filme): Long {
        val db = dbHelper.writableDatabase

        val valores = ContentValues().apply {
            put(FilmeDBHelper.COLUMN_NOME, filme.nome)
            put(FilmeDBHelper.COLUMN_TIPO, filme.tipo)
            put(FilmeDBHelper.COLUMN_IMAGEM, filme.imagem)
            put(FilmeDBHelper.COLUMN_ASSISTIDO, if (filme.assistido) 1 else 0)
            put(FilmeDBHelper.COLUMN_DATA_ASSISTIDO, filme.dataAssistido)
        }

        val novoId = db.insert(FilmeDBHelper.TABLE_NAME, null, valores)
        db.close()

        return novoId
    }

    fun listarTodos(): List<Filme> {
        val db = dbHelper.readableDatabase
        val filmes = mutableListOf<Filme>()

        val cursor: Cursor? = db.query(
            FilmeDBHelper.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            FilmeDBHelper.COLUMN_NOME + " ASC"
        )

        cursor?.use { c ->
            while (c.moveToNext()) {
                filmes.add(converterCursorParaFilme(c))
            }
        }

        db.close()
        return filmes
    }

    fun buscarPorId(id: Int): Filme? {
        val db = dbHelper.readableDatabase

        val cursor: Cursor? = db.query(
            FilmeDBHelper.TABLE_NAME,
            null,
            FilmeDBHelper.COLUMN_ID + " = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        var filme: Filme? = null

        cursor?.use { c ->
            if (c.moveToFirst()) {
                filme = converterCursorParaFilme(c)
            }
        }

        db.close()
        return filme
    }

    fun buscarPorNome(nome: String): List<Filme> {
        val db = dbHelper.readableDatabase
        val filmes = mutableListOf<Filme>()

        val cursor: Cursor? = db.query(
            FilmeDBHelper.TABLE_NAME,
            null,
            FilmeDBHelper.COLUMN_NOME + " LIKE ?",
            arrayOf("%$nome%"),
            null,
            null,
            FilmeDBHelper.COLUMN_NOME + " ASC"
        )

        cursor?.use { c ->
            while (c.moveToNext()) {
                filmes.add(converterCursorParaFilme(c))
            }
        }

        db.close()
        return filmes
    }

    fun listarPorStatus(assistido: Boolean): List<Filme> {
        val db = dbHelper.readableDatabase
        val filmes = mutableListOf<Filme>()

        val cursor: Cursor? = db.query(
            FilmeDBHelper.TABLE_NAME,
            null,
            FilmeDBHelper.COLUMN_ASSISTIDO + " = ?",
            arrayOf(if (assistido) "1" else "0"),
            null,
            null,
            FilmeDBHelper.COLUMN_NOME + " ASC"
        )

        cursor?.use { c ->
            while (c.moveToNext()) {
                filmes.add(converterCursorParaFilme(c))
            }
        }

        db.close()
        return filmes
    }

    fun listarComOrdenacao(ascendente: Boolean): List<Filme> {
        val db = dbHelper.readableDatabase
        val filmes = mutableListOf<Filme>()

        val ordenacao = if (ascendente)
            "${FilmeDBHelper.COLUMN_NOME} ASC"
        else
            "${FilmeDBHelper.COLUMN_NOME} DESC"

        val cursor: Cursor? = db.query(
            FilmeDBHelper.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            ordenacao
        )

        cursor?.use { c ->
            while (c.moveToNext()) {
                filmes.add(converterCursorParaFilme(c))
            }
        }

        db.close()
        return filmes
    }

    fun listarPorStatusComOrdenacao(assistido: Boolean, ascendente: Boolean): List<Filme> {
        val db = dbHelper.readableDatabase
        val filmes = mutableListOf<Filme>()

        val ordenacao = if (ascendente)
            "${FilmeDBHelper.COLUMN_NOME} ASC"
        else
            "${FilmeDBHelper.COLUMN_NOME} DESC"

        val cursor: Cursor? = db.query(
            FilmeDBHelper.TABLE_NAME,
            null,
            FilmeDBHelper.COLUMN_ASSISTIDO + " = ?",
            arrayOf(if (assistido) "1" else "0"),
            null,
            null,
            ordenacao
        )

        cursor?.use { c ->
            while (c.moveToNext()) {
                filmes.add(converterCursorParaFilme(c))
            }
        }

        db.close()
        return filmes
    }

    fun contarAssistidos(): Int {
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM ${FilmeDBHelper.TABLE_NAME} WHERE ${FilmeDBHelper.COLUMN_ASSISTIDO} = 1",
            null
        )

        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }

        cursor.close()
        db.close()

        return count
    }

    fun contarTotal(): Int {
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM ${FilmeDBHelper.TABLE_NAME}",
            null
        )

        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }

        cursor.close()
        db.close()

        return count
    }

    fun atualizar(filme: Filme): Int {
        val db = dbHelper.writableDatabase

        val valores = ContentValues().apply {
            put(FilmeDBHelper.COLUMN_NOME, filme.nome)
            put(FilmeDBHelper.COLUMN_TIPO, filme.tipo)
            put(FilmeDBHelper.COLUMN_IMAGEM, filme.imagem)
            put(FilmeDBHelper.COLUMN_ASSISTIDO, if (filme.assistido) 1 else 0)
            put(FilmeDBHelper.COLUMN_DATA_ASSISTIDO, filme.dataAssistido)
        }

        val linhasAtualizadas = db.update(
            FilmeDBHelper.TABLE_NAME,
            valores,
            FilmeDBHelper.COLUMN_ID + " = ?",
            arrayOf(filme.id.toString())
        )

        db.close()
        return linhasAtualizadas
    }

    fun excluir(id: Int): Int {
        val db = dbHelper.writableDatabase

        val linhasExcluidas = db.delete(
            FilmeDBHelper.TABLE_NAME,
            FilmeDBHelper.COLUMN_ID + " = ?",
            arrayOf(id.toString())
        )

        db.close()
        return linhasExcluidas
    }

    private fun converterCursorParaFilme(cursor: Cursor): Filme {
        return Filme(
            id = cursor.getInt(cursor.getColumnIndexOrThrow(FilmeDBHelper.COLUMN_ID)),
            nome = cursor.getString(cursor.getColumnIndexOrThrow(FilmeDBHelper.COLUMN_NOME)),
            tipo = cursor.getString(cursor.getColumnIndexOrThrow(FilmeDBHelper.COLUMN_TIPO)),
            imagem = cursor.getString(cursor.getColumnIndexOrThrow(FilmeDBHelper.COLUMN_IMAGEM)) ?: "",
            assistido = cursor.getInt(cursor.getColumnIndexOrThrow(FilmeDBHelper.COLUMN_ASSISTIDO)) == 1,
            dataAssistido = cursor.getString(cursor.getColumnIndexOrThrow(FilmeDBHelper.COLUMN_DATA_ASSISTIDO))
        )
    }
}

