import SwiftUI

// Native SwiftUI data models
struct Show: Identifiable, Codable {
    let id: Int
    let name: String
    let overview: String
    let posterPath: String?
    let voteAverage: Double
}
