package com.example.arquivorpg

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.Normalizer
import kotlin.random.Random

class ThirdActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)


        // Recupera os dados da ficha
        val nome = intent.getStringExtra("NOME") ?: "Nome não fornecido"
        val classe = intent.getStringExtra("CLASSE") ?: "Classe não fornecida"
        val raca = intent.getStringExtra("RACA") ?: "Raça não fornecida"
        val nivel = intent.getIntExtra("NIVEL", 1)


        val atributos = intent.getSerializableExtra("ATRIBUTOS") as? Map<String, Int> ?: emptyMap()

        val periciasMarcadas = intent.getStringArrayListExtra("PERICIAS_MARCADAS") ?: arrayListOf()

        val tvNome = findViewById<TextView>(R.id.tvNome)
        val tvClasse = findViewById<TextView>(R.id.tvClasse)
        val tvRaca = findViewById<TextView>(R.id.tvRaca)
        val tvNivel = findViewById<TextView>(R.id.tvNivel)
        val tvProficiencia = findViewById<TextView>(R.id.tvProficiencia)
        val tvPericias = findViewById<TextView>(R.id.tvPericias)

        atributos.forEach { (nomeAtributo, valorAtributo) ->
            atualizarAtributo(nomeAtributo, valorAtributo)
        }
        tvNome.text = "Nome: $nome"
        tvClasse.text = "Classe: $classe"
        tvRaca.text = "Raça: $raca"
        tvNivel.text = "Nível: $nivel"

        val modificadorProficiencia = kotlin.math.ceil(nivel.toDouble() / 4).toInt() // Exemplo, ajuste conforme necessário
        tvProficiencia.text = "Bônus de Proficiência: $modificadorProficiencia"

        // Exibe as perícias marcadas
        val periciasTexto = if (periciasMarcadas.isNotEmpty()) {
            periciasMarcadas.joinToString("\n") { it }
        } else {
            "Nenhuma perícia marcada"
        }
        tvPericias.text = periciasTexto

        val salvaguardasTexto = exibirSalvaguardas(classe, atributos, modificadorProficiencia)
        val tvSalvaguardas = findViewById<TextView>(R.id.tvSalvaGuarda)
        tvSalvaguardas.text = salvaguardasTexto

        val ivCharacter = findViewById<ImageView>(R.id.ivPersonagem)
        val btnLeft = findViewById<ImageButton>(R.id.btnAnterior)
        val btnRight = findViewById<ImageButton>(R.id.btnProximo)

        val imagens = obterImagensPorClasse(classe)
        var imagemAtual = 0

        atualizarImagem(ivCharacter, imagens, imagemAtual)

        btnLeft.setOnClickListener {
            imagemAtual = obterIndiceAnterior(imagemAtual, imagens.size)
            atualizarImagem(ivCharacter, imagens, imagemAtual)
        }

        btnRight.setOnClickListener {
            imagemAtual = obterIndiceProximo(imagemAtual, imagens.size)
            atualizarImagem(ivCharacter, imagens, imagemAtual)
        }

        val spinnerAtributo = findViewById<Spinner>(R.id.spinnerAtributo)
        val spinnerDado = findViewById<Spinner>(R.id.spinnerDado)
        val btnRolarDado = findViewById<Button>(R.id.btnRolarDado)
        val tvResultado = findViewById<TextView>(R.id.tvResultado)

        btnRolarDado.setOnClickListener {
            rolarDado(spinnerAtributo, spinnerDado, tvResultado, atributos, ::calcularModificador)
        }
    }

    private fun calcularModificador(valorAtributo: Int): Int {
        return kotlin.math.floor((valorAtributo - 10) / 2.0).toInt()
    }

    private fun atualizarAtributo(nomeAtributo: String, valorAtributo: Int) {
        val nomeAtributoSemAcento = removerAcentos(nomeAtributo)
        val modificador = calcularModificador(valorAtributo)

        val valorTextViewId = resources.getIdentifier("valor$nomeAtributoSemAcento", "id", packageName)
        val modTextViewId = resources.getIdentifier("mod$nomeAtributoSemAcento", "id", packageName)

        val valorTextView = findViewById<TextView>(valorTextViewId)
        val modTextView = findViewById<TextView>(modTextViewId)

        valorTextView.text = valorAtributo.toString()
        modTextView.text = modificador.toString()
    }

    private fun removerAcentos(texto: String): String {
        val semAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD).replace("[^~\\p{ASCII}]".toRegex(), "")

        return semAcentos.replace("ç", "c").replace("Ç", "C")    }

    private fun obterSalvaguardas(classe: String): List<String> {
        return when (classe.lowercase()) {
            "guerreiro" -> listOf("Destreza", "Força")
            "mago" -> listOf("Inteligência", "Sabedoria")
            "bárbaro" -> listOf("Força", "Constituição")
            "clérigo" -> listOf("Sabedoria", "Carisma")
            "ladino" -> listOf("Inteligência", "Destreza")
            else -> emptyList()
        }
    }

    private fun calcularSalvaguarda(valorAtributo: Int, modificadorProficiencia: Int): Int {
        val modificadorAtributo = calcularModificador(valorAtributo)
        return modificadorAtributo + modificadorProficiencia
    }

    private fun exibirSalvaguardas(
        classe: String,
        atributos: Map<String, Int>,
        bonusProficiencia: Int
    ): String {
        val salvaguardas = obterSalvaguardas(classe)
        return if (salvaguardas.isNotEmpty()) {
            salvaguardas.joinToString("\n") { atributo ->
                val valorAtributo = atributos[atributo] ?: 0
                val valorSalvaguarda = calcularSalvaguarda(valorAtributo, bonusProficiencia)
                "$atributo: ${if (valorSalvaguarda >= 0) "+$valorSalvaguarda" else valorSalvaguarda}"
            }
        } else {
            "Nenhuma salvaguarda disponível"
        }
    }

    private fun obterImagensPorClasse(classe: String): List<Int> {
        val imagensClasse: Map<String, List<Int>> = mapOf(
            "guerreiro" to listOf(R.drawable.guerreiro1, R.drawable.guerreiro2, R.drawable.guerreiro3),
            "mago" to listOf(R.drawable.mago1, R.drawable.mago2, R.drawable.mago3),
            "bárbaro" to listOf(R.drawable.barbaro1, R.drawable.barbaro2, R.drawable.barbaro3),
            "clérigo" to listOf(R.drawable.clerigo1, R.drawable.clerigo2, R.drawable.clerigo3),
            "ladino" to listOf(R.drawable.ladino1, R.drawable.ladino2, R.drawable.ladino3)
        )
        return imagensClasse[classe.lowercase()] ?: listOf(R.drawable.default_character)
    }

    private fun atualizarImagem(imageView: ImageView, imagens: List<Int>, indiceAtual: Int) {
        imageView.setImageResource(imagens[indiceAtual])
    }

    private fun obterIndiceAnterior(indiceAtual: Int, tamanhoLista: Int): Int {
        return if (indiceAtual == 0) tamanhoLista - 1 else indiceAtual - 1
    }

    private fun obterIndiceProximo(indiceAtual: Int, tamanhoLista: Int): Int {
        return (indiceAtual + 1) % tamanhoLista
    }

    private fun rolarDado(
        spinnerAtributo: Spinner,
        spinnerDado: Spinner,
        tvResultado: TextView,
        atributos: Map<String, Int>,
        calcularModificador: (Int) -> Int
    ) {
        val atributoSelecionado = spinnerAtributo.selectedItem.toString()
        val valorAtributo = atributos[atributoSelecionado] ?: 0
        val modificador = calcularModificador(valorAtributo)

        val dadoSelecionadoStr = spinnerDado.selectedItem.toString() // Ex.: "d6"
        val dadoSelecionado = dadoSelecionadoStr.substring(1).toInt() // Extrai o número após "d"
        val dadoRolado = Random.nextInt(1, dadoSelecionado + 1)

        val resultadoFinal = dadoRolado + modificador
        tvResultado.text = "Resultado: $dadoRolado + $modificador = $resultadoFinal"
    }
}