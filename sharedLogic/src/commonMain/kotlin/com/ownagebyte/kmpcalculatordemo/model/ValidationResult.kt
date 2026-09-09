package com.ownagebyte.kmpcalculatordemo.model

data class ValidationResult(
    val firstNumberError: String? = null,
    val secondNumberError: String? = null,
    val operationError: String? = null,
    val parsedFirstNumber: Double? = null,
    val parsedSecondNumber: Double? = null,
) {
    val isValid: Boolean
        get() = firstNumberError == null && secondNumberError == null && operationError == null
}
