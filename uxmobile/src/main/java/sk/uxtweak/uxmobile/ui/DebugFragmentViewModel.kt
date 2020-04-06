package sk.uxtweak.uxmobile.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sk.uxtweak.uxmobile.UxMobile
import sk.uxtweak.uxmobile.core.Stats
import sk.uxtweak.uxmobile.core.withFixedDelay
import sk.uxtweak.uxmobile.util.TAG
import sk.uxtweak.uxmobile.util.logd

class DebugFragmentViewModel : ViewModel() {
    val eventRecorderEnabled = MutableLiveData<Boolean>()
    val screenRecorderEnabled = MutableLiveData<Boolean>()
    val connectionManagerEnabled = MutableLiveData<Boolean>()
    val persisterEnabled = MutableLiveData<Boolean>()
    val senderEnabled = MutableLiveData<Boolean>()
    val logArea = MutableLiveData<String>()

    init {
        updateStates()
        viewModelScope.withFixedDelay(timeMillis = 1000L) {
            logArea.value = withContext(Dispatchers.Default) { Stats.log() }
            updateStates()
        }
    }

    fun eventRecorderCheckChanged(isChecked: Boolean) {
        if (isChecked) {
            UxMobile.sessionManager.eventRecorder.start()
        } else {
            UxMobile.sessionManager.eventRecorder.stop()
        }
        updateStates()
    }

    fun screenRecorderCheckChanged(isChecked: Boolean) {
        if (isChecked) {
            if (!UxMobile.sessionManager.screenRecorder.isRunning) {
                UxMobile.sessionManager.screenRecorder.start()
            }
        } else {
            if (UxMobile.sessionManager.screenRecorder.isRunning) {
                UxMobile.sessionManager.screenRecorder.stop()
            }
        }
        updateStates()
    }

    fun connectionManagerCheckChanged(isChecked: Boolean) {
        if (isChecked) {
            UxMobile.sessionManager.connectionManager.start()
        } else {
            UxMobile.sessionManager.connectionManager.stop()
        }
        updateStates()
    }

    fun persisterCheckChanged(isChecked: Boolean) {
        if (isChecked) {
            UxMobile.sessionManager.persister.start()
        } else {
            UxMobile.sessionManager.persister.stop()
        }
        updateStates()
    }

    fun senderCheckChanged(isChecked: Boolean) {
        if (isChecked) {
            UxMobile.sessionManager.sender.start()
        } else {
            UxMobile.sessionManager.sender.stop()
        }
        updateStates()
    }

    private fun updateStates() {
        eventRecorderEnabled.value = UxMobile.sessionManager.eventRecorder.isRunning
        screenRecorderEnabled.value = UxMobile.sessionManager.screenRecorder.isRunning
        connectionManagerEnabled.value = UxMobile.sessionManager.connectionManager.isRunning
        persisterEnabled.value = UxMobile.sessionManager.persister.isRunning
        senderEnabled.value = UxMobile.sessionManager.sender.isRunning
    }
}
