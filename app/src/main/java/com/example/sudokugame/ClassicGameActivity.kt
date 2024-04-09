package com.example.sudokugame

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider

class ClassicGameActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var viewModel: SudokuViewModel
    private var mode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classic_game)

        mediaPlayer = MediaPlayer.create(this, R.raw.ding)

        // una vez cambiamos de actividad, configuramos la vista del tablero y la mostramos en el activity-classic-game.xml
        val boardView = findViewById<SudokuBoardView>(R.id.sudokuBoardView)
        boardView.onTouchListener = object : SudokuBoardView.OnTouchListener {
            override fun onTouch(row: Int, column: Int) {
                onCellTouched(row, column)
            }
        }

        // Usando el intent, obtenemos el modo de dificultad del juego como un int
        mode = intent.getIntExtra("mode", 1)

        // ViewModel
        val viewModelFactory = ViewModelFactory(mode)
        viewModel = ViewModelProvider(this, viewModelFactory)[SudokuViewModel::class.java]
        viewModel.selectedCell.observe(this) { updateSelectedCell(it) }
        viewModel.boardNumbers.observe(this) { updateBoardNumbers(it) }
        viewModel.seconds.observe(this) { updateTime(it) }

        // Teckado de numeros del 1 al 9
        val buttonList = listOf(
            findViewById(R.id.oneButton),
            findViewById(R.id.twoButton),
            findViewById(R.id.threeButton),
            findViewById(R.id.fourButton),
            findViewById(R.id.fiveButton),
            findViewById(R.id.sixButton),
            findViewById(R.id.sevenButton),
            findViewById(R.id.eightButton),
            findViewById<Button>(R.id.nineButton)
        )
        buttonList.forEachIndexed { index, button ->
            button.setOnClickListener { viewModel.numberInput(index + 1)
                mediaPlayer.seekTo(100)
                mediaPlayer.start()} // le sumamos 1 porque los indices empiezan en 0
        }

        // aceptar button
        val acceptButton = findViewById<Button>(R.id.acceptButton)
        acceptButton.setOnClickListener { viewModel.acceptNumber() }

        // eliminar button
        val removeButton = findViewById<Button>(R.id.backButton)
        removeButton.setOnClickListener { viewModel.removeNumber() }

        // Terminar button
        val finishButton = findViewById<Button>(R.id.finishButton)
        finishButton.setOnClickListener {
            if (viewModel.finish()) end()
        }
    }

    /**
     * Actualiza la celda seleccionada en el tablero del Sudoku.
     * Cuando se toca una celda, se actualiza la celda seleccionada en el tablero
     * y se muestra en la vista usando coordenadas de fila y columna.
     */
    private fun updateSelectedCell(cell: Pair<Int, Int>?) = cell?.let { // cell para la celda seleccionada y recibe un par de enteros fila o columna
        val boardView = findViewById<SudokuBoardView>(R.id.sudokuBoardView) // obtenemos la vista del tablero
        boardView.updateSelectedCell(cell.first, cell.second) // actualizamos la celda seleccionada en la vista del tablero
    }

    /**
     * Actualiza los números en el tablero del Sudoku.
     *  dibuja los numeros en el tablero del sudoku y la actualiza segun la celda seleccionada.
     */
    private fun updateBoardNumbers(boardNumbers: List<Pair<Int, Int>?>) {
        val boardView = findViewById<SudokuBoardView>(R.id.sudokuBoardView)
        boardView.updateBoardNumbers(boardNumbers)
    }

    /**
     * Esta funcion actualiza el tiempo en la vista del tablero.
     * muestra el tiempo en el formato MM:SS
     */
    private fun updateTime(seconds: Int) {
        var text = viewModel.minutes.value.toString() + ":"
        if (viewModel.minutes.value!! < 10) { // si los minutos son menores a 10, se agrega un 0 al inicio
            text = "0$text"
        }
        if (seconds < 10) { // si los segundos son menores a 10, se agrega un 0 al inicio
            text += "0$seconds"
        } else {
            text += seconds
        }
        val timeTextView = findViewById<TextView>(R.id.time) // obtenemos el textview del tiempo
        timeTextView.text = text // actualizamos el tiempo en la vista
    }

    /**
     * Aqui se maneja el evento de tocar una celda en el tablero.
     * nuevamente cuando se toca la celda se actualiza la celda seleccionada en el tablero
     * y se muestra en la vista usando coordenadas de fila y columna.
     */
    fun onCellTouched(row: Int, column: Int) {
        viewModel.updateSelectedCell(row, column)
    }

    /**
     * Cuando el juego termina, se inicia la actividad de juego terminado.
     */
    private fun end() {
        val handler = Handler() // maneja las variables de tiempoy errores en un hilo que va a gamefinishedactivity

        val startNewActivityRunnable = Runnable {
            val intent = Intent(this, GameFinishedActivity::class.java)
            intent.putExtra("activity", "classic")
            intent.putExtra("contadorErrores", viewModel.mistakes)
            intent.putExtra("Minutos", viewModel.minutes.value)
            intent.putExtra("Segundos", viewModel.seconds.value)
            intent.putExtra("Mode", mode)
            startActivity(intent)
            this.finish()
        }
        handler.postDelayed(startNewActivityRunnable, 2000)
    }
}
