package com.ownagebyte.kmpcalculatordemo.domain

import com.ownagebyte.kmpcalculatordemo.model.MathOperation

object CalculatorEngine {
    sealed interface CalculationResult {
        data class Success(val result: String) : CalculationResult
        data class Failure(val errorMessage: String) : CalculationResult
    }

    /**
     * Calculates the result of a mathematical operation.
     * @param firstNumber The first number in the operation.
     * @param secondNumber The second number in the operation.
     * @param operation The type of operation to perform.
     * @return A [CalculationResult] object containing the result or error message.
     */
    fun calculate(
        firstNumber: Double,
        secondNumber: Double,
        operation: MathOperation
    ): CalculationResult {
        val result = when (operation) {
            MathOperation.ADD -> firstNumber + secondNumber
            MathOperation.SUBTRACT -> firstNumber - secondNumber
            MathOperation.MULTIPLY -> firstNumber * secondNumber
            MathOperation.DIVIDE -> {
                if (secondNumber != 0.0) {
                    firstNumber / secondNumber
                } else {
                    return CalculationResult.Failure(CalculatorValidator.ERROR_DIVIDE_BY_ZERO)
                }
            }
        }

        if (result.isInfinite() || result.isNaN()) {
            return CalculationResult.Failure(CalculatorValidator.ERROR_RESULT_TOO_LARGE)
        }

        return CalculationResult.Success(formatResult(result))
    }

    /**
     * Formats the result of a calculation.
     * @param result The result of the calculation.
     * @return A string representation of the result.
     */
    fun formatResult(result: Double): String {
        return if (result % 1.0 == 0.0 && result <= Long.MAX_VALUE.toDouble() && result >= Long.MIN_VALUE.toDouble()) {
            result.toLong().toString()
        } else {
            result.toString()
        }
    }
}
