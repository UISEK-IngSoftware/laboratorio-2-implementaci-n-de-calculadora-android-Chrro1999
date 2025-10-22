package ec.edu.uisek.calculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class CalculatorState(
    val display: String = "0"
)

sealed class CalculatorEvent {
    data class Number(val number: String) : CalculatorEvent()
    data class Operator(val operator: String) : CalculatorEvent()
    object Clear : CalculatorEvent()
    object AllClear : CalculatorEvent()
    object Calculate : CalculatorEvent()
    object Decimal : CalculatorEvent()
}

class CalculatorViewModel : ViewModel() {
    private var number1: String = ""
    private var number2: String = ""
    private var operator: String? = null
    private var shouldResetNumber: Boolean = false

    var state by mutableStateOf(CalculatorState())
        private set

    fun onEvent(event: CalculatorEvent) {
        when (event) {
            is CalculatorEvent.Number -> enterNumber(event.number)
            is CalculatorEvent.Operator -> enterOperator(event.operator)
            is CalculatorEvent.Decimal -> enterDecimal()
            is CalculatorEvent.AllClear -> clearAll()
            is CalculatorEvent.Clear -> clearLast()
            is CalculatorEvent.Calculate -> performCalculation()
        }
    }

    private fun enterNumber(number: String) {
        if (shouldResetNumber) {
            // Si acabamos de calcular, empezamos nuevo número
            number1 = state.display
            number2 = ""
            operator = null
            shouldResetNumber = false
        }

        if (operator == null) {
            // Primer número
            if (number1 == "0" || number1 == "Error") {
                number1 = number
            } else {
                number1 += number
            }
            state = state.copy(display = number1)
        } else {
            // Segundo número
            if (number2 == "0" || number2.isEmpty()) {
                number2 = number
            } else {
                number2 += number
            }
            state = state.copy(display = number2)
        }
    }

    private fun enterOperator(op: String) {
        if (number1.isNotBlank() && number2.isNotBlank() && operator != null) {
            // Si ya tenemos una operación pendiente, la calculamos primero
            performCalculation()
            number1 = state.display
            number2 = ""
        }

        operator = op
        shouldResetNumber = false
    }

    private fun enterDecimal() {
        if (shouldResetNumber) {
            number1 = state.display
            number2 = ""
            operator = null
            shouldResetNumber = false
        }

        val currentNumber = if (operator == null) number1 else number2

        if (!currentNumber.contains(".")) {
            if (operator == null) {
                number1 = if (number1.isEmpty()) "0." else number1 + "."
                state = state.copy(display = number1)
            } else {
                number2 = if (number2.isEmpty()) "0." else number2 + "."
                state = state.copy(display = number2)
            }
        }
    }

    private fun performCalculation() {
        if (number1.isBlank() || operator == null) return

        val num1 = number1.toDoubleOrNull() ?: 0.0
        val num2 = if (number2.isBlank()) num1 else number2.toDoubleOrNull() ?: 0.0

        val result = when (operator) {
            "+" -> num1 + num2
            "−" -> num1 - num2
            "×" -> num1 * num2
            "÷" -> if (num2 != 0.0) num1 / num2 else Double.NaN
            else -> num1
        }

        val resultString = if (result.isNaN()) "Error"
        else result.toString().removeSuffix(".0")

        // Preparamos para la siguiente operación
        number1 = if (result.isNaN()) "" else resultString
        number2 = ""
        operator = null
        shouldResetNumber = true

        state = state.copy(display = resultString)
    }

    private fun clearLast() {
        if (operator == null) {
            if (number1.isNotBlank()) {
                number1 = number1.dropLast(1)
                state = state.copy(display = if (number1.isBlank()) "0" else number1)
            }
        } else {
            if (number2.isNotBlank()) {
                number2 = number2.dropLast(1)
                state = state.copy(display = if (number2.isBlank()) "0" else number2)
            } else {
                operator = null
                state = state.copy(display = number1)
            }
        }
        shouldResetNumber = false
    }

    private fun clearAll() {
        number1 = ""
        number2 = ""
        operator = null
        shouldResetNumber = false
        state = state.copy(display = "0")
    }
}