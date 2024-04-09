package com.example.sudokugame
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

/**
 * Main activity of the Sudoku game.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        configureButtons()
    }

    /**
     * Configures the click listeners for the buttons in the activity.
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
     * Starts the TwoPlayersActivity.
     */

    /**
     * Starts the ClassicGameActivity with the specified game mode.
     *
     * @param mode The game mode: 1 for easy, 2 for medium, 3 for hard.
     */
    private fun startClassicGameActivity(mode: Int) {
        val intent = Intent(this, ClassicGameActivity::class.java).apply {
            putExtra("mode", mode)
        }
        startActivity(intent)
    }
}
