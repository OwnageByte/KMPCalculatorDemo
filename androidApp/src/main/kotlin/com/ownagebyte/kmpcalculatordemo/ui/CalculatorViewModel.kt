package com.ownagebyte.kmpcalculatordemo.ui

import androidx.lifecycle.ViewModel
import com.ownagebyte.kmpcalculatordemo.model.CalculatorUiState
import com.ownagebyte.kmpcalculatordemo.mvi.CalculatorIntent
import com.ownagebyte.kmpcalculatordemo.mvi.CalculatorStore
import com.ownagebyte.kmpcalculatordemo.mvi.DefaultCalculatorStore
import kotlinx.coroutines.flow.StateFlow

class CalculatorViewModel(
    private val store: CalculatorStore = DefaultCalculatorStore()
) : ViewModel() {

    // Read-only state flow from the shared store
    val state: StateFlow<CalculatorUiState> = store.state

    // Delegate the user events directly to the shared store
    fun dispatch(intent: CalculatorIntent) {
        store.dispatch(intent)
    }
}
