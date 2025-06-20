# CueView - TV Show Tracking App

CueView is a Kotlin Multiplatform app that helps you track your TV show watching progress and stay updated on upcoming episodes using TMDb API and Firebase.

## 🏗️ Architecture

- **Kotlin Multiplatform** with **Compose Multiplatform** for shared UI
- **Clean Architecture** (Data, Domain, Presentation layers)
- **MVVM Pattern** with ViewModels and Compose State
- **Firebase Auth** for authentication
- **Firebase Firestore** for user data storage
- **TMDb API** for TV show data
- **SQLDelight** for local caching
- **Ktor** for networking
- **Koin** for dependency injection

## 🚀 Features

### Core Features
- [x] User Authentication (Email/Password, Google Sign-In)
- [x] TV Show Discovery & Search
- [x] Personal Library Management
- [x] Watch Progress Tracking
- [x] Upcoming Episodes Calendar
- [x] Show Ratings & Notes

### Upcoming Features
- [ ] Push Notifications for new episodes
- [ ] Social features (friends, recommendations)
- [ ] Offline support
- [ ] Calendar integration
- [ ] Statistics and insights

## 🛠️ Setup Instructions

### Prerequisites
- Android Studio or IntelliJ IDEA
- Xcode (for iOS development)
- TMDb API Key
- Firebase Project

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/cueview.git
cd cueview
```

### 2. TMDb API Setup
1. Create an account at [TMDb](https://www.themoviedb.org/)
2. Go to Settings > API and request an API key
3. Replace `YOUR_TMDB_API_KEY_HERE` in `ApiConfig.kt` or use build config

### 3. Firebase Setup

#### Android
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project or use existing one
3. Add Android app with package name `com.example.cueview`
4. Download `google-services.json` and place it in `composeApp/src/androidMain/`
5. Enable Authentication (Email/Password and Google Sign-In)
6. Enable Firestore Database

#### iOS
1. In the same Firebase project, add iOS app
2. Download `GoogleService-Info.plist` and add it to your iOS project
3. Follow Firebase iOS setup instructions

### 4. Build Configuration

Update your `local.properties` file:
```properties
# TMDb API Key
tmdb.api.key=your_actual_api_key_here

# Android SDK
sdk.dir=/path/to/your/android/sdk
```

### 5. Run the App

#### Android
```bash
./gradlew assembleDebug
```

#### iOS
Open `iosApp/iosApp.xcodeproj` in Xcode and run

## 📱 Screenshots

[Add screenshots here once UI is complete]

## 🏃‍♂️ Development Progress

### Phase 1: Foundation ✅
- [x] Project setup with KMP and Compose
- [x] Basic navigation structure
- [x] Authentication screens
- [x] Core data models
- [x] Repository interfaces

### Phase 2: Core Features (In Progress)
- [ ] TMDb API integration
- [ ] Firebase integration
- [ ] Show discovery and search
- [ ] User library management
- [ ] Progress tracking

### Phase 3: Advanced Features (Planned)
- [ ] Episode tracking with calendar
- [ ] Push notifications
- [ ] Offline support
- [ ] Performance optimization

### Phase 4: Polish (Planned)
- [ ] UI/UX improvements
- [ ] Testing
- [ ] App store preparation

## 🏛️ Project Structure

```
composeApp/src/commonMain/kotlin/com/example/cueview/
├── data/
│   ├── local/              # SQLDelight database, DataStore
│   ├── remote/             # TMDb API, Firebase
│   └── repository/         # Repository implementations
├── domain/
│   ├── model/              # Domain models
│   ├── repository/         # Repository interfaces
│   └── usecase/            # Business logic
├── presentation/
│   ├── navigation/         # Navigation setup
│   ├── screens/            # Screen composables
│   │   ├── auth/           # Login/Register
│   │   ├── discover/       # Browse/Search
│   │   ├── library/        # Personal library
│   │   ├── details/        # Show/Episode details
│   │   └── profile/        # User profile
│   └── theme/              # App theming
└── di/                     # Dependency injection
```

## 🔑 Key Technologies

| Category | Technology |
|----------|------------|
| Framework | Kotlin Multiplatform |
| UI | Compose Multiplatform |
| Architecture | Clean Architecture + MVVM |
| Navigation | Compose Navigation |
| Networking | Ktor Client |
| Serialization | Kotlinx Serialization |
| Database | SQLDelight |
| Preferences | DataStore |
| Authentication | Firebase Auth |
| Backend | Firebase Firestore |
| Image Loading | Coil |
| DI | Koin |
| Date/Time | Kotlinx DateTime |

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

If you have any questions or need help setting up the project, please create an issue in the repository.

---

**Happy tracking! 📺✨**
