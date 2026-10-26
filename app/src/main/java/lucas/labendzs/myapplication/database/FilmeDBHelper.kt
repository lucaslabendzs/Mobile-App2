package lucas.labendzs.myapplication.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class FilmeDBHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_NAME = "filme.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_NAME = "filmes"
        const val COLUMN_ID = "id"
        const val COLUMN_NOME = "nome"
        const val COLUMN_TIPO = "tipo"
        const val COLUMN_IMAGEM = "imagem"
        const val COLUMN_ASSISTIDO = "assistido"
        const val COLUMN_DATA_ASSISTIDO = "data_assistido"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableSQL = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOME TEXT NOT NULL,
                $COLUMN_TIPO TEXT NOT NULL,
                $COLUMN_IMAGEM TEXT,
                $COLUMN_ASSISTIDO INTEGER NOT NULL DEFAULT 0,
                $COLUMN_DATA_ASSISTIDO TEXT
            )
        """.trimIndent()

        db.execSQL(createTableSQL)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }
}

