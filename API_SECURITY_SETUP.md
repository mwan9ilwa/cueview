# API Keys Security Setup Guide

## Overview

This guide shows how to properly secure API keys for both Firebase and TMDb in your CueView app.

## 🔐 API Keys Used in This Project

1. **Firebase API Keys** (Android & iOS)
2. **TMDb API Key** (The Movie Database)

## ✅ Current Security Status

### Firebase Configuration
- ✅ **Secured**: Config files excluded from git
- ✅ **Template files**: Available for easy setup
- ⚠️ **Setup required**: Need actual config files locally

### TMDb API Key
- ✅ **Secured**: Using build configuration
- ✅ **Cross-platform**: Works on Android & iOS
- ⚠️ **Setup required**: Need to add actual API key

## 🛠️ Setup Instructions

### Step 1: Set up TMDb API Key

1. **Get your TMDb API key:**
   - Go to [TMDb API Settings](https://www.themoviedb.org/settings/api)
   - Create an account if needed
   - Request an API key
   - Copy your API key

2. **Configure the API key:**
   ```bash
   # Copy the template
   cp local.properties.template local.properties
   
   # Edit local.properties and add your actual API key
   echo "TMDB_API_KEY=your_actual_api_key_here" >> local.properties
   ```

### Step 2: Set up Firebase Configuration

1. **Android Firebase setup:**
   ```bash
   # Copy template
   cp composeApp/google-services.json.template composeApp/google-services.json
   
   # Replace with your actual Firebase project values in google-services.json
   ```

2. **iOS Firebase setup:**
   ```bash
   # Copy template  
   cp iosApp/iosApp/GoogleService-Info.plist.template iosApp/iosApp/GoogleService-Info.plist
   
   # Replace with your actual Firebase project values in GoogleService-Info.plist
   ```

### Step 3: Verify Security

After setup, verify that:
- [ ] `local.properties` contains your real TMDb API key
- [ ] `google-services.json` contains your Firebase Android config
- [ ] `GoogleService-Info.plist` contains your Firebase iOS config
- [ ] None of these files are tracked by git

```bash
# Check git status - these files should NOT appear
git status

# These files should be ignored:
# - local.properties  
# - composeApp/google-services.json
# - iosApp/iosApp/GoogleService-Info.plist
```

## 🏗️ How the Security Works

### TMDb API Key Security
- **Android**: Uses `BuildConfig.TMDB_API_KEY` from build configuration
- **iOS**: Uses expect/actual pattern for cross-platform compatibility
- **Source**: API key read from `local.properties` at build time
- **Git**: `local.properties` is excluded from version control

### Firebase Security
- **Configuration files**: Excluded from git via `.gitignore`
- **Template files**: Provide structure without sensitive data
- **Local development**: Real config files exist locally but aren't tracked

## 🔄 Build Process

1. **Build reads** `local.properties` for TMDb API key
2. **Gradle generates** `BuildConfig` with the API key
3. **Kotlin code** accesses API key via platform-specific implementations
4. **Firebase config** files are read directly by Firebase SDKs

## 🚀 Environment Variables (Optional)

For CI/CD or shared environments:

```bash
# Set environment variable
export TMDB_API_KEY="your_api_key_here"

# Build will automatically use environment variable if local.properties doesn't exist
```

## ⚠️ Important Notes

1. **Never commit real API keys** to version control
2. **Use template files** for sharing project structure
3. **Each developer** needs their own `local.properties` with real keys
4. **Production builds** should use secure environment variables
5. **Rotate API keys** periodically for security

## 🧪 Testing the Setup

```bash
# Test Android build
./gradlew assembleDebug

# Test iOS build  
./gradlew compileKotlinIosSimulatorArm64

# Both should build successfully without exposing API keys in logs
```

## 🆘 Troubleshooting

### Build fails with "YOUR_TMDB_API_KEY_HERE"
- Add your real TMDb API key to `local.properties`

### Firebase initialization fails
- Verify your Firebase config files have real project data
- Check that config files exist in the correct locations

### Git shows config files
- Run `git rm --cached <filename>` to stop tracking
- Verify `.gitignore` includes the files

## 📋 File Checklist

After setup, you should have:
- ✅ `local.properties` (with real TMDb API key, not tracked by git)
- ✅ `composeApp/google-services.json` (real Firebase config, not tracked)
- ✅ `iosApp/iosApp/GoogleService-Info.plist` (real Firebase config, not tracked)
- ✅ Template files (tracked by git for easy sharing)
- ✅ `.gitignore` properly excludes sensitive files
