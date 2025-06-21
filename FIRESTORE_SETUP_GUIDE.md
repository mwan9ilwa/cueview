# Firestore Security Rules Setup Instructions

## Step 1: Set up Firestore Database

1. Go to **Firebase Console** → **Firestore Database**
2. If you haven't created a database yet:
   - Click **"Create database"**
   - Choose **"Start in test mode"** (we'll set up proper rules below)
   - Select your preferred region

## Step 2: Apply Security Rules

1. Go to **Firestore Database** → **Rules** tab
2. Replace the current rules with:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // Users can only read/write their own user profile
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Users can only read/write their own shows
    match /user_shows/{showId} {
      allow read, write: if request.auth != null && 
        request.auth.uid == resource.data.userId;
      allow create: if request.auth != null && 
        request.auth.uid == request.resource.data.userId;
    }
    
    // Allow users to query their own shows
    match /user_shows/{showId} {
      allow list: if request.auth != null && 
        request.auth.uid == resource.data.userId;
    }
  }
}
```

3. Click **"Publish"**

## Step 3: Initialize Database Collections

The collections (`users` and `user_shows`) will be created automatically when you first add data through the app.

## What these rules do:

- **Authentication Required**: Only authenticated users can access data
- **User Isolation**: Users can only access their own data
- **Profile Security**: Each user can only read/write their own profile in the `users` collection
- **Library Security**: Each user can only access shows in their own library in the `user_shows` collection
- **Query Protection**: Users can only query their own shows

## Next Steps:

After setting up these rules, you can:
1. Sign up/login to your app
2. Add shows to your library
3. View them in the Library tab
4. All data will be stored securely in Firestore
