package com.ownagebyte.kmpcalculatordemo.domain

import com.ownagebyte.kmpcalculatordemo.model.MathOperation
import com.ownagebyte.kmpcalculatordemo.model.ValidationResult

object CalculatorValidator {
    const val ERROR_FIRST_NUMBER_REQUIRED = "First number is required"
    const val ERROR_SECOND_NUMBER_REQUIRED = "Second number is required"
    const val ERROR_INVALID_NUMBER = "Enter a valid number"
    const val ERROR_OPERATION_REQUIRED = "Select an operation"
    const val ERROR_DIVIDE_BY_ZERO = "Cannot divide by zero"
    const val ERROR_RESULT_TOO_LARGE = "Result is too large"

    /**
     * Validates the input for the calculator.
     * @param firstNumber The first number entered by the user.
     * @param secondNumber The second number entered by the user.
     * @param operation The selected operation by the user.
     * @return A [ValidationResult] object containing the validation results.
     */
    fun validate(
        firstNumber: String,
        secondNumber: String,
        operation: MathOperation?
    ): ValidationResult {
        val trimmedFirstNumber = firstNumber.trim()
        val trimmedSecondNumber = secondNumber.trim()

        val firstNumberError: String? = when {
            trimmedFirstNumber.isEmpty() -> ERROR_FIRST_NUMBER_REQUIRED
            trimmedFirstNumber.toDoubleOrNull() == null -> ERROR_INVALID_NUMBER
            else -> null
        }

        val secondNumberError: String? = when {
            trimmedSecondNumber.isEmpty() -> ERROR_SECOND_NUMBER_REQUIRED
            trimmedSecondNumber.toDoubleOrNull() == null -> ERROR_INVALID_NUMBER
            else -> null
        }

        var operationError: String? = if (operation == null) ERROR_OPERATION_REQUIRED else null

        val parsedFirstNumber = if (firstNumberError == null) trimmedFirstNumber.toDouble() else null
        val parsedSecondNumber = if (secondNumberError == null) trimmedSecondNumber.toDouble() else null

        if (operation == MathOperation.DIVIDE && parsedSecondNumber == 0.0) {
            operationError = ERROR_DIVIDE_BY_ZERO
        }

        return ValidationResult(
            firstNumberError = firstNumberError,
            secondNumberError = secondNumberError,
            operationError = operationError,
            parsedFirstNumber = parsedFirstNumber,
            parsedSecondNumber = parsedSecondNumber
        )
    }
}
