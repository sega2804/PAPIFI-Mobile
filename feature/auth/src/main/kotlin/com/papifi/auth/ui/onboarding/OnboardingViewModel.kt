package com.papifi.auth.ui.onboarding

import com.papifi.ui.BaseViewModel
import com.papifi.ui.UiState
import com.papifi.ui.ViewEffect
import com.papifi.ui.ViewIntent

data class OnboardingPage(
    val title: String,
    val description: String,
    val iconResName: String = ""
)

data class OnboardingUiState(
    val currentPage: Int = 0,
    val pages: List<OnboardingPage> = listOf(
        OnboardingPage(
            title = "Multi-Currency Support",
            description = "Hold, send, and convert money across 30+ global currencies with real-time exchange rates."
        ),
        OnboardingPage(
            title = "Instant Money Transfers",
            description = "Send funds to friends, family, or businesses worldwide instantly with zero delay."
        ),
        OnboardingPage(
            title = "Low & Transparent Fees",
            description = "No hidden charges. Enjoy market-leading competitive rates and ultra-low transaction fees."
        ),
        OnboardingPage(
            title = "Secure Wallet",
            description = "Your funds are protected with bank-grade encryption, biometrics, and fraud monitoring."
        ),
        OnboardingPage(
            title = "Instant Virtual Cards",
            description = "Create customizable virtual debit cards in seconds for safe online shopping worldwide."
        ),
        OnboardingPage(
            title = "24/7 Customer Support",
            description = "Our support team is available 24/7 to assist you with any questions or issues."
        ),
        OnboardingPage(
            title = "Global Financial Reach",
            description = "Connect your finances seamlessly across 150+ countries with easy cross-border access."
        )
    )
) : UiState

sealed interface OnboardingIntent : ViewIntent {
    data object NextClicked : OnboardingIntent
    data object SkipClicked : OnboardingIntent
    data class PageSelected(val index: Int) : OnboardingIntent
    data object LoginClicked : OnboardingIntent
}

sealed interface OnboardingEffect : ViewEffect {
    data object NavigateToSignUp : OnboardingEffect
    data object NavigateToLogin : OnboardingEffect
    data class ScrollToPage(val page: Int) : OnboardingEffect
}

class OnboardingViewModel : BaseViewModel<OnboardingIntent, OnboardingUiState, OnboardingEffect>(
    OnboardingUiState()
) {
    override fun handleIntent(intent: OnboardingIntent) {
        when (intent) {
            OnboardingIntent.NextClicked -> {
                val nextPage = currentState.currentPage + 1
                if (nextPage < currentState.pages.size) {
                    setState { copy(currentPage = nextPage) }
                    setEffect { OnboardingEffect.ScrollToPage(nextPage) }
                } else {
                    setEffect { OnboardingEffect.NavigateToSignUp }
                }
            }
            OnboardingIntent.SkipClicked -> {
                setEffect { OnboardingEffect.NavigateToSignUp }
            }
            is OnboardingIntent.PageSelected -> {
                setState { copy(currentPage = intent.index) }
            }
            OnboardingIntent.LoginClicked -> {
                setEffect { OnboardingEffect.NavigateToLogin }
            }
        }
    }
}
