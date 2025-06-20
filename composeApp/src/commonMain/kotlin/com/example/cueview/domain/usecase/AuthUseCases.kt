package com.example.cueview.domain.usecase

import com.example.cueview.domain.model.UserProfile
import com.example.cueview.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use cases for authentication operations
 */

class SignInUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<UserProfile> {
        return repository.signInWithEmail(email, password)
    }
}

class SignUpUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String, displayName: String): Result<UserProfile> {
        return repository.signUpWithEmail(email, password, displayName)
    }
}

class SignOutUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.signOut()
    }
}

class GetCurrentUserUseCase(
    private val repository: AuthRepository
) {
    operator fun invoke(): Flow<UserProfile?> {
        return repository.getCurrentUser()
    }
}

class ResetPasswordUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        return repository.resetPassword(email)
    }
}
