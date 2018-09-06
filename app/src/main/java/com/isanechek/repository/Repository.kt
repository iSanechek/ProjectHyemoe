package com.isanechek.repository

interface Repository<T> {
    fun listing(request: Request?): Listing<T>
    suspend fun loadData(request: Request?)
}