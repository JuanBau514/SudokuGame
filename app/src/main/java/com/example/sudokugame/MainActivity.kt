package com.example.sudokugame
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        configureButtons()
    }

    /**
     * Aqui estamos configurando los botones de la actividad principal.
     */
    private fun configureButtons() {
        findViewById<Button>(R.id.easyButton).setOnClickListener {
            startClassicGameActivity(1)
        }
        findViewById<Button>(R.id.mediumButton).setOnClickListener {
            startClassicGameActivity(2)
        }
        findViewById<Button>(R.id.hardButton).setOnClickListener {
            startClassicGameActivity(3)
        }

    }

    /**
     * Comienza la actividad del juego clasico. Dependiendo del modo, se inicia el juego con un nivel de dificultad diferente.
     *
     * @param mode El juego tiene 3 niveles o dificultades: 1 facil, 2 medio, 3 dificil .
     */
    private fun startClassicGameActivity(mode: Int) {
        val intent = Intent(this, ClassicGameActivity::class.java).apply {
            putExtra("mode", mode)
        }
        startActivity(intent)
    }
}
