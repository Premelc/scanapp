package com.domelabs.scanapp.screens.detail

import androidx.lifecycle.ViewModel
import com.domelabs.scanapp.data.MuseumObject
import com.domelabs.scanapp.data.MuseumRepository
import kotlinx.coroutines.flow.Flow

class DetailViewModel(private val museumRepository: MuseumRepository) : ViewModel() {
    fun getObject(objectId: Int): Flow<MuseumObject?> =
        museumRepository.getObjectById(objectId)
}
