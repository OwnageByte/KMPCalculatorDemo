package com.ownagebyte.kmpcalculatordemo.mvi

import com.ownagebyte.kmpcalculatordemo.model.CalculatorUiState
import kotlinx.coroutines.flow.StateFlow

interface CalculatorStore {
    val state: StateFlow<CalculatorUiState>
    fun dispatch(intent: CalculatorIntent)
}
