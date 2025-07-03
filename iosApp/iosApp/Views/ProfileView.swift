import SwiftUI

struct ProfileView: View {
    @State private var isSignedIn = false
    @State private var userName = "Demo User"
    
    var body: some View {
        NavigationView {
            VStack(spacing: 20) {
                // Profile Image
                Circle()
                    .fill(Color.blue.opacity(0.3))
                    .frame(width: 100, height: 100)
                    .overlay(
                        Image(systemName: "person.fill")
                            .font(.system(size: 50))
                            .foregroundColor(.blue)
                    )
                
                // User Name
                Text(userName)
                    .font(.title2)
                    .fontWeight(.medium)
                
                // Stats
                HStack(spacing: 40) {
                    VStack {
                        Text("12")
                            .font(.title2)
                            .fontWeight(.bold)
                        Text("Shows")
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                    
                    VStack {
                        Text("256")
                            .font(.title2)
                            .fontWeight(.bold)
                        Text("Episodes")
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                    
                    VStack {
                        Text("48h")
                            .font(.title2)
                            .fontWeight(.bold)
                        Text("Watched")
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                }
                .padding()
                .background(Color.secondary.opacity(0.1))
                .cornerRadius(12)
                
                Spacer()
                
                // Settings/Actions
                VStack(spacing: 12) {
                    Button("Settings") {
                        // Handle settings
                    }
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color.blue)
                    .foregroundColor(.white)
                    .cornerRadius(8)
                    
                    if isSignedIn {
                        Button("Sign Out") {
                            isSignedIn = false
                        }
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.red)
                        .foregroundColor(.white)
                        .cornerRadius(8)
                    } else {
                        Button("Sign In") {
                            isSignedIn = true
                        }
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.green)
                        .foregroundColor(.white)
                        .cornerRadius(8)
                    }
                }
                .padding(.horizontal)
                
                Spacer()
            }
            .padding()
            .navigationTitle("Profile")
        }
    }
}
