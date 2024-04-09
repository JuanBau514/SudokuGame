package com.example.sudokugame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider

/**
 * Activity for playing the classic Sudoku game.
 */
class ClassicGameActivity : AppCompatActivity() {

    private lateinit var viewModel: SudokuViewModel
    private var mode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classic_game)

        // Set up Sudoku board view
        val boardView = findViewById<SudokuBoardView>(R.id.sudokuBoardView)
        boardView.onTouchListener = object : SudokuBoardView.OnTouchListener {
            override fun onTouch(row: Int, column: Int) {
                onCellTouched(row, column)
            }
        }

        // Get game mode from intent extras
        mode = intent.getIntExtra("mode", 1)

        // Set up ViewModel
        val viewModelFactory = ViewModelFactory(mode)
        viewModel = ViewModelProvider(this, viewModelFactory)[SudokuViewModel::class.java]
        viewModel.selectedCell.observe(this) { updateSelectedCell(it) }
        viewModel.boardNumbers.observe(this) { updateBoardNumbers(it) }
        viewModel.seconds.observe(this) { updateTime(it) }

        // Set up number buttons
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
            button.setOnClickListener { viewModel.numberInput(index + 1) }
        }

        // Set up accept button
        val acceptButton = findViewById<Button>(R.id.acceptButton)
        acceptButton.setOnClickListener { viewModel.acceptNumber() }

        // Set up remove button
        val removeButton = findViewById<Button>(R.id.backButton)
        removeButton.setOnClickListener { viewModel.removeNumber() }

        // Set up finish button
        val finishButton = findViewById<Button>(R.id.finishButton)
        finishButton.setOnClickListener {
            if (viewModel.finish()) end()
        }
    }

    /**
     * Updates the selected cell on the Sudoku board view.
     *
     * @param cell The coordinates of the selected cell as a pair of (row, column).
     */
    private fun updateSelectedCell(cell: Pair<Int, Int>?) = cell?.let {
        val boardView = findViewById<SudokuBoardView>(R.id.sudokuBoardView)
        boardView.updateSelectedCell(cell.first, cell.second)
    }

    /**
     * Updates the numbers displayed on the Sudoku board view.
     *
     * @param boardNumbers The list of numbers to be displayed on the board as pairs of (row, column) coordinates.
     */
    private fun updateBoardNumbers(boardNumbers: List<Pair<Int, Int>?>) {
        val boardView = findViewById<SudokuBoardView>(R.id.sudokuBoardView)
        boardView.updateBoardNumbers(boardNumbers)
    }

    /**
     * Updates the time display on the activity.
     *
     * @param seconds The number of seconds elapsed in the game.
     */
    private fun updateTime(seconds: Int) {
        var text = viewModel.minutes.value.toString() + ":"
        if (viewModel.minutes.value!! < 10) {
            text = "0$text"
        }
        if (seconds < 10) {
            text += "0$seconds"
        } else {
            text += seconds
        }
        val timeTextView = findViewById<TextView>(R.id.time)
        timeTextView.text = text
    }

    /**
     * Callback when a cell on the Sudoku board is touched.
     *
     * @param row The row index of the touched cell.
     * @param column The column index of the touched cell.
     */
    fun onCellTouched(row: Int, column: Int) {
        viewModel.updateSelectedCell(row, column)
    }

    /**
     * Ends the game and starts a new activity to display the game results.
     */
    private fun end() {
        val handler = Handler()

        val startNewActivityRunnable = Runnable {
            val intent = Intent(this, GameFinishedActivity::class.java)
            intent.putExtra("activity", "classic")
            intent.putExtra("mistakesCount", viewModel.mistakes)
            intent.putExtra("minutes", viewModel.minutes.value)
            intent.putExtra("seconds", viewModel.seconds.value)
            intent.putExtra("mode", mode)
            startActivity(intent)
            this.finish()
        }
        handler.postDelayed(startNewActivityRunnable, 2000)
    }
}



