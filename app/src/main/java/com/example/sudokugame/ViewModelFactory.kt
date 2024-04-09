package com.example.sudokugame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

///Clase de fábrica para crear instancias de [SudokuViewModel].
//@param parameter Un parámetro entero utilizado para personalizar la instancia del ViewModel.

class ViewModelFactory(private val parameter: Int) : ViewModelProvider.Factory {
    //Crea una nueva instancia de la clase de ViewModel solicitada.

    //modelClass es la clase del viewModel a crear
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass == SudokuViewModel::class.java) {
            //retorna una nueva instancia de la clase de ViewModel solicitada.
            return SudokuViewModel(parameter) as T
        }
        //manejo de errores por si la clase de ViewModel solicitada es desconocida
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.canonicalName}")
    }
}
