import SwiftUI

struct LibraryView: View {
    @StateObject private var viewModel = LibraryViewModel()
    
    var body: some View {
        NavigationView {
            VStack {
                if viewModel.isLoading {
                    ProgressView("Loading your library...")
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                } else if viewModel.userShows.isEmpty {
                    VStack(spacing: 20) {
                        Image(systemName: "books.vertical")
                            .font(.system(size: 60))
                            .foregroundColor(.gray)
                        Text("Your Library is Empty")
                            .font(.title2)
                            .fontWeight(.medium)
                        Text("Add some shows from the Discover tab to get started!")
                            .font(.body)
                            .foregroundColor(.secondary)
                            .multilineTextAlignment(.center)
                    }
                    .padding()
                } else {
                    List(viewModel.userShows) { show in
                        LibraryShowCard(show: show)
                    }
                }
            }
            .navigationTitle("My Library")
            .onAppear {
                viewModel.loadLibrary()
            }
        }
    }
}

struct LibraryShowCard: View {
    let show: UserShow
    
    var body: some View {
        HStack {
            AsyncImage(url: URL(string: "https://image.tmdb.org/t/p/w200\(show.posterPath ?? "")")) { image in
                image
                    .resizable()
                    .aspectRatio(contentMode: .fill)
            } placeholder: {
                Rectangle()
                    .fill(Color.gray.opacity(0.3))
            }
            .frame(width: 60, height: 90)
            .clipShape(RoundedRectangle(cornerRadius: 8))
            
            VStack(alignment: .leading, spacing: 4) {
                Text(show.name)
                    .font(.headline)
                    .lineLimit(2)
                
                Text(show.status.displayName)
                    .font(.caption)
                    .foregroundColor(show.status.color)
                    .padding(.horizontal, 8)
                    .padding(.vertical, 2)
                    .background(show.status.color.opacity(0.2))
                    .clipShape(Capsule())
                
                if show.status == .watching {
                    Text("S\(show.currentSeason)E\(show.currentEpisode)")
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
                
                Spacer()
            }
            
            Spacer()
        }
        .padding(.vertical, 4)
    }
}

@MainActor
class LibraryViewModel: ObservableObject {
    @Published var userShows: [UserShow] = []
    @Published var isLoading = false
    
    func loadLibrary() {
        isLoading = true
        
        // Mock data for demonstration
        DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
            self.userShows = [
                UserShow(
                    id: 1,
                    name: "Breaking Bad",
                    posterPath: "/ggFHVNu6YYI5L9pCfOacjizRGt.jpg",
                    status: .completed,
                    currentSeason: 5,
                    currentEpisode: 16
                ),
                UserShow(
                    id: 2,
                    name: "The Bear",
                    posterPath: "/zNyLn250Rg4H86aAYaJNfFXVFzv.jpg",
                    status: .watching,
                    currentSeason: 3,
                    currentEpisode: 5
                )
            ]
            self.isLoading = false
        }
    }
}
