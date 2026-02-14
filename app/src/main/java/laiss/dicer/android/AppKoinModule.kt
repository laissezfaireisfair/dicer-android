package laiss.dicer.android

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import laiss.dicer.android.viewModels.SelectDicesViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appKoinModule = module {
    single<CoroutineDispatcher>(named(InjectedCoroutineDispatcher.Main)) { Dispatchers.Main }
    single<CoroutineDispatcher>(named(InjectedCoroutineDispatcher.Default)) { Dispatchers.Default }
    single<CoroutineDispatcher>(named(InjectedCoroutineDispatcher.IO)) { Dispatchers.IO }

    viewModel {
        SelectDicesViewModel(defaultDispatcher = get(named(InjectedCoroutineDispatcher.Default)))
    }
}

enum class InjectedCoroutineDispatcher { Main, Default, IO }