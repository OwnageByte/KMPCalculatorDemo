package com.ownagebyte.kmpcalculatordemo

import com.ownagebyte.kmpcalculatordemo.domain.CalculatorValidator
import com.ownagebyte.kmpcalculatordemo.model.MathOperation
import com.ownagebyte.kmpcalculatordemo.mvi.CalculatorIntent
import com.ownagebyte.kmpcalculatordemo.mvi.DefaultCalculatorStore
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CalculatorStoreTest {
    private lateinit var store: DefaultCalculatorStore

    @BeforeTest
    fun setUp() {
        store = DefaultCalculatorStore()
    }

    @Test
    fun testAddition() {
        store.dispatch(CalculatorIntent.FirstNumberChanged("2"))
        store.dispatch(CalculatorIntent.SecondNumberChanged("3"))
        store.dispatch(CalculatorIntent.OperationSelected(MathOperation.ADD))
        store.dispatch(CalculatorIntent.CalculateClicked)

        val state = store.state.value
        assertEquals("5", state.result)
    }

    @Test
    fun testSubtraction() {
        store.dispatch(CalculatorIntent.FirstNumberChanged("10"))
        store.dispatch(CalculatorIntent.SecondNumberChanged("4"))
        store.dispatch(CalculatorIntent.OperationSelected(MathOperation.SUBTRACT))
        store.dispatch(CalculatorIntent.CalculateClicked)

        val state = store.state.value
        assertEquals("6", state.result)
    }

    @Test
    fun testMultiplicationWithNegativeNumber() {
        store.dispatch(CalculatorIntent.FirstNumberChanged("-2"))
        store.dispatch(CalculatorIntent.SecondNumberChanged("3"))
        store.dispatch(CalculatorIntent.OperationSelected(MathOperation.MULTIPLY))
        store.dispatch(CalculatorIntent.CalculateClicked)

        val state = store.state.value
        assertEquals("-6", state.result)
    }

    @Test
    fun testDivisionProducingDecimal() {
        store.dispatch(CalculatorIntent.FirstNumberChanged("7"))
        store.dispatch(CalculatorIntent.SecondNumberChanged("2"))
        store.dispatch(CalculatorIntent.OperationSelected(MathOperation.DIVIDE))
        store.dispatch(CalculatorIntent.CalculateClicked)

        val state = store.state.value
        assertEquals("3.5", state.result)
    }

    @Test
    fun testEmptyInputsValidation() {
        store.dispatch(CalculatorIntent.CalculateClicked)

        val state = store.state.value
        assertEquals(CalculatorValidator.ERROR_FIRST_NUMBER_REQUIRED, state.firstNumberError)
        assertEquals(CalculatorValidator.ERROR_SECOND_NUMBER_REQUIRED, state.secondNumberError)
        assertEquals(CalculatorValidator.ERROR_OPERATION_REQUIRED, state.operationError)
        assertNull(state.result)
    }

    @Test
    fun testInvalidNumericInput() {
        store.dispatch(CalculatorIntent.FirstNumberChanged("abc"))
        store.dispatch(CalculatorIntent.SecondNumberChanged("5"))
        store.dispatch(CalculatorIntent.OperationSelected(MathOperation.ADD))
        store.dispatch(CalculatorIntent.CalculateClicked)

        val state = store.state.value
        assertEquals(CalculatorValidator.ERROR_INVALID_NUMBER, state.firstNumberError)
        assertNull(state.result)
    }

    @Test
    fun testMissingOperation() {
        store.dispatch(CalculatorIntent.FirstNumberChanged("10"))
        store.dispatch(CalculatorIntent.SecondNumberChanged("5"))
        store.dispatch(CalculatorIntent.CalculateClicked)

        val state = store.state.value
        assertEquals(CalculatorValidator.ERROR_OPERATION_REQUIRED, state.operationError)
        assertNull(state.result)
    }

    @Test
    fun testDivisionByZero() {
        store.dispatch(CalculatorIntent.FirstNumberChanged("10"))
        store.dispatch(CalculatorIntent.SecondNumberChanged("0"))
        store.dispatch(CalculatorIntent.OperationSelected(MathOperation.DIVIDE))
        store.dispatch(CalculatorIntent.CalculateClicked)

        val state = store.state.value
        assertEquals(CalculatorValidator.ERROR_DIVIDE_BY_ZERO, state.operationError)
        assertNull(state.result)

        // Negative zero verification
        store.dispatch(CalculatorIntent.SecondNumberChanged("-0"))
        store.dispatch(CalculatorIntent.CalculateClicked)
        assertEquals(CalculatorValidator.ERROR_DIVIDE_BY_ZERO, store.state.value.operationError)
        assertNull(state.result)
    }

    @Test
    fun testStateChangeAfterEachIntent() {
        store.dispatch(CalculatorIntent.FirstNumberChanged("12"))
        assertEquals("12", store.state.value.firstNumber)

        store.dispatch(CalculatorIntent.SecondNumberChanged("34"))
        assertEquals("34", store.state.value.secondNumber)

        store.dispatch(CalculatorIntent.OperationSelected(MathOperation.MULTIPLY))
        assertEquals(MathOperation.MULTIPLY, store.state.value.selectedOperation)
    }

    @Test
    fun testResultAndErrorsClearedWhenInputChanges() {
        // Trigger calculation first
        store.dispatch(CalculatorIntent.FirstNumberChanged("5"))
        store.dispatch(CalculatorIntent.SecondNumberChanged("5"))
        store.dispatch(CalculatorIntent.OperationSelected(MathOperation.ADD))
        store.dispatch(CalculatorIntent.CalculateClicked)

        val state = store.state
        assertEquals("10", state.value.result)

        // Changing first number must clear result
        store.dispatch(CalculatorIntent.FirstNumberChanged("6"))
        assertNull(state.value.result)

        // Induce validation error
        store.dispatch(CalculatorIntent.FirstNumberChanged(""))
        store.dispatch(CalculatorIntent.CalculateClicked)
        assertEquals(CalculatorValidator.ERROR_FIRST_NUMBER_REQUIRED, state.value.firstNumberError)
        assertNull(state.value.result)

        // Editing clears the field error immediately
        store.dispatch(CalculatorIntent.FirstNumberChanged("8"))
        assertNull(state.value.firstNumberError)
        assertNull(state.value.result)
    }
}
