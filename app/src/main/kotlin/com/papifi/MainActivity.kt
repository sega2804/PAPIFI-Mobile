@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.papifi

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.papifi.addmoney.ui.bankdetails.NgnBankDetailsScreen
import com.papifi.addmoney.ui.bankdetails.NgnBankDetailsViewModel
import com.papifi.addmoney.ui.foreign.ForeignDepositDetailsScreen
import com.papifi.addmoney.ui.foreign.ForeignDepositDetailsViewModel
import com.papifi.addmoney.ui.pending.FundingPendingScreen
import com.papifi.addmoney.ui.pending.FundingPendingViewModel
import com.papifi.addmoney.ui.selector.AddMoneySelectorScreen
import com.papifi.addmoney.ui.selector.AddMoneySelectorViewModel
import com.papifi.auth.ui.login.LoginScreen
import com.papifi.auth.ui.login.LoginViewModel
import com.papifi.auth.ui.onboarding.OnboardingScreen
import com.papifi.auth.ui.onboarding.OnboardingViewModel
import com.papifi.auth.ui.otp.OtpVerificationScreen
import com.papifi.auth.ui.otp.OtpVerificationViewModel
import com.papifi.auth.ui.pin.CreatePinScreen
import com.papifi.auth.ui.pin.CreatePinViewModel
import com.papifi.auth.ui.reset.ForgotPasswordScreen
import com.papifi.auth.ui.reset.ForgotPasswordViewModel
import com.papifi.auth.ui.signup.SignUpScreen
import com.papifi.auth.ui.signup.SignUpViewModel
import com.papifi.designsystem.PapifiButton
import com.papifi.designsystem.PapifiTheme
import com.papifi.exchange.ui.converter.CurrencyConverterScreen
import com.papifi.exchange.ui.converter.CurrencyConverterViewModel
import com.papifi.exchange.ui.quote.ExecutableQuoteScreen
import com.papifi.exchange.ui.quote.ExecutableQuoteViewModel
import com.papifi.kyc.ui.document.KycDocumentUploadScreen
import com.papifi.kyc.ui.document.KycDocumentUploadViewModel
import com.papifi.kyc.ui.selfie.KycSelfieScreen
import com.papifi.kyc.ui.selfie.KycSelfieViewModel
import com.papifi.kyc.ui.start.KycStartScreen
import com.papifi.kyc.ui.start.KycStartViewModel
import com.papifi.kyc.ui.status.KycStatusScreen
import com.papifi.kyc.ui.status.KycStatusViewModel
import com.papifi.model.Currency
import com.papifi.profile.ui.ProfileScreen
import com.papifi.profile.ui.ProfileViewModel
import com.papifi.receive.ui.ReceiveScreen
import com.papifi.receive.ui.ReceiveViewModel
import com.papifi.send.domain.TransferRoute
import com.papifi.send.ui.amount.SendAmountScreen
import com.papifi.send.ui.amount.SendAmountViewModel
import com.papifi.send.ui.beneficiaries.BeneficiaryListScreen
import com.papifi.send.ui.beneficiaries.BeneficiaryViewModel
import com.papifi.send.ui.destination.TransferDestinationScreen
import com.papifi.send.ui.review.SendReviewScreen
import com.papifi.send.ui.review.SendReviewViewModel
import com.papifi.send.ui.status.SendStatusScreen
import com.papifi.send.ui.status.SendStatusViewModel
import com.papifi.wallet.ui.WalletScreen
import com.papifi.wallet.ui.WalletViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PapifiTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "onboarding",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("onboarding") {
                            val viewModel: OnboardingViewModel = koinViewModel()
                            OnboardingScreen(
                                viewModel = viewModel,
                                onNavigateToSignUp = { navController.navigate("signup") },
                                onNavigateToLogin = { navController.navigate("login") }
                            )
                        }

                        composable("signup") {
                            val viewModel: SignUpViewModel = koinViewModel()
                            SignUpScreen(
                                viewModel = viewModel,
                                onNavigateToOtp = { contact -> navController.navigate("otp/$contact") },
                                onNavigateToLogin = { navController.navigate("login") }
                            )
                        }

                        composable(
                            route = "otp/{contact}",
                            arguments = listOf(navArgument("contact") { type = NavType.StringType; defaultValue = "" })
                        ) { backStackEntry ->
                            val contact = backStackEntry.arguments?.getString("contact") ?: ""
                            val viewModel: OtpVerificationViewModel = koinViewModel()
                            OtpVerificationScreen(
                                viewModel = viewModel,
                                contact = contact,
                                onNavigateToCreatePin = { navController.navigate("create_pin") },
                                onNavigateToHome = {
                                    navController.navigate("home") {
                                        popUpTo("onboarding") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("otp") {
                            val viewModel: OtpVerificationViewModel = koinViewModel()
                            OtpVerificationScreen(
                                viewModel = viewModel,
                                contact = "",
                                onNavigateToCreatePin = { navController.navigate("create_pin") },
                                onNavigateToHome = {
                                    navController.navigate("home") {
                                        popUpTo("onboarding") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("login") {
                            val viewModel: LoginViewModel = koinViewModel()
                            LoginScreen(
                                viewModel = viewModel,
                                onNavigateToHome = {
                                    navController.navigate("home") {
                                        popUpTo("onboarding") { inclusive = true }
                                    }
                                },
                                onNavigateToCreatePin = { navController.navigate("create_pin") },
                                onNavigateToForgotPassword = { navController.navigate("forgot_password") },
                                onNavigateToSignUp = { navController.navigate("signup") }
                            )
                        }

                        composable("forgot_password") {
                            val viewModel: ForgotPasswordViewModel = koinViewModel()
                            ForgotPasswordScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("create_pin") {
                            val viewModel: CreatePinViewModel = koinViewModel()
                            CreatePinScreen(
                                viewModel = viewModel,
                                onNavigateToHome = {
                                    navController.navigate("home") {
                                        popUpTo("onboarding") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("home") {
                            val walletViewModel: WalletViewModel = koinViewModel()
                            WalletScreen(
                                viewModel = walletViewModel,
                                onNavigateToAddMoney = { navController.navigate("add_money") },
                                onNavigateToSendMoney = { navController.navigate("beneficiaries") },
                                onReceiveMoney = { navController.navigate("receive") },
                                onNavigateToActivity = { },
                                onNavigateToProfile = { navController.navigate("profile") },
                                onNavigateToKyc = { navController.navigate("kyc_start") },
                                onNavigateToRecipients = { navController.navigate("beneficiaries") },
                                onNavigateToExchange = { navController.navigate("converter") }
                            )
                        }

                        composable("receive") {
                            val viewModel: ReceiveViewModel = koinViewModel()
                            ReceiveScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("converter") {
                            val viewModel: CurrencyConverterViewModel = koinViewModel()
                            CurrencyConverterScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToQuote = { fromCurrency, toCurrency, amountMinor ->
                                    navController.navigate("executable_quote/${fromCurrency.code}/${toCurrency.code}/$amountMinor")
                                }
                            )
                        }

                        composable(
                            route = "executable_quote/{fromCurrency}/{toCurrency}/{amount}",
                            arguments = listOf(
                                navArgument("fromCurrency") { type = NavType.StringType; defaultValue = "USD" },
                                navArgument("toCurrency") { type = NavType.StringType; defaultValue = "NGN" },
                                navArgument("amount") { type = NavType.LongType; defaultValue = 0L }
                            )
                        ) { backStackEntry ->
                            val fromCurrencyStr = backStackEntry.arguments?.getString("fromCurrency") ?: "USD"
                            val toCurrencyStr = backStackEntry.arguments?.getString("toCurrency") ?: "NGN"
                            val amountMinor = backStackEntry.arguments?.getLong("amount") ?: 0L

                            val fromCurrency = try { Currency.valueOf(fromCurrencyStr) } catch (_: Exception) { Currency.USD }
                            val toCurrency = try { Currency.valueOf(toCurrencyStr) } catch (_: Exception) { Currency.NGN }

                            val viewModel: ExecutableQuoteViewModel = koinViewModel()
                            ExecutableQuoteScreen(
                                fromCurrency = fromCurrency,
                                toCurrency = toCurrency,
                                fromAmountMinor = amountMinor,
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToHome = {
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("profile") {
                            val viewModel: ProfileViewModel = koinViewModel()
                            ProfileScreen(
                                viewModel = viewModel,
                                onNavigateToLogin = {
                                    navController.navigate("login") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                },
                                onNavigateToKycStart = { navController.navigate("kyc_start") },
                                onNavigateToKycStatus = { navController.navigate("kyc_status") }
                            )
                        }

                        composable("kyc_start") {
                            val viewModel: KycStartViewModel = koinViewModel()
                            KycStartScreen(
                                viewModel = viewModel,
                                onNavigateToDocumentUpload = { navController.navigate("kyc_document") },
                                onNavigateToStatus = { navController.navigate("kyc_status") }
                            )
                        }

                        composable("kyc_document") {
                            val viewModel: KycDocumentUploadViewModel = koinViewModel()
                            KycDocumentUploadScreen(
                                viewModel = viewModel,
                                onNavigateToSelfie = { navController.navigate("kyc_selfie") }
                            )
                        }

                        composable("kyc_selfie") {
                            val viewModel: KycSelfieViewModel = koinViewModel()
                            KycSelfieScreen(
                                viewModel = viewModel,
                                onNavigateToStatus = {
                                    navController.navigate("kyc_status") {
                                        popUpTo("kyc_start") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("kyc_status") {
                            val viewModel: KycStatusViewModel = koinViewModel()
                            KycStatusScreen(
                                viewModel = viewModel,
                                onNavigateToHome = {
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                },
                                onNavigateToStart = {
                                    navController.navigate("kyc_start") {
                                        popUpTo("kyc_status") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("add_money") {
                            val viewModel: AddMoneySelectorViewModel = koinViewModel()
                            AddMoneySelectorScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToNgnBankDetails = { navController.navigate("ngn_bank_details") },
                                onNavigateToForeignDepositDetails = { currency: Currency ->
                                    navController.navigate("foreign_deposit_details/${currency.code}")
                                }
                            )
                        }

                        composable("ngn_bank_details") {
                            val viewModel: NgnBankDetailsViewModel = koinViewModel()
                            NgnBankDetailsScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToPending = { navController.navigate("funding_pending") }
                            )
                        }

                        composable(
                            route = "foreign_deposit_details/{currency}",
                            arguments = listOf(navArgument("currency") { type = NavType.StringType; defaultValue = "USD" })
                        ) { backStackEntry ->
                            val currencyStr = backStackEntry.arguments?.getString("currency") ?: "USD"
                            val currency = try { Currency.valueOf(currencyStr) } catch (_: Exception) { Currency.USD }
                            val viewModel: ForeignDepositDetailsViewModel = koinViewModel()
                            ForeignDepositDetailsScreen(
                                currency = currency,
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToPending = { navController.navigate("funding_pending") }
                            )
                        }

                        composable("funding_pending") {
                            val viewModel: FundingPendingViewModel = koinViewModel()
                            FundingPendingScreen(
                                viewModel = viewModel,
                                onNavigateToWallet = {
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("beneficiaries") {
                            val viewModel: BeneficiaryViewModel = koinViewModel()
                            BeneficiaryListScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToDestination = { beneficiaryId ->
                                    navController.navigate("send_destination")
                                }
                            )
                        }

                        composable("send_destination") {
                            TransferDestinationScreen(
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToAmount = { route ->
                                    navController.navigate("send_amount/${route.name}")
                                }
                            )
                        }

                        composable(
                            route = "send_amount/{route}",
                            arguments = listOf(navArgument("route") { type = NavType.StringType; defaultValue = "LOCAL_BANK" })
                        ) { backStackEntry ->
                            val routeStr = backStackEntry.arguments?.getString("route") ?: "LOCAL_BANK"
                            val route = try { TransferRoute.valueOf(routeStr) } catch (_: Exception) { TransferRoute.LOCAL_BANK }
                            val viewModel: SendAmountViewModel = koinViewModel()
                            SendAmountScreen(
                                route = route,
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToReview = { selectedRoute, amountMinor, note ->
                                    navController.navigate("send_review/1/$amountMinor")
                                }
                            )
                        }

                        composable(
                            route = "send_review/{recipientId}/{amount}",
                            arguments = listOf(
                                navArgument("recipientId") { type = NavType.StringType; defaultValue = "1" },
                                navArgument("amount") { type = NavType.LongType; defaultValue = 0L }
                            )
                        ) { backStackEntry ->
                            val recipientId = backStackEntry.arguments?.getString("recipientId") ?: "1"
                            val amountMinor = backStackEntry.arguments?.getLong("amount") ?: 0L
                            val viewModel: SendReviewViewModel = koinViewModel()
                            SendReviewScreen(
                                recipientId = recipientId,
                                route = TransferRoute.LOCAL_BANK,
                                amountMinor = amountMinor,
                                note = "",
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToStatus = { ref ->
                                    navController.navigate("send_status/$ref") {
                                        popUpTo("home") { inclusive = false }
                                    }
                                }
                            )
                        }

                        composable(
                            route = "send_status/{ref}",
                            arguments = listOf(navArgument("ref") { type = NavType.StringType; defaultValue = "" })
                        ) { backStackEntry ->
                            val ref = backStackEntry.arguments?.getString("ref") ?: ""
                            val viewModel: SendStatusViewModel = koinViewModel()
                            SendStatusScreen(
                                transactionReference = ref,
                                viewModel = viewModel,
                                onNavigateToWallet = {
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
