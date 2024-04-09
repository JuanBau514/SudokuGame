package com.example.sudokugame
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView

/**
 * Activity cuando el juego ha terminado.
 */
class GameFinishedActivity : AppCompatActivity() {

    private var mode = 1
    private lateinit var activity : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_finished)

        // Optiene el tipo de actividad
        activity = intent.getStringExtra("activity")!!

        // Configura variables de la actividad anterior en base al tiempo que duro el juego y los errores cometidos
        val textView = findViewById<TextView>(R.id.time)
        var text = ""
        when (activity) {
            "classic" -> {
                val mistakes = intent.getIntExtra("contadorErrores", 0)
                val minutes = intent.getIntExtra("Minutos", 0)
                val seconds = intent.getIntExtra("Segundos", 0)
                mode = intent.getIntExtra("mode", 1)
                text = "has terminado el juego en  \n$minutes "
                text += if (minutes == 1) "Minutos"
                else "minutos"
                text += " y $seconds "
                text += if (seconds == 1) "Segundos"
                else "segundos"
                text += ".\nHaz tenido  $mistakes "
                text += if (mistakes == 1) "Errores."
                else "Errores."
            }
            "twoPlayers" -> {
                text = intent.getStringExtra("type")!!
            }
        }
        textView.text = text
    }

    /**
     * llama de nuevo a la actividad principal. cuando el boton de menu es presionado.
     */
    fun menu(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        this.startActivity(intent)
        this.finish()
    }

    /**
     * Llama de nuevo a la actividad del juego. cuando el boton de jugar de nuevo es presionado.
     */
    fun again(view: View) {
        when(activity) {
            "classic" -> {
                val intent = Intent(this, ClassicGameActivity::class.java)
                intent.putExtra("mode", mode)
                this.startActivity(intent) // Inicia la actividad del juego clasico de la misma dificultad
                this.finish()
            }
        }
    }
}
