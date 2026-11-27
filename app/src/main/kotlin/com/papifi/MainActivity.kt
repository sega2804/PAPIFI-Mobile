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
import com.papifi.kyc.ui.document.KycDocumentUploadScreen
import com.papifi.kyc.ui.document.KycDocumentUploadViewModel
import com.papifi.kyc.ui.selfie.KycSelfieScreen
import com.papifi.kyc.ui.selfie.KycSelfieViewModel
import com.papifi.kyc.ui.start.KycStartScreen
import com.papifi.kyc.ui.start.KycStartViewModel
import com.papifi.kyc.ui.status.KycStatusScreen
import com.papifi.kyc.ui.status.KycStatusViewModel
import com.papifi.profile.ui.ProfileScreen
import com.papifi.profile.ui.ProfileViewModel
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
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(text = "Welcome to Papifi")
                                    PapifiButton(
                                        text = "View Profile & Settings",
                                        onClick = { navController.navigate("profile") }
                                    )
                                    PapifiButton(
                                        text = "Identity Verification (KYC)",
                                        onClick = { navController.navigate("kyc_start") }
                                    )
                                    PapifiButton(
                                        text = "Log Out",
                                        onClick = {
                                            navController.navigate("login") {
                                                popUpTo("home") { inclusive = true }
                                            }
                                        }
                                    )
                                }
                            }
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
                    }
                }
            }
        }
    }
}
