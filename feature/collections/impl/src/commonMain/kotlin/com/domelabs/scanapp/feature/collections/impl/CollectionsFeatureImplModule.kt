package com.domelabs.scanapp.feature.collections.impl

import com.domelabs.scanapp.feature.collections.impl.data.repository.CollectionsRepositoryImpl
import com.domelabs.scanapp.feature.collections.impl.data.repository.ScannedItemsRepositoryImpl
import com.domelabs.scanapp.feature.collections.impl.data.source.CollectionsLocalSource
import com.domelabs.scanapp.feature.collections.impl.data.source.CollectionsLocalSourceImpl
import com.domelabs.scanapp.feature.collections.impl.data.source.ScannedItemsLocalSource
import com.domelabs.scanapp.feature.collections.impl.data.source.ScannedItemsLocalSourceImpl
import com.domelabs.scanapp.feature.collections.impl.domain.repository.CollectionsRepository
import com.domelabs.scanapp.feature.collections.impl.domain.repository.ScannedItemsRepository
import com.domelabs.scanapp.feature.collections.impl.domain.usecase.CreateCollectionUseCase
import com.domelabs.scanapp.feature.collections.impl.domain.usecase.DeleteCollectionUseCase
import com.domelabs.scanapp.feature.collections.impl.domain.usecase.DeleteItemUseCase
import com.domelabs.scanapp.feature.collections.impl.domain.usecase.GetItemByIdUseCase
import com.domelabs.scanapp.feature.collections.impl.domain.usecase.MoveItemToCollectionUseCase
import com.domelabs.scanapp.feature.collections.impl.domain.usecase.ObserveItemByIdUseCase
import com.domelabs.scanapp.feature.collections.impl.domain.usecase.ObserveCollectionsUseCase
import com.domelabs.scanapp.feature.collections.impl.domain.usecase.ObserveItemsInCollectionUseCase
import com.domelabs.scanapp.feature.collections.impl.domain.usecase.ObserveRecentItemsUseCase
import com.domelabs.scanapp.feature.collections.impl.domain.usecase.RecolorCollectionUseCase
import com.domelabs.scanapp.feature.collections.impl.domain.usecase.RegisterScannedItemUseCase
import com.domelabs.scanapp.feature.collections.impl.domain.usecase.RenameCollectionUseCase
import com.domelabs.scanapp.feature.collections.impl.domain.usecase.RenameItemUseCase
import com.domelabs.scanapp.feature.collections.impl.domain.usecase.SearchItemsInCollectionUseCase
import com.domelabs.scanapp.feature.collections.impl.presentation.create.CreateCollectionViewModel
import com.domelabs.scanapp.feature.collections.impl.presentation.detail.CollectionDetailViewModel
import com.domelabs.scanapp.feature.collections.impl.presentation.list.CollectionsListViewModel
import com.domelabs.scanapp.feature.collections.impl.presentation.picker.CollectionPickerViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val collectionsFeatureImplModule = module {

    single<CollectionsLocalSource> { CollectionsLocalSourceImpl(get()) }
    single<ScannedItemsLocalSource> { ScannedItemsLocalSourceImpl(get()) }

    single<CollectionsRepository> {
        CollectionsRepositoryImpl(
            collectionsSource = get(),
            itemsSource = get(),
        )
    }
    single<ScannedItemsRepository> {
        ScannedItemsRepositoryImpl(
            itemsSource = get(),
            collectionsSource = get(),
            collectionsRepository = get(),
        )
    }

    factory { ObserveCollectionsUseCase(get()) }
    factory { CreateCollectionUseCase(get()) }
    factory { RenameCollectionUseCase(get()) }
    factory { RecolorCollectionUseCase(get()) }
    factory { DeleteCollectionUseCase(get()) }

    factory { ObserveRecentItemsUseCase(get()) }
    factory { ObserveItemsInCollectionUseCase(get()) }
    factory { SearchItemsInCollectionUseCase(get()) }
    factory { GetItemByIdUseCase(get()) }
    factory { ObserveItemByIdUseCase(get()) }
    factory { RegisterScannedItemUseCase(get()) }
    factory { MoveItemToCollectionUseCase(get()) }
    factory { RenameItemUseCase(get()) }
    factory { DeleteItemUseCase(get()) }

    viewModelOf(::CollectionPickerViewModel)
    viewModelOf(::CreateCollectionViewModel)
    viewModelOf(::CollectionsListViewModel)
    viewModelOf(::CollectionDetailViewModel)
}
