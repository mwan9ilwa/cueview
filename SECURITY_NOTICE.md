# 🔐 Security Notice

## ⚠️ **CRITICAL SECURITY ISSUE DETECTED**

**Firebase API keys are currently exposed in the source code!**

### What's at risk:
- Firebase project access
- Potential unauthorized database access
- Firestore data exposure
- Authentication bypass

### Immediate action required:
1. **DO NOT** deploy this code to production
2. **DO NOT** share this repository until secured
3. Follow the complete security guide: [FIREBASE_SECURITY_SETUP.md](./FIREBASE_SECURITY_SETUP.md)

### Current security status:
- ❌ **Android**: API keys exposed in `google-services.json`
- ❌ **iOS**: API keys exposed in `GoogleService-Info.plist`
- ❌ **Git**: Sensitive files tracked in version control

### Quick fix:
```bash
# Remove files from git tracking
git rm composeApp/google-services.json
git rm iosApp/iosApp/GoogleService-Info.plist
git commit -m "Remove exposed Firebase config files"

# Use template files instead
cp composeApp/google-services.json.template composeApp/google-services.json
cp iosApp/iosApp/GoogleService-Info.plist.template iosApp/iosApp/GoogleService-Info.plist
```

**Then replace placeholder values with your actual Firebase configuration.**

---

*This notice will be removed once security issues are resolved.*
