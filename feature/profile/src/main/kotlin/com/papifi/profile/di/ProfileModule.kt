package com.papifi.profile.di

import com.papifi.profile.data.FakeProfileRepository
import com.papifi.profile.domain.ProfileRepository
import com.papifi.profile.ui.ProfileViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val profileModule = module {
    single<ProfileRepository> { FakeProfileRepository() }
    viewModel { ProfileViewModel(get()) }
}
