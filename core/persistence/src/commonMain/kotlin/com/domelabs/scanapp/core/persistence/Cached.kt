package com.domelabs.scanapp.core.persistence

interface Cached {
    suspend fun clear()
}
