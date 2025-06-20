package com.example.cueview.data.firebase

import com.example.cueview.domain.model.UserProfile
import com.example.cueview.domain.model.UserShow
import kotlinx.coroutines.flow.Flow

/**
 * Common Firebase service interface for both Authentication and Firestore
 */
interface FirebaseService {
    
    // Authentication methods
    suspend fun signInWithEmail(email: String, password: String): Result<UserProfile>
    suspend fun signUpWithEmail(email: String, password: String, displayName: String): Result<UserProfile>
    suspend fun signInWithGoogle(): Result<UserProfile>
    suspend fun signOut(): Result<Unit>
    fun getCurrentUser(): Flow<UserProfile?>
    
    // Firestore methods
    suspend fun getUserProfile(userId: String): Result<UserProfile?>
    suspend fun updateUserProfile(profile: UserProfile): Result<Unit>
    fun getUserShows(userId: String): Flow<List<UserShow>>
    suspend fun addShowToLibrary(userShow: UserShow): Result<Unit>
    suspend fun updateShowInLibrary(userShow: UserShow): Result<Unit>
    suspend fun removeShowFromLibrary(userId: String, showId: Int): Result<Unit>
}
