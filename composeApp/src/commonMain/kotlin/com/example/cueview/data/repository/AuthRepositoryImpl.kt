package com.example.cueview.data.repository

import com.example.cueview.data.firebase.FirebaseService
import com.example.cueview.domain.model.UserProfile
import com.example.cueview.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

/**
 * Firebase Authentication Repository Implementation
 */
class AuthRepositoryImpl(
    private val firebaseService: FirebaseService
) : AuthRepository {
    
    override fun getCurrentUser(): Flow<UserProfile?> {
        return firebaseService.getCurrentUser()
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<UserProfile> {
        return firebaseService.signInWithEmail(email, password)
    }

    override suspend fun signUpWithEmail(email: String, password: String, displayName: String): Result<UserProfile> {
        return firebaseService.signUpWithEmail(email, password, displayName)
    }

    override suspend fun signInWithGoogle(): Result<UserProfile> {
        return firebaseService.signInWithGoogle()
    }

    override suspend fun signOut(): Result<Unit> {
        return firebaseService.signOut()
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            // TODO: Implement Firebase password reset
            Result.failure(Exception("Password reset not implemented yet"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
