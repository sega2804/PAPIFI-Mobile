package com.papifi.send.di

import com.papifi.common.IdempotencyKeyGenerator
import com.papifi.common.UuidIdempotencyKeyGenerator
import com.papifi.send.data.FakeSendRepository
import com.papifi.send.domain.SendRepository
import com.papifi.send.ui.amount.SendAmountViewModel
import com.papifi.send.ui.beneficiaries.BeneficiaryViewModel
import com.papifi.send.ui.review.SendReviewViewModel
import com.papifi.send.ui.status.SendStatusViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val sendModule = module {
    single<IdempotencyKeyGenerator> { UuidIdempotencyKeyGenerator() }
    single<SendRepository> { FakeSendRepository() }
    viewModel { BeneficiaryViewModel(get()) }
    viewModel { SendAmountViewModel() }
    viewModel { SendReviewViewModel(get(), get()) }
    viewModel { SendStatusViewModel() }
}
