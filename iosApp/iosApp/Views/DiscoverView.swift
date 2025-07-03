import SwiftUI

struct DiscoverView: View {
    @StateObject private var viewModel = DiscoverViewModel()
    @State private var searchText = ""
    
    var body: some View {
        NavigationView {
            VStack {
                // Search Bar
                SearchBar(text: $searchText, onSearchButtonClicked: {
                    viewModel.searchShows(query: searchText)
                })
                
                // Content
                if viewModel.isLoading {
                    Spacer()
                    ProgressView("Loading shows...")
                        .scaleEffect(1.2)
                    Spacer()
                } else {
                    ScrollView {
                        LazyVStack(spacing: 16) {
                            // Trending Section
                            if !viewModel.trendingShows.isEmpty {
                                SectionView(title: "Trending Now", shows: viewModel.trendingShows)
                            }
                            
                            // Popular Section
                            if !viewModel.popularShows.isEmpty {
                                SectionView(title: "Popular", shows: viewModel.popularShows)
                            }
                            
                            // Search Results
                            if !searchText.isEmpty && !viewModel.searchResults.isEmpty {
                                SectionView(title: "Search Results", shows: viewModel.searchResults)
                            }
                            
                            // Empty state
                            if viewModel.trendingShows.isEmpty && viewModel.popularShows.isEmpty {
                                VStack(spacing: 16) {
                                    Image(systemName: "tv")
                                        .font(.system(size: 60))
                                        .foregroundColor(.gray)
                                    Text("No shows available")
                                        .font(.title2)
                                        .foregroundColor(.secondary)
                                    Text("Try refreshing or check your connection")
                                        .font(.body)
                                        .foregroundColor(.secondary)
                                        .multilineTextAlignment(.center)
                                }
                                .padding()
                            }
                        }
                        .padding()
                    }
                }
            }
            .navigationTitle("Discover")
            .onAppear {
                viewModel.loadContent()
            }
            .alert("Error", isPresented: .constant(viewModel.errorMessage != nil)) {
                Button("OK") {
                    viewModel.clearError()
                }
            } message: {
                Text(viewModel.errorMessage ?? "")
            }
        }
    }
}

struct SectionView: View {
    let title: String
    let shows: [Show]
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack {
                Text(title)
                    .font(.title2)
                    .fontWeight(.bold)
                Spacer()
            }
            
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: 12) {
                    ForEach(shows) { show in
                        ShowCardView(show: show)
                    }
                }
                .padding(.horizontal)
            }
        }
    }
}

struct ShowCardView: View {
    let show: Show
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            AsyncImage(url: URL(string: "https://image.tmdb.org/t/p/w300\(show.posterPath ?? "")")) { image in
                image
                    .resizable()
                    .aspectRatio(contentMode: .fill)
            } placeholder: {
                Rectangle()
                    .fill(Color.gray.opacity(0.3))
            }
            .frame(width: 120, height: 180)
            .clipShape(RoundedRectangle(cornerRadius: 8))
            
            Text(show.name)
                .font(.caption)
                .fontWeight(.medium)
                .lineLimit(2)
                .frame(width: 120, alignment: .leading)
            
            HStack {
                Image(systemName: "star.fill")
                    .foregroundColor(.yellow)
                    .font(.caption2)
                Text(String(format: "%.1f", show.voteAverage))
                    .font(.caption2)
                    .foregroundColor(.secondary)
            }
        }
    }
}

struct SearchBar: View {
    @Binding var text: String
    var onSearchButtonClicked: () -> Void
    
    var body: some View {
        HStack {
            Image(systemName: "magnifyingglass")
                .foregroundColor(.secondary)
            
            TextField("Search TV shows...", text: $text)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .onSubmit {
                    onSearchButtonClicked()
                }
            
            if !text.isEmpty {
                Button("Clear") {
                    text = ""
                }
                .foregroundColor(.blue)
            }
        }
        .padding(.horizontal)
    }
}
