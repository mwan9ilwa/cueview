package com.example.cueview.domain.usecase

import com.example.cueview.domain.model.UserShow
import com.example.cueview.domain.repository.UserRepository

/**
 * Use cases for user operations and library management
 */

class AddShowToLibraryUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userShow: UserShow): Result<Unit> {
        return repository.addShowToLibrary(userShow)
    }
}

class RemoveShowFromLibraryUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: String, showId: Int): Result<Unit> {
        return repository.removeShowFromLibrary(userId, showId)
    }
}
