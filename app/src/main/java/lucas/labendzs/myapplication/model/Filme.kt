package lucas.labendzs.myapplication.model

data class Filme(
    val id: Int = 0,
    val nome: String = "",
    val tipo: String = "Filme",
    val imagem: String = "",
    val assistido: Boolean = false,
    val dataAssistido: String? = null
)

