# Firebase Setup Instructions

## Required Files

You need to download configuration files from your Firebase console:

### Android Setup
1. Go to your Firebase Console
2. Select your project
3. Go to Project Settings (gear icon)
4. In the "Your apps" section, click on your Android app
5. Download the `google-services.json` file
6. Place it in: `/composeApp/google-services.json` (same level as build.gradle.kts)

### iOS Setup
1. In the same Firebase Console section
2. Click on your iOS app (or create one if it doesn't exist)
3. Download the `GoogleService-Info.plist` file
4. Place it in: `/iosApp/iosApp/GoogleService-Info.plist`

## App Configuration

### Android Package Name
Make sure your Firebase Android app uses package name: `com.example.cueview`

### iOS Bundle ID
Make sure your Firebase iOS app uses bundle ID: `com.example.cueview`

After adding these files, the real Firebase authentication will work!
