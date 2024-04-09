package com.example.sudokugame
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * Aqui usuamos el View para crear un tablero de sudoku, con las lineas y celdas correspondientes.
 * Ademas, se definen los colores y estilos de las celdas, lineas y numeros.
 * Se implementa la interfaz OnTouchListener para manejar los eventos de toque en el tablero.
 */
class SudokuBoardView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private var boardSize = 9 // Tamaño del tablero
    private var sqrtSize = 3 // Tamaño de la raiz cuadrada del tablero
    private var cellSizePixels = 0 // Tamaño de las celdas del tablero
    private var selectedRow = -1 // Fila seleccionada
    private var selectedColumn = -1 // Columna seleccionada
    private var boardNumbers = ArrayList<Pair<Int, Int>?>() // Lista de numeros en el tablero
    internal var onTouchListener: OnTouchListener? = null // Listener para los eventos de toque en el tablero

    private val thickLinePaint = Paint().apply { // Paint para las lineas gruesas del tablero
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 4F
    }

    private val thinLinePaint = Paint().apply { // Paint para las lineas delgadas del tablero
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 2F
    }

    private val selectedCellPaint = Paint().apply { // Paint para la celda seleccionada en el tablero
        style = Paint.Style.FILL_AND_STROKE
        color = Color.rgb(173, 216, 230)
    }

    private val conflictingCellPaint = Paint().apply { // Paint para las celdas conflictivas en el tablero
        // conflictivas son las que tienen el mismo numero en la misma fila, columna o cuadrante, ademas de la seleccionada
        style = Paint.Style.FILL_AND_STROKE
        color = Color.LTGRAY
    }

    private val textPaint = Paint().apply {// Paint para los numeros en las celdas del tablero
        color = Color.BLACK
        textSize = 35f
    }

    private val preDefinedCellPaint = Paint().apply { // Paint para las celdas predefinidas en el tablero
        style = Paint.Style.FILL_AND_STROKE
        color = Color.rgb(235, 235, 235)
    }

    private val checkedCellPaint = Paint().apply { // Paint para las celdas verificadas en el tablero
        style = Paint.Style.FILL_AND_STROKE
        color = Color.rgb(255, 255, 204)
    }

    private val correctCellPaint = Paint().apply { // Paint para las celdas correctas en el tablero
        style = Paint.Style.FILL_AND_STROKE
        color = Color.GREEN
    }

    private val wrongCellPaint = Paint().apply {// verifica si la celda es incorrecta
        style = Paint.Style.FILL_AND_STROKE
        color = Color.RED
    }

    /**
     * mide el tamaño de la celda y lo ajusta al tamaño del tablero
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val sizePixels = widthMeasureSpec.coerceAtMost(heightMeasureSpec)
        setMeasuredDimension(sizePixels, sizePixels)
    }

    /**
     * Dibuja el tablero en todo el canvas o el lienzo.
     */
    override fun onDraw(canvas: Canvas) {
        cellSizePixels = (width / boardSize)
        fillCells(canvas)
        drawLines(canvas)
    }

    private fun fillCells(canvas: Canvas) {
        if (selectedRow == -1 || selectedColumn == -1) return // valida si la celda seleccionada es valida
    // si es valida recorre las celdas del tablero y las pinta segun su estado
        for (row in 0 until boardSize) {
            for (column in 0 until boardSize) { // recorre las columnas y filas del tablero y verifica si hay conflictos
                var conflict = false
                if (row == selectedRow && column == selectedColumn) {
                    fillCell(canvas, row, column, selectedCellPaint)
                    conflict = true
                } else if (row == selectedRow || column == selectedColumn || (row / sqrtSize == selectedRow / sqrtSize && column / sqrtSize == selectedColumn / sqrtSize)) {
                    fillCell(canvas, row, column, conflictingCellPaint)
                    conflict = true
                }

                if (boardNumbers[row * boardSize + column] != null) { // si no hay conflictos pinta la celda segun su estado
                    val text = boardNumbers[row * boardSize + column]!!.first.toString()
                    val textX = column * cellSizePixels + (cellSizePixels - textPaint.measureText(text)) / 2
                    val textY = row * cellSizePixels + (cellSizePixels + textPaint.textSize) / 2
                    if (!conflict) {
                        when (boardNumbers[row * boardSize + column]!!.second) {
                            0 -> fillCell(canvas, row, column, preDefinedCellPaint)
                            2 -> fillCell(canvas, row, column, checkedCellPaint)
                            3 -> fillCell(canvas, row, column, correctCellPaint)
                            4 -> fillCell(canvas, row, column, wrongCellPaint)
                        }
                    }

                    canvas.drawText(text, textX, textY, textPaint) // pinta el numero en la celda
                }
            }
        }
    }

    // determina el color de la celda segun su estado
    private fun fillCell(canvas: Canvas, row: Int, column: Int, paint: Paint) {
        val rect = Rect(column * cellSizePixels, row * cellSizePixels, (column + 1) * cellSizePixels, (row + 1) * cellSizePixels)
        canvas.drawRect(rect, paint)
    }

    private fun drawLines(canvas: Canvas) { // dibuja las lineas del tablero
        canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), thickLinePaint)

        for (i in 1 until boardSize) {
            val paint = when (i % sqrtSize) {
                0 -> thickLinePaint
                else -> thinLinePaint
            }

            canvas.drawLine(
                i * cellSizePixels.toFloat(),
                0F,
                i * cellSizePixels.toFloat(),
                height.toFloat(),
                paint
            )

            canvas.drawLine(
                0F,
                i * cellSizePixels.toFloat(),
                width.toFloat(),
                i * cellSizePixels.toFloat(),
                paint
            )
        }
    }

    /**
     * Manejador de eventos de toque en el tablero.
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                handleTouchEvent(event.x, event.y)
                true
            }
            else -> false
        }
    }

    private fun handleTouchEvent(x: Float, y: Float) {
        val selectedRow = (y / cellSizePixels).toInt()
        val selectedColumn = (x / cellSizePixels).toInt()
        onTouchListener?.onTouch(selectedRow, selectedColumn)
    }

    /**
     * Actualiza la celda seleccionada en el tablero.
     */
    fun updateSelectedCell(row: Int, column: Int) {
        selectedRow = row
        selectedColumn = column
        invalidate()
    }

    /**
     * Actualiza los numeros en el tablero segun lo seleccionado en el teclado.
     */
    fun updateBoardNumbers(boardNumbers: List<Pair<Int, Int>?>) {
        this.boardNumbers = boardNumbers as ArrayList<Pair<Int, Int>?>
        selectedRow = 10
        selectedColumn = 10
        invalidate()
    }

    /**
     * Para manjejar los eventos de toque en el tablero.
     * se tuvo que usar una interfaz para poder implementar el metodo onTouch en el tablero.
     * se implementa el metodo onTouch de la interfaz OnTouchListener.
     *
     */
    interface OnTouchListener {
        /**
         * Cada vez que se toca una celda en el tablero se llama a este metodo.
         *  por medio de la interfaz se implementa el metodo onTouch.
         */
        fun onTouch(row: Int, column: Int)
    }
}
