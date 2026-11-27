package com.papifi.profile.ui

import androidx.lifecycle.viewModelScope
import com.papifi.common.onSuccess
import com.papifi.model.KycStatus
import com.papifi.profile.domain.ProfileRepository
import com.papifi.profile.domain.UserProfile
import com.papifi.ui.BaseViewModel
import com.papifi.ui.UiState
import com.papifi.ui.ViewEffect
import com.papifi.ui.ViewIntent
import kotlinx.coroutines.launch

data class ProfileUiState(
    val userProfile: UserProfile? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showChangePinDialog: Boolean = false,
    val changePinError: String? = null
) : UiState

sealed interface ProfileIntent : ViewIntent {
    data object LoadProfile : ProfileIntent
    data class ToggleBiometrics(val enabled: Boolean) : ProfileIntent
    data class ToggleNotifications(val enabled: Boolean) : ProfileIntent
    data object ShowChangePinDialog : ProfileIntent
    data object DismissChangePinDialog : ProfileIntent
    data class SubmitChangePin(val oldPin: String, val newPin: String) : ProfileIntent
    data object LogoutClicked : ProfileIntent
    data object UpgradeTierClicked : ProfileIntent
    data object HelpAndSupportClicked : ProfileIntent
}

sealed interface ProfileEffect : ViewEffect {
    data object NavigateToLogin : ProfileEffect
    data object NavigateToKycStart : ProfileEffect
    data object NavigateToKycStatus : ProfileEffect
    data class ShowMessage(val message: String) : ProfileEffect
}

class ProfileViewModel(
    private val profileRepository: ProfileRepository
) : BaseViewModel<ProfileIntent, ProfileUiState, ProfileEffect>(ProfileUiState()) {

    init {
        sendIntent(ProfileIntent.LoadProfile)
    }

    override fun handleIntent(intent: ProfileIntent) {
        when (intent) {
            ProfileIntent.LoadProfile -> loadProfile()
            is ProfileIntent.ToggleBiometrics -> toggleBiometrics(intent.enabled)
            is ProfileIntent.ToggleNotifications -> toggleNotifications(intent.enabled)
            ProfileIntent.ShowChangePinDialog -> setState { copy(showChangePinDialog = true, changePinError = null) }
            ProfileIntent.DismissChangePinDialog -> setState { copy(showChangePinDialog = false, changePinError = null) }
            is ProfileIntent.SubmitChangePin -> submitChangePin(intent.oldPin, intent.newPin)
            ProfileIntent.LogoutClicked -> logout()
            ProfileIntent.UpgradeTierClicked -> onUpgradeTierClicked()
            ProfileIntent.HelpAndSupportClicked -> setEffect { ProfileEffect.ShowMessage("Help & Support: Contact support@papifi.com") }
        }
    }

    private fun loadProfile() {
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            profileRepository.getUserProfile().collect { result ->
                result.onSuccess { profile ->
                    setState { copy(userProfile = profile, isLoading = false) }
                }
            }
        }
    }

    private fun toggleBiometrics(enabled: Boolean) {
        viewModelScope.launch {
            profileRepository.updateBiometrics(enabled).onSuccess {
                val current = currentState.userProfile ?: return@onSuccess
                setState { copy(userProfile = current.copy(isBiometricsEnabled = enabled)) }
                setEffect { ProfileEffect.ShowMessage("Biometrics preference updated") }
            }
        }
    }

    private fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            profileRepository.updateNotifications(enabled).onSuccess {
                val current = currentState.userProfile ?: return@onSuccess
                setState { copy(userProfile = current.copy(isNotificationsEnabled = enabled)) }
                setEffect { ProfileEffect.ShowMessage("Notification preference updated") }
            }
        }
    }

    private fun submitChangePin(oldPin: String, newPin: String) {
        if (oldPin.length != 4 || newPin.length != 4) {
            setState { copy(changePinError = "PIN must be exactly 4 digits") }
            return
        }
        viewModelScope.launch {
            profileRepository.changePin(oldPin, newPin).onSuccess {
                setState { copy(showChangePinDialog = false, changePinError = null) }
                setEffect { ProfileEffect.ShowMessage("PIN updated successfully") }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            profileRepository.logout().onSuccess {
                setEffect { ProfileEffect.NavigateToLogin }
            }
        }
    }

    private fun onUpgradeTierClicked() {
        val status = currentState.userProfile?.kycStatus ?: KycStatus.NOT_STARTED
        if (status == KycStatus.NOT_STARTED || status == KycStatus.REJECTED) {
            setEffect { ProfileEffect.NavigateToKycStart }
        } else {
            setEffect { ProfileEffect.NavigateToKycStatus }
        }
    }
}
