# Firebase Setup Instructions

## Prerequisites

1. Create a Firebase project at https://console.firebase.google.com/
2. Enable Authentication with Email/Password sign-in method
3. Create a Firestore database in test mode (or production mode with proper security rules)

## Android Configuration

1. **Add Android App to Firebase Project**:
   - Go to your Firebase Console > Project Settings
   - Click "Add app" and select Android
   - Package name: `com.example.cueview`
   - App nickname: `CueView Android` (optional)
   - Debug signing certificate SHA-1: (optional for development)

2. **Download google-services.json**:
   - Download the `google-services.json` file from Firebase Console
   - Place it in: `composeApp/google-services.json`
   - ⚠️ **IMPORTANT**: The file must be at the root of the `composeApp` module, NOT in `src/` folders

3. **Verify File Location**:
   ```
   CueView/
   ├── composeApp/
   │   ├── google-services.json  ← HERE
   │   ├── build.gradle.kts
   │   └── src/
   ```

## iOS Configuration (Optional)

1. **Add iOS App to Firebase Project**:
   - Go to your Firebase Console > Project Settings
   - Click "Add app" and select iOS
   - Bundle ID: `com.example.cueview`
   - App nickname: `CueView iOS` (optional)

2. **Download GoogleService-Info.plist**:
   - Download the `GoogleService-Info.plist` file from Firebase Console
   - Place it in: `iosApp/iosApp/GoogleService-Info.plist`

## Firestore Security Rules

Add these basic security rules in Firebase Console > Firestore Database > Rules:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users can read/write their own profile
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Users can read/write their own shows
    match /user_shows/{showId} {
      allow read, write: if request.auth != null && request.auth.uid == resource.data.userId;
      allow create: if request.auth != null && request.auth.uid == request.resource.data.userId;
    }
  }
}
```

## Testing

1. Build the project: `./gradlew assembleDebug`
2. Run the app and try signing up with a new email
3. Check Firebase Console to see if users are created in Authentication
4. Check Firestore to see if user profiles and shows are stored

## Troubleshooting

### Common Issues:

1. **"Default FirebaseApp is not initialized"**
   - Make sure `google-services.json` is in the correct location
   - Ensure Google Services plugin is applied in `build.gradle.kts`

2. **"FirebaseException: An internal error has occurred"**
   - Check your internet connection
   - Verify Firebase project configuration
   - Check Android app package name matches Firebase configuration

3. **Authentication fails silently**
   - Check Firebase Console > Authentication > Sign-in method
   - Ensure Email/Password is enabled
   - Check if there are any domain restrictions

4. **Firestore permission errors**
   - Update Firestore security rules as shown above
   - Ensure rules allow authenticated users to read/write their data

## Next Steps

After completing setup:
1. Test user registration and login
2. Test adding shows to user library
3. Verify data appears in Firebase Console
4. (Optional) Implement Google Sign-In integration
5. (Optional) Add offline caching with proper sync
