package com.ownagebyte.kmpcalculatordemo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ownagebyte.kmpcalculatordemo.model.CalculatorUiState
import com.ownagebyte.kmpcalculatordemo.model.MathOperation
import com.ownagebyte.kmpcalculatordemo.mvi.CalculatorIntent

@Composable
fun CalculatorScreen(
    modifier: Modifier = Modifier,
    viewModel: CalculatorViewModel = viewModel()
) {
    // Lifecycle-aware collection stops flow collection when the app is in the background
    val state by viewModel.state.collectAsStateWithLifecycle()

    CalculatorScreenContent(
        modifier = modifier,
        state = state,
        onIntent = viewModel::dispatch
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalculatorScreenContent(
    modifier: Modifier = Modifier,
    state: CalculatorUiState,
    onIntent: (CalculatorIntent) -> Unit
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "KMP Calculator Demo",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        // First number input
        NumberInputTextField(
            modifier = Modifier.fillMaxWidth(),
            label = "First Number",
            placeholder = "Enter number (e.g. 10 or -2.5)",
            value = state.firstNumber,
            isError = state.firstNumberError != null,
            errorText = state.firstNumberError,
            onValueChange = { onIntent(CalculatorIntent.FirstNumberChanged(it)) }
        )

        // Second number input
        NumberInputTextField(
            modifier = Modifier.fillMaxWidth(),
            label = "Second Number",
            placeholder = "Enter number (e.g. 5)",
            value = state.secondNumber,
            isError = state.secondNumberError != null,
            errorText = state.secondNumberError,
            onValueChange = { onIntent(CalculatorIntent.SecondNumberChanged(it)) }
        )

        ExposedDropdownMenuBox(
            modifier = Modifier.fillMaxWidth(),
            expanded = isDropdownExpanded,
            onExpandedChange = { isDropdownExpanded = it }
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                value = state.selectedOperation?.display.orEmpty(),
                onValueChange = {},
                readOnly = true,
                label = { Text("Operation") },
                placeholder = { Text("Select Operation") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                isError = state.operationError != null,
                supportingText = {
                    state.operationError?.let { errorText ->
                        Text(
                            modifier = Modifier.semantics { contentDescription = "Operation error: $errorText" },
                            text = errorText,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            ExposedDropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false }
            ) {
                MathOperation.entries.forEach { operation ->
                    DropdownMenuItem(
                        text = { Text(operation.display) },
                        onClick = {
                            onIntent(CalculatorIntent.OperationSelected(operation))
                            isDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            onClick = { onIntent(CalculatorIntent.CalculateClicked) }
        ) {
            Text(
                text = "Calculate",
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Calculation result: ${state.result ?: "Empty"}" },
            value = state.result.orEmpty(),
            onValueChange = {}, // Read-only: User cannot type here
            readOnly = true,
            label = { Text("Result") },
            singleLine = true
        )
    }
}

@Composable
private fun NumberInputTextField(
    modifier: Modifier = Modifier,
    label: String,
    placeholder: String,
    value: String,
    isError: Boolean,
    errorText: String?,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        isError = isError,
        supportingText = {
            errorText?.let { errorText ->
                Text(
                    modifier = Modifier.semantics { contentDescription = "$label error: $errorText" },
                    text = errorText,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true
    )
}

@Preview(showBackground = true)
@Composable
private fun CalculatorScreenPreview() {
    CalculatorScreenContent(
        state = CalculatorUiState(),
        onIntent = {}
    )
}
