package sk.uxtweak.uxmobile.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import sk.uxtweak.uxmobile.util.LogUtils

class DebugLogViewModel : ViewModel() {
    val logChanged = MutableLiveData<String>()

    init {
        LogUtils.addLogListener(::onLogChanged)
        logChanged.value = LogUtils.logs.toString()
    }

    override fun onCleared() {
        super.onCleared()
        LogUtils.removeLogListener(::onLogChanged)
    }

    private fun onLogChanged(logs: String) {
        logChanged.postValue(logs)
    }
}
