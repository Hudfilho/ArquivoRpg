package com.example.arquivorpg

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SecondActivity : AppCompatActivity() {
    data class Pericia(
        val checkBoxId: Int,
        val textViewId: Int,
        val nome: String,
        val atributoAssociado: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val etNome = findViewById<EditText>(R.id.etNome)
        val spnClasse = findViewById<Spinner>(R.id.spnClasse)
        val spnRaca = findViewById<Spinner>(R.id.spnRaca)
        val btnSalvar = findViewById<Button>(R.id.btnSalvar)

        val etNivel = findViewById<EditText>(R.id.etNivel)
        val tvModProficiencia = findViewById<TextView>(R.id.tvModProficiencia)

        val atributos = listOf(
            R.id.etForca to R.id.tvModForca,
            R.id.etDestreza to R.id.tvModDestreza,
            R.id.etConstituicao to R.id.tvModConstituicao,
            R.id.etInteligencia to R.id.tvModInteligencia,
            R.id.etSabedoria to R.id.tvModSabedoria,
            R.id.etCarisma to R.id.tvModCarisma
        )

        val pericias = listOf(
            Pericia(R.id.cbAtletismo, R.id.tvAtletismo, "Atletismo", "Força"),
            Pericia(R.id.cbFurtividade, R.id.tvFurtividade, "Furtividade", "Destreza"),
            Pericia(R.id.cbArcanismo, R.id.tvArcanismo, "Arcanismo", "Inteligência"),
            Pericia(R.id.cbPerformance, R.id.tvPerformance, "Performance", "Carisma"),
            Pericia(R.id.cbSobrevivencia, R.id.tvSobrevivencia, "Sobrevivência", "Sabedoria")
        )

        for ((editTextId, textViewId) in atributos) {
            configurarListenerAtributo(
                findViewById(editTextId),
                findViewById(textViewId)
            ) {
                atualizarPericias(pericias)
            }
        }

        configurarListenerModProficiencia(etNivel, tvModProficiencia) {
            atualizarPericias(pericias)
        }

        for (pericia in pericias) {
            val checkBox: CheckBox = findViewById(pericia.checkBoxId)
            checkBox.setOnCheckedChangeListener { _, _ ->
                atualizarPericias(pericias)
            }
        }

        btnSalvar.setOnClickListener {
            val nome = etNome.text.toString()
            val classe = spnClasse.selectedItem.toString()
            val raca = spnRaca.selectedItem.toString()
            val nivel = etNivel.text.toString().toIntOrNull() ?: 0

            val atributosMap = mapOf(
                "Força" to getValorAtributo("Força"),
                "Destreza" to getValorAtributo("Destreza"),
                "Constituição" to getValorAtributo("Constituicao"),
                "Inteligência" to getValorAtributo("Inteligência"),
                "Sabedoria" to getValorAtributo("Sabedoria"),
                "Carisma" to getValorAtributo("Carisma")
            )

            val periciasMarcadas = mutableListOf<String>()

            for (pericia in pericias) {
                val checkBox: CheckBox = findViewById(pericia.checkBoxId)
                val atributo = pericia.atributoAssociado

                if (checkBox.isChecked) {
                    val valorAtributo = getValorAtributo(atributo)
                    val modificador = calcularModificador(valorAtributo) + calcularModificadorProficiencia(nivel)
                    periciasMarcadas.add("${pericia.nome} (${modificador})")
                }
            }

            // Envia os dados para a ThirdActivity
            val intent = Intent(this, ThirdActivity::class.java)
            intent.putExtra("NOME", nome)
            intent.putExtra("CLASSE", classe)
            intent.putExtra("RACA", raca)
            intent.putExtra("NIVEL", nivel)
            intent.putExtra("ATRIBUTOS", HashMap(atributosMap))
            intent.putStringArrayListExtra("PERICIAS_MARCADAS", ArrayList(periciasMarcadas))

            startActivity(intent)
        }
    }

    private fun configurarListenerAtributo(et: EditText, tv: TextView, onUpdate: () -> Unit) {
        et.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val valor = s?.toString()?.toIntOrNull() ?: 0
                val modificador = calcularModificador(valor)
                tv.text = modificador.toString()
                onUpdate()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }


    private fun calcularModificador(valorAtributo: Int): Int {
        return kotlin.math.floor((valorAtributo - 10) / 2.0).toInt()
    }


    private fun getValorAtributo(atributo: String): Int {
        return when (atributo) {
            "Força" -> findViewById<EditText>(R.id.etForca).text.toString().toIntOrNull() ?: 0
            "Destreza" -> findViewById<EditText>(R.id.etDestreza).text.toString().toIntOrNull() ?: 0
            "Constituicao" -> findViewById<EditText>(R.id.etConstituicao).text.toString().toIntOrNull() ?: 0
            "Inteligência" -> findViewById<EditText>(R.id.etInteligencia).text.toString().toIntOrNull() ?: 0
            "Sabedoria" -> findViewById<EditText>(R.id.etSabedoria).text.toString().toIntOrNull() ?: 0
            "Carisma" -> findViewById<EditText>(R.id.etCarisma).text.toString().toIntOrNull() ?: 0
            else -> 0
        }
    }

    private fun calcularModificadorProficiencia(nivel: Int): Int {
        return kotlin.math.ceil(nivel / 4.0).toInt()
    }

    private fun configurarListenerModProficiencia(et: EditText, tv: TextView, onUpdate: (Int) -> Unit) {
        et.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val nivel = s?.toString()?.toIntOrNull() ?: 0
                val modificadorProficiencia = calcularModificadorProficiencia(nivel)
                tv.text = modificadorProficiencia.toString()
                onUpdate(modificadorProficiencia)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun atualizarPericias(pericias: List<Pericia>) {
        val nivel = findViewById<EditText>(R.id.etNivel).text.toString().toIntOrNull() ?: 0
        val modificadorProficiencia = calcularModificadorProficiencia(nivel)

        for (pericia in pericias) {
            val checkBox: CheckBox = findViewById(pericia.checkBoxId)
            val textView: TextView = findViewById(pericia.textViewId)
            val atributo = pericia.atributoAssociado

            val modificadorAtributo = calcularModificador(getValorAtributo(atributo))

            val valorPericia = if (checkBox.isChecked) {
                modificadorAtributo + modificadorProficiencia
            } else {
                modificadorAtributo
            }

            textView.text = valorPericia.toString()
        }
    }

}