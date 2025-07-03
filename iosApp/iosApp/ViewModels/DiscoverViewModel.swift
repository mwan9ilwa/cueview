import SwiftUI
import Foundation

@MainActor
class DiscoverViewModel: ObservableObject {
    @Published var trendingShows: [Show] = []
    @Published var popularShows: [Show] = []
    @Published var searchResults: [Show] = []
    @Published var isLoading = false
    @Published var errorMessage: String?
    
    private let apiKey = "your_tmdb_api_key" // Replace with actual API key
    private let baseURL = "https://api.themoviedb.org/3"
    
    func loadContent() {
        isLoading = true
        errorMessage = nil
        
        Task {
            await loadTrendingShows()
            await loadPopularShows()
            isLoading = false
        }
    }
    
    private func loadTrendingShows() async {
        do {
            let shows = try await fetchShows(endpoint: "/trending/tv/week")
            self.trendingShows = shows
        } catch {
            self.errorMessage = "Failed to load trending shows: \(error.localizedDescription)"
        }
    }
    
    private func loadPopularShows() async {
        do {
            let shows = try await fetchShows(endpoint: "/tv/popular")
            self.popularShows = shows
        } catch {
            self.errorMessage = "Failed to load popular shows: \(error.localizedDescription)"
        }
    }
    
    func searchShows(query: String) {
        guard !query.isEmpty else {
            searchResults = []
            return
        }
        
        Task {
            do {
                let shows = try await fetchShows(endpoint: "/search/tv", query: query)
                self.searchResults = shows
            } catch {
                self.errorMessage = "Search failed: \(error.localizedDescription)"
            }
        }
    }
    
    func clearError() {
        errorMessage = nil
    }
    
    private func fetchShows(endpoint: String, query: String? = nil) async throws -> [Show] {
        var urlComponents = URLComponents(string: "\(baseURL)\(endpoint)")!
        var queryItems = [URLQueryItem(name: "api_key", value: apiKey)]
        
        if let query = query {
            queryItems.append(URLQueryItem(name: "query", value: query))
        }
        
        urlComponents.queryItems = queryItems
        
        guard let url = urlComponents.url else {
            throw URLError(.badURL)
        }
        
        let (data, _) = try await URLSession.shared.data(from: url)
        let response = try JSONDecoder().decode(TMDbResponse.self, from: data)
        
        return response.results.map { tmdbShow in
            Show(
                id: tmdbShow.id,
                name: tmdbShow.name,
                overview: tmdbShow.overview,
                posterPath: tmdbShow.posterPath,
                voteAverage: tmdbShow.voteAverage
            )
        }
    }
}

// TMDB API Response models
struct TMDbResponse: Codable {
    let results: [TMDbShow]
}

struct TMDbShow: Codable {
    let id: Int
    let name: String
    let overview: String
    let posterPath: String?
    let voteAverage: Double
    
    enum CodingKeys: String, CodingKey {
        case id, name, overview
        case posterPath = "poster_path"
        case voteAverage = "vote_average"
    }
}
