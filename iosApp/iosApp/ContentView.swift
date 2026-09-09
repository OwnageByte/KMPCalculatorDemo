import SwiftUI
import sharedLogic

struct ContentView: View {

    @StateObject private var viewModel = ObservableCalculatorStore()

    var body: some View {
        NavigationView {
            Form {
                // 1. First Number Input Section
                Section(header: Text("First Number")) {
                    TextField(
                        "Enter number (e.g. 10 or -2.5)",
                        text: Binding(
                            get: { viewModel.state.firstNumber },
                            set: { viewModel.dispatch(intent: CalculatorIntent.FirstNumberChanged(value: $0)) }
                        )
                    )
                    .keyboardType(.decimalPad)

                    if let error = viewModel.state.firstNumberError {
                        Text(error)
                            .font(.caption)
                            .foregroundColor(.red)
                    }
                }

                // 2. Second Number Input Section
                Section(header: Text("Second Number")) {
                    TextField(
                        "Enter number (e.g. 5)",
                        text: Binding(
                            get: { viewModel.state.secondNumber },
                            set: { viewModel.dispatch(intent: CalculatorIntent.SecondNumberChanged(value: $0)) }
                        )
                    )
                    .keyboardType(.decimalPad)

                    if let error = viewModel.state.secondNumberError {
                        Text(error)
                            .font(.caption)
                            .foregroundColor(.red)
                    }
                }

                // 3. Operation Selector Section
                Section(header: Text("Operation")) {
                    Picker(
                        "Select operation",
                        selection: Binding(
                            get: { viewModel.state.selectedOperation },
                            set: { newOp in
                                if let selected = newOp {
                                    viewModel.dispatch(intent: CalculatorIntent.OperationSelected(operation: selected))
                                }
                            }
                        )
                    ) {
                        Text("Select operation").tag(nil as MathOperation?)
                        Text("Add (+)").tag(MathOperation.add as MathOperation?)
                        Text("Subtract (-)").tag(MathOperation.subtract as MathOperation?)
                        Text("Multiply (x)").tag(MathOperation.multiply as MathOperation?)
                        Text("Divide (÷)").tag(MathOperation.divide as MathOperation?)
                    }
                    .pickerStyle(.menu)

                    if let error = viewModel.state.operationError {
                        Text(error)
                            .font(.caption)
                            .foregroundColor(.red)
                    }
                }

                // 4. Calculate Action Section
                Section {
                    Button(action: {
                        viewModel.dispatch(intent: CalculatorIntent.CalculateClicked())
                    }) {
                        HStack {
                            Spacer()
                            Text("Calculate")
                                .fontWeight(.semibold)
                            Spacer()
                        }
                    }
                }

                // 5. Result Section (Read-Only)
                Section(header: Text("Result")) {
                    Text(viewModel.state.result ?? "Calculated value appears here")
                        .foregroundColor(viewModel.state.result != nil ? .primary : .secondary)
                        .accessibilityLabel("Calculation result: \(viewModel.state.result ?? "Empty")")
                }
            }
            .navigationTitle("KMP Calculator Demo")
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}