package com.example.arquivorpg

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnCadastrar = findViewById<Button>(R.id.btnCadastrar)
        val btnInfo = findViewById<Button>(R.id.btnInfo)

        btnCadastrar.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }

        btnInfo.setOnClickListener {
            val siteIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.dndbeyond.com"))
            startActivity(siteIntent)
        }
    }
}