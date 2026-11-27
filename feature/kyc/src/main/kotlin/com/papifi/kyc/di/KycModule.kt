package com.papifi.kyc.di

import com.papifi.kyc.data.FakeKycRepository
import com.papifi.kyc.domain.KycRepository
import com.papifi.kyc.ui.document.KycDocumentUploadViewModel
import com.papifi.kyc.ui.selfie.KycSelfieViewModel
import com.papifi.kyc.ui.start.KycStartViewModel
import com.papifi.kyc.ui.status.KycStatusViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val kycModule = module {
    single<KycRepository> { FakeKycRepository() }
    viewModel { KycStartViewModel(get()) }
    viewModel { KycDocumentUploadViewModel(get()) }
    viewModel { KycSelfieViewModel(get()) }
    viewModel { KycStatusViewModel(get()) }
}
