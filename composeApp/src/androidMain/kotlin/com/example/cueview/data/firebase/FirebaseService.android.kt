package com.example.cueview.data.firebase

import com.example.cueview.domain.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

/**
 * Android Firebase implementation using real Firebase SDK
 */
class FirebaseServiceImpl : FirebaseService {
    
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    
    companion object {
        private const val COLLECTION_USERS = "users"
        private const val COLLECTION_USER_SHOWS = "user_shows"
    }
    
    override suspend fun signInWithEmail(email: String, password: String): Result<UserProfile> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            
            if (firebaseUser != null) {
                val userProfile = getUserProfileFromFirestore(firebaseUser.uid) 
                    ?: createUserProfileFromFirebaseUser(firebaseUser)
                Result.success(userProfile)
            } else {
                Result.failure(Exception("Authentication failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUpWithEmail(email: String, password: String, displayName: String): Result<UserProfile> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            
            if (firebaseUser != null) {
                // Update display name
                if (displayName.isNotBlank()) {
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName)
                        .build()
                    firebaseUser.updateProfile(profileUpdates).await()
                }
                
                val userProfile = UserProfile(
                    id = firebaseUser.uid,
                    email = email,
                    displayName = displayName.ifBlank { email.substringBefore("@") },
                    joinDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
                )
                
                // Save user profile to Firestore
                saveUserProfileToFirestore(userProfile)
                
                Result.success(userProfile)
            } else {
                Result.failure(Exception("User creation failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithGoogle(): Result<UserProfile> {
        return try {
            // TODO: Implement Google Sign-In with Firebase
            Result.failure(Exception("Google Sign-In not implemented yet"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUser(): Flow<UserProfile?> {
        return callbackFlow {
            val authStateListener = FirebaseAuth.AuthStateListener { auth ->
                val firebaseUser = auth.currentUser
                trySend(
                    if (firebaseUser != null) {
                        createUserProfileFromFirebaseUser(firebaseUser)
                    } else {
                        null
                    }
                )
            }
            
            auth.addAuthStateListener(authStateListener)
            
            awaitClose {
                auth.removeAuthStateListener(authStateListener)
            }
        }
    }

    override suspend fun getUserProfile(userId: String): Result<UserProfile?> {
        return try {
            val profile = getUserProfileFromFirestore(userId)
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserProfile(profile: UserProfile): Result<Unit> {
        return try {
            saveUserProfileToFirestore(profile)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getUserShows(userId: String): Flow<List<UserShow>> {
        return callbackFlow {
            val listenerRegistration = firestore.collection(COLLECTION_USER_SHOWS)
                .whereEqualTo("userId", userId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    
                    val shows = snapshot?.documents?.mapNotNull { doc ->
                        try {
                            val data = doc.data ?: return@mapNotNull null
                            UserShow(
                                id = doc.id,
                                userId = data["userId"] as String,
                                showId = (data["showId"] as Long).toInt(),
                                showName = data["showName"] as String,
                                posterPath = data["posterPath"] as? String,
                                status = WatchStatus.valueOf(data["status"] as String),
                                currentSeason = (data["currentSeason"] as? Long)?.toInt() ?: 1,
                                currentEpisode = (data["currentEpisode"] as? Long)?.toInt() ?: 1,
                                personalRating = data["personalRating"] as? Double,
                                personalNotes = data["personalNotes"] as? String,
                                dateAdded = kotlinx.datetime.LocalDate.fromEpochDays(
                                    ((data["dateAdded"] as Long) / (24 * 60 * 60 * 1000)).toInt()
                                ),
                                lastWatched = (data["lastWatched"] as? Long)?.let { 
                                    kotlinx.datetime.LocalDate.fromEpochDays((it / (24 * 60 * 60 * 1000)).toInt())
                                },
                                watchedEpisodes = emptyList() // TODO: Implement watched episodes parsing
                            )
                        } catch (e: Exception) {
                            null // Skip invalid documents
                        }
                    } ?: emptyList()
                    
                    trySend(shows)
                }
            
            awaitClose {
                listenerRegistration.remove()
            }
        }
    }

    override suspend fun addShowToLibrary(userShow: UserShow): Result<Unit> {
        return try {
            val data = mapOf(
                "userId" to userShow.userId,
                "showId" to userShow.showId,
                "showName" to userShow.showName,
                "posterPath" to userShow.posterPath,
                "status" to userShow.status.name,
                "currentSeason" to userShow.currentSeason,
                "currentEpisode" to userShow.currentEpisode,
                "personalRating" to userShow.personalRating,
                "personalNotes" to userShow.personalNotes,
                "dateAdded" to userShow.dateAdded.toEpochDays() * 24 * 60 * 60 * 1000L,
                "lastWatched" to userShow.lastWatched?.toEpochDays()?.let { it * 24 * 60 * 60 * 1000L }
            )
            
            if (userShow.id.isNotBlank()) {
                firestore.collection(COLLECTION_USER_SHOWS)
                    .document(userShow.id)
                    .set(data)
                    .await()
            } else {
                firestore.collection(COLLECTION_USER_SHOWS)
                    .add(data)
                    .await()
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateShowInLibrary(userShow: UserShow): Result<Unit> {
        return try {
            val data = mapOf(
                "userId" to userShow.userId,
                "showId" to userShow.showId,
                "showName" to userShow.showName,
                "posterPath" to userShow.posterPath,
                "status" to userShow.status.name,
                "currentSeason" to userShow.currentSeason,
                "currentEpisode" to userShow.currentEpisode,
                "personalRating" to userShow.personalRating,
                "personalNotes" to userShow.personalNotes,
                "dateAdded" to userShow.dateAdded.toEpochDays() * 24 * 60 * 60 * 1000L,
                "lastWatched" to userShow.lastWatched?.toEpochDays()?.let { it * 24 * 60 * 60 * 1000L }
            )
            
            firestore.collection(COLLECTION_USER_SHOWS)
                .document(userShow.id)
                .update(data)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeShowFromLibrary(userId: String, showId: Int): Result<Unit> {
        return try {
            val query = firestore.collection(COLLECTION_USER_SHOWS)
                .whereEqualTo("userId", userId)
                .whereEqualTo("showId", showId)
                .get()
                .await()
            
            for (document in query.documents) {
                document.reference.delete().await()
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateShowProgress(userId: String, showId: Int, season: Int, episode: Int): Result<Unit> {
        return try {
            val query = firestore.collection(COLLECTION_USER_SHOWS)
                .whereEqualTo("userId", userId)
                .whereEqualTo("showId", showId)
                .get()
                .await()
                
            for (document in query.documents) {
                document.reference.update(
                    mapOf(
                        "currentSeason" to season,
                        "currentEpisode" to episode,
                        "lastWatched" to Clock.System.todayIn(TimeZone.currentSystemDefault()).toEpochDays() * 24 * 60 * 60 * 1000L
                    )
                ).await()
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateShowStatus(userId: String, showId: Int, status: WatchStatus): Result<Unit> {
        return try {
            val query = firestore.collection(COLLECTION_USER_SHOWS)
                .whereEqualTo("userId", userId)
                .whereEqualTo("showId", showId)
                .get()
                .await()
                
            for (document in query.documents) {
                document.reference.update("status", status.name).await()
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun addWatchedEpisode(userId: String, showId: Int, episode: WatchedEpisode): Result<Unit> {
        return try {
            val query = firestore.collection(COLLECTION_USER_SHOWS)
                .whereEqualTo("userId", userId)
                .whereEqualTo("showId", showId)
                .get()
                .await()
                
            for (document in query.documents) {
                // Get current watched episodes
                val currentData = document.data
                val currentWatchedEpisodes = currentData?.get("watchedEpisodes") as? List<Map<String, Any>> ?: emptyList()
                
                // Add new episode to the list
                val episodeData = mapOf(
                    "seasonNumber" to episode.seasonNumber,
                    "episodeNumber" to episode.episodeNumber,
                    "watchedDate" to episode.watchedDate
                )
                
                val updatedWatchedEpisodes = currentWatchedEpisodes + episodeData
                
                document.reference.update(
                    mapOf(
                        "watchedEpisodes" to updatedWatchedEpisodes,
                        "lastWatched" to Clock.System.todayIn(TimeZone.currentSystemDefault()).toEpochDays() * 24 * 60 * 60 * 1000L
                    )
                ).await()
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun rateShow(userId: String, showId: Int, rating: Double): Result<Unit> {
        return try {
            val query = firestore.collection(COLLECTION_USER_SHOWS)
                .whereEqualTo("userId", userId)
                .whereEqualTo("showId", showId)
                .get()
                .await()
                
            for (document in query.documents) {
                document.reference.update("personalRating", rating).await()
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun addShowNotes(userId: String, showId: Int, notes: String): Result<Unit> {
        return try {
            val query = firestore.collection(COLLECTION_USER_SHOWS)
                .whereEqualTo("userId", userId)
                .whereEqualTo("showId", showId)
                .get()
                .await()
                
            for (document in query.documents) {
                document.reference.update("personalNotes", notes).await()
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun markSeasonCompleted(userId: String, showId: Int, season: Int): Result<Unit> {
        return try {
            val query = firestore.collection(COLLECTION_USER_SHOWS)
                .whereEqualTo("userId", userId)
                .whereEqualTo("showId", showId)
                .get()
                .await()
                
            for (document in query.documents) {
                // Mark all episodes in the season as watched (simplified - just update progress)
                document.reference.update(
                    mapOf(
                        "currentSeason" to (season + 1),
                        "currentEpisode" to 1,
                        "lastWatched" to Clock.System.todayIn(TimeZone.currentSystemDefault()).toEpochDays() * 24 * 60 * 60 * 1000L
                    )
                ).await()
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Helper functions
    private suspend fun getUserProfileFromFirestore(userId: String): UserProfile? {
        return try {
            val document = firestore.collection(COLLECTION_USERS)
                .document(userId)
                .get()
                .await()
            
            if (document.exists()) {
                val data = document.data!!
                UserProfile(
                    id = userId,
                    email = data["email"] as String,
                    displayName = data["displayName"] as String,
                    joinDate = Instant.fromEpochMilliseconds(data["joinDate"] as Long)
                        .let { instant ->
                            kotlinx.datetime.LocalDate.fromEpochDays(
                                (instant.toEpochMilliseconds() / (24 * 60 * 60 * 1000)).toInt()
                            )
                        }
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    private suspend fun saveUserProfileToFirestore(profile: UserProfile) {
        val data = mapOf(
            "email" to profile.email,
            "displayName" to profile.displayName,
            "joinDate" to profile.joinDate.toEpochDays() * 24 * 60 * 60 * 1000L // Convert to milliseconds
        )
        
        firestore.collection(COLLECTION_USERS)
            .document(profile.id)
            .set(data)
            .await()
    }
    
    private fun createUserProfileFromFirebaseUser(firebaseUser: com.google.firebase.auth.FirebaseUser): UserProfile {
        return UserProfile(
            id = firebaseUser.uid,
            email = firebaseUser.email ?: "",
            displayName = firebaseUser.displayName ?: firebaseUser.email?.substringBefore("@") ?: "",
            joinDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
        )
    }
}
