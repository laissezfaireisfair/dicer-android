package laiss.dicer.android.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SelectDicesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SelectDicesUiState())
    val uiState: StateFlow<SelectDicesUiState> = _uiState.asStateFlow()


}