package com.ownagebyte.kmpcalculatordemo.model

data class CalculatorUiState(
    val firstNumber: String = "",
    val secondNumber: String = "",
    val selectedOperation: MathOperation? = null,
    val firstNumberError: String? = null,
    val secondNumberError: String? = null,
    val operationError: String? = null,
    val result: String? = null
)
