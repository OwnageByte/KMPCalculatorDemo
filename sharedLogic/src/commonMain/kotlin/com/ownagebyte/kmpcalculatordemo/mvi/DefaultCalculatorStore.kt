package com.ownagebyte.kmpcalculatordemo.mvi

import com.ownagebyte.kmpcalculatordemo.domain.CalculatorEngine
import com.ownagebyte.kmpcalculatordemo.domain.CalculatorValidator
import com.ownagebyte.kmpcalculatordemo.model.CalculatorUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DefaultCalculatorStore(
    initialState: CalculatorUiState = CalculatorUiState()
) : CalculatorStore {
    private val _state = MutableStateFlow(initialState)
    override val state: StateFlow<CalculatorUiState> = _state.asStateFlow()

    override fun dispatch(intent: CalculatorIntent) {
        _state.update { currentState ->
            reduce(currentState, intent)
        }
    }

    /**
     * Reduces the current state based on the given intent.
     * @param state The current state.
     * @param intent The intent to reduce the state with.
     * @return A [CalculatorUiState] object after applying the intent.
     */
    fun reduce(
        state: CalculatorUiState,
        intent: CalculatorIntent
    ): CalculatorUiState = when (intent) {
        is CalculatorIntent.FirstNumberChanged -> {
            state.copy(
                firstNumber = intent.value,
                firstNumberError = null,
                result = null
            )
        }

        is CalculatorIntent.SecondNumberChanged -> {
            state.copy(
                secondNumber = intent.value,
                secondNumberError = null,
                result = null
            )
        }

        is CalculatorIntent.OperationSelected -> {
            state.copy(
                selectedOperation = intent.operation,
                operationError = null,
                result = null
            )
        }

        is CalculatorIntent.CalculateClicked -> {
            val validation = CalculatorValidator.validate(
                firstNumber = state.firstNumber,
                secondNumber = state.secondNumber,
                operation = state.selectedOperation
            )

            if (!validation.isValid) {
                state.copy(
                    firstNumberError = validation.firstNumberError,
                    secondNumberError = validation.secondNumberError,
                    operationError = validation.operationError,
                    result = null
                )
            } else {
                val result = CalculatorEngine.calculate(
                    firstNumber = validation.parsedFirstNumber!!,
                    secondNumber = validation.parsedSecondNumber!!,
                    operation = state.selectedOperation!!
                )

                when (result) {
                    is CalculatorEngine.CalculationResult.Success -> {
                        state.copy(
                            firstNumberError = null,
                            secondNumberError = null,
                            operationError = null,
                            result = result.result
                        )
                    }

                    is CalculatorEngine.CalculationResult.Failure -> {
                        state.copy(
                            firstNumberError = null,
                            secondNumberError = null,
                            operationError = result.errorMessage,
                            result = null
                        )
                    }
                }
            }
        }
    }
}
