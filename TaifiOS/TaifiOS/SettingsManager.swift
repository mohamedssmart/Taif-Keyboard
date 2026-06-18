import Foundation

class SettingsManager {
    static let shared = SettingsManager()
    
    // Shared UserDefaults for App Groups
    private let sharedDefaults = UserDefaults(suiteName: "group.com.taif.keyboard") ?? UserDefaults.standard
    
    private init() {}
    
    var isSoundEnabled: Bool {
        get { sharedDefaults.object(forKey: "sound_enabled") as? Bool ?? true }
        set { sharedDefaults.set(newValue, forKey: "sound_enabled") }
    }
    
    var isHapticEnabled: Bool {
        get { sharedDefaults.object(forKey: "haptic_enabled") as? Bool ?? true }
        set { sharedDefaults.set(newValue, forKey: "haptic_enabled") }
    }
    
    var selectedTheme: String {
        get { sharedDefaults.string(forKey: "keyboard_theme") ?? "spectrum" }
        set { sharedDefaults.set(newValue, forKey: "keyboard_theme") }
    }
    
    var selectedLanguage: String {
        get { sharedDefaults.string(forKey: "keyboard_lang") ?? "arabic" }
        set { sharedDefaults.set(newValue, forKey: "keyboard_lang") }
    }
}
