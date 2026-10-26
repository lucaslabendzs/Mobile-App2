package lucas.labendzs.myapplication.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import lucas.labendzs.myapplication.R
import lucas.labendzs.myapplication.model.Filme
import java.io.File

class FilmeAdapter(
    private val context: Context,
    private var filmes: List<Filme>
) : BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return filmes.size
    }

    override fun getItem(position: Int): Filme {
        return filmes[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: inflater.inflate(R.layout.item_filme, parent, false)

        val imgFilme: ImageView = view.findViewById(R.id.imgFilme)
        val txtNome: TextView = view.findViewById(R.id.txtNome)
        val txtTipo: TextView = view.findViewById(R.id.txtTipo)
        val txtStatus: TextView = view.findViewById(R.id.txtStatus)

        val filme = filmes[position]

        txtNome.text = filme.nome
        txtTipo.text = filme.tipo

        if (filme.assistido) {
            txtStatus.text = "✓ Assistido - ${filme.dataAssistido}"
            txtStatus.setTextColor(context.resources.getColor(android.R.color.holo_green_dark, null))
        } else {
            txtStatus.text = "✗ Não assistido"
            txtStatus.setTextColor(context.resources.getColor(android.R.color.holo_red_dark, null))
        }

        carregarImagem(imgFilme, filme.imagem)

        return view
    }

    fun atualizarLista(novosList: List<Filme>) {
        this.filmes = novosList
        notifyDataSetChanged()
    }

    private fun carregarImagem(imageView: ImageView, caminhoImagem: String) {
        if (caminhoImagem.isNotEmpty()) {
            val arquivo = File(caminhoImagem)
            if (arquivo.exists()) {
                val bitmap = android.graphics.BitmapFactory.decodeFile(caminhoImagem)
                imageView.setImageBitmap(bitmap)
            } else {
                imageView.setImageResource(R.drawable.ic_launcher_foreground)
            }
        } else {
            imageView.setImageResource(R.drawable.ic_launcher_foreground)
        }
    }
}

