import SwiftUI

// SwiftUI-specific models
struct UserShow: Identifiable {
    let id: Int
    let name: String
    let posterPath: String?
    let status: WatchStatus
    let currentSeason: Int
    let currentEpisode: Int
}

enum WatchStatus {
    case watching
    case completed
    case planToWatch
    case onHold
    case dropped
    
    var displayName: String {
        switch self {
        case .watching: return "Watching"
        case .completed: return "Completed"
        case .planToWatch: return "Plan to Watch"
        case .onHold: return "On Hold"
        case .dropped: return "Dropped"
        }
    }
    
    var color: Color {
        switch self {
        case .watching: return .blue
        case .completed: return .green
        case .planToWatch: return .orange
        case .onHold: return .yellow
        case .dropped: return .red
        }
    }
}
