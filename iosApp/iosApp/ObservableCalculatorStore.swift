import Foundation
import sharedLogic // Framework exported by Kotlin Multiplatform

@MainActor
final class ObservableCalculatorStore: ObservableObject {

    // Published state consumed reactively by SwiftUI
    @Published private(set) var state: CalculatorUiState

    private let store: CalculatorStore
    private var job: (any Kotlinx_coroutines_coreJob)?

    init(store: CalculatorStore = DefaultCalculatorStore(initialState: CalculatorUiState())) {
        self.store = store
        self.state = store.state.value
        startObservingState()
    }

    private func startObservingState() {
            // Collect state using the raw Kotlin/Native FlowCollector protocol
            job = self.store.state.collect(
                collector: FlowCollectorWrapper { [weak self] (newState: CalculatorUiState) in
                    Task { @MainActor in
                        self?.state = newState
                    }
                },
                completionHandler: { _, _ in }
            )
        }

    // Forward user actions directly to the shared KMP store
    func dispatch(intent: CalculatorIntent) {
        store.dispatch(intent: intent)
    }

    deinit {
        // Cancel the coroutine collector when this store is deallocated
        job?.cancel(cause: nil)
    }
}

// Minimal generic FlowCollector wrapper for Kotlin/Native to Swift interop
private class FlowCollectorWrapper<T>: Kotlinx_coroutines_coreFlowCollector {
    private let callback: (T) -> Void

    init(callback: @escaping (T) -> Void) {
        self.callback = callback
    }

    func emit(value: Any?, completionHandler: @escaping (Error?) -> Void) {
        if let stronglyTypedValue = value as? T {
            callback(stronglyTypedValue)
        }
        completionHandler(nil)
    }
}
