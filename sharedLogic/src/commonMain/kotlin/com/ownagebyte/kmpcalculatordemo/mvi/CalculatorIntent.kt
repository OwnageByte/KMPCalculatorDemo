package com.ownagebyte.kmpcalculatordemo.mvi

import com.ownagebyte.kmpcalculatordemo.model.MathOperation

sealed interface CalculatorIntent {
    data class FirstNumberChanged(val value: String) : CalculatorIntent
    data class SecondNumberChanged(val value: String) : CalculatorIntent
    data class OperationSelected(val operation: MathOperation) : CalculatorIntent
    data object CalculateClicked : CalculatorIntent
}
