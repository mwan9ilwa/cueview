# Firebase Security Setup Guide

## 🔴 **IMPORTANT SECURITY NOTICE**

Your Firebase API keys are currently **EXPOSED** in the source code. This is a **critical security vulnerability**. Follow this guide to secure your Firebase configuration.

## Issues Found:

1. **API Keys in Source Code**: `google-services.json` and `GoogleService-Info.plist` contain sensitive API keys
2. **Incomplete iOS Implementation**: iOS Firebase is not properly integrated
3. **Missing Firebase Security Rules**: No proper Firestore security rules

## ✅ **Immediate Actions Required:**

### 1. Remove Exposed API Keys from Git History

```bash
# Remove sensitive files from the repository
git rm composeApp/google-services.json
git rm iosApp/iosApp/GoogleService-Info.plist
git commit -m "Remove exposed Firebase configuration files"

# If files are already committed, you may need to clean git history:
# WARNING: This will rewrite git history
git filter-branch --force --index-filter \
  'git rm --cached --ignore-unmatch composeApp/google-services.json iosApp/iosApp/GoogleService-Info.plist' \
  --prune-empty --tag-name-filter cat -- --all
```

### 2. Set Up Secure Firebase Configuration

#### For Android:
1. Copy `composeApp/google-services.json.template` to `composeApp/google-services.json`
2. Replace placeholder values with your actual Firebase project values
3. **Never commit the real `google-services.json` file**

#### For iOS:
1. Copy `iosApp/iosApp/GoogleService-Info.plist.template` to `iosApp/iosApp/GoogleService-Info.plist`
2. Replace placeholder values with your actual Firebase project values
3. **Never commit the real `GoogleService-Info.plist` file**

### 3. Add Firebase iOS SDK

Add Firebase to your iOS project by updating the iOS project dependencies:

1. Open `iosApp/iosApp.xcodeproj` in Xcode
2. Go to File → Add Package Dependencies
3. Add: `https://github.com/firebase/firebase-ios-sdk`
4. Select the following products:
   - FirebaseAuth
   - FirebaseFirestore
   - FirebaseAnalytics

### 4. Implement Firestore Security Rules

Create proper security rules in your Firebase Console:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users can only read/write their own profile
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Users can only read/write their own shows
    match /user_shows/{document} {
      allow read, write: if request.auth != null && 
        request.auth.uid == resource.data.userId;
      allow create: if request.auth != null && 
        request.auth.uid == request.resource.data.userId;
    }
  }
}
```

### 5. Use Environment Variables (Recommended for CI/CD)

For production builds, consider using environment variables:

```bash
# Set environment variables
export FIREBASE_PROJECT_ID="your-project-id"
export FIREBASE_API_KEY_ANDROID="your-android-api-key"
export FIREBASE_API_KEY_IOS="your-ios-api-key"
```

### 6. Validate Security

After setup, verify:
- [ ] Firebase configuration files are not in version control
- [ ] `.gitignore` properly excludes Firebase config files
- [ ] Firestore security rules are properly configured
- [ ] API keys are restricted to your app bundles in Firebase Console

## Firebase Console Security Settings:

1. **Restrict API Keys**:
   - Go to Google Cloud Console → APIs & Services → Credentials
   - For each API key, click "Restrict key"
   - Add application restrictions (Android app SHA-1 fingerprint, iOS bundle ID)

2. **Enable App Check** (Recommended):
   - Protects your Firebase resources from abuse
   - Go to Firebase Console → App Check
   - Enable for your apps

## Current Implementation Status:

### ✅ Android:
- [x] Firebase Auth implemented
- [x] Firestore integration complete
- [x] Proper error handling
- [ ] **Security**: API keys need to be secured

### ⚠️ iOS:
- [ ] Firebase SDK integration needed
- [ ] Real Firebase Auth implementation
- [ ] Real Firestore integration
- [ ] **Security**: API keys need to be secured

## Next Steps:

1. **Immediately**: Secure your API keys using this guide
2. **iOS Implementation**: Complete Firebase SDK integration for iOS
3. **Security Rules**: Implement proper Firestore security rules
4. **Testing**: Test both platforms with secured configuration

## Repository Security Status:

❌ **Current Status**: VULNERABLE (API keys exposed)
✅ **Target Status**: SECURE (after following this guide)
