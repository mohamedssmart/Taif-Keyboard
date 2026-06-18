import SwiftUI

struct ContentView: View {
    @State private var isSoundEnabled = SettingsManager.shared.isSoundEnabled
    @State private var isHapticEnabled = SettingsManager.shared.isHapticEnabled
    @State private var selectedTheme = SettingsManager.shared.selectedTheme
    @State private var testText = ""
    
    let themes = ["spectrum", "dark", "light"]
    
    var body: some View {
        ZStack {
            // Background
            Color(red: 15/255, green: 23/255, blue: 42/255)
                .ignoresSafeArea()
            
            ScrollView {
                VStack(spacing: 24) {
                    Spacer().frame(height: 20)
                    
                    // Logo UI matching "ط"
                    ZStack {
                        RoundedRectangle(cornerRadius: 24)
                            .fill(Color.white)
                            .frame(width: 100, height: 100)
                            .shadow(radius: 5)
                        
                        Circle()
                            .fill(LinearGradient(colors: [.cyan, .blue, .purple], startPoint: .topLeading, endPoint: .bottomTrailing))
                            .frame(width: 24, height: 24)
                            .offset(x: 22, y: -22)
                        
                        Text("ط")
                            .font(.system(size: 58, weight: .bold))
                            .foregroundColor(Color(red: 30/255, green: 41/255, blue: 59/255))
                    }
                    
                    VStack(spacing: 6) {
                        Text("طيف | Taif")
                            .font(.system(size: 28, weight: .bold))
                            .foregroundColor(.white)
                        
                        Text("لوحة المفاتيح الذكية والجمالية الأولى")
                            .font(.system(size: 14))
                            .foregroundColor(Color(red: 148/255, green: 163/255, blue: 184/255))
                            .multilineTextAlignment(.center)
                    }
                    
                    // Onboarding Steps
                    VStack(spacing: 12) {
                        OnboardingStepRow(
                            step: 1,
                            title: "تفعيل لوحة المفاتيح",
                            desc: "اذهب إلى الإعدادات > عام > لوحة المفاتيح وقم بإضافة 'طيف'"
                        ) {
                            if let url = URL(string: UIApplication.openSettingsURLString) {
                                UIApplication.shared.open(url)
                            }
                        }
                        
                        OnboardingStepRow(
                            step: 2,
                            title: "السماح بالوصول الكامل",
                            desc: "مطلوب لتفعيل الأصوات، الاهتزازات والثيمات الملونة"
                        ) {
                            if let url = URL(string: UIApplication.openSettingsURLString) {
                                UIApplication.shared.open(url)
                            }
                        }
                    }
                    
                    // Settings Header
                    VStack(alignment: .leading, spacing: 12) {
                        Text("الإعدادات والمظهر (Settings)")
                            .font(.system(size: 18, weight: .bold))
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity, alignment: .leading)
                        
                        // Theme Selection
                        VStack(alignment: .leading, spacing: 8) {
                            Text("اختر المظهر (Select Theme)")
                                .font(.system(size: 14))
                                .foregroundColor(Color(red: 148/255, green: 163/255, blue: 184/255))
                            
                            HStack(spacing: 10) {
                                ForEach(themes, id: \.self) { theme in
                                    ThemeCard(themeName: theme, isSelected: selectedTheme == theme) {
                                        selectedTheme = theme
                                        SettingsManager.shared.selectedTheme = theme
                                    }
                                }
                            }
                        }
                        
                        // Toggles
                        VStack(spacing: 0) {
                            Toggle("صوت المفاتيح عند النقر", isOn: $isSoundEnabled)
                                .onChange(of: isSoundEnabled) { value in
                                    SettingsManager.shared.isSoundEnabled = value
                                }
                                .padding()
                                .foregroundColor(.white)
                            
                            Divider().background(Color(red: 51/255, green: 65/255, blue: 85/255))
                            
                            Toggle("اهتزاز المفاتيح عند النقر", isOn: $isHapticEnabled)
                                .onChange(of: isHapticEnabled) { value in
                                    SettingsManager.shared.isHapticEnabled = value
                                }
                                .padding()
                                .foregroundColor(.white)
                        }
                        .background(Color(red: 30/255, green: 41/255, blue: 59/255))
                        .cornerRadius(16)
                    }
                    
                    // Textfield to test keyboard
                    TextField("جرب لوحة المفاتيح واكتب هنا...", text: $testText)
                        .padding()
                        .background(Color(red: 30/255, green: 41/255, blue: 59/255))
                        .cornerRadius(12)
                        .foregroundColor(.white)
                        .overlay(
                            RoundedRectangle(cornerRadius: 12)
                                .stroke(Color(red: 51/255, green: 65/255, blue: 85/255), lineWidth: 1.5)
                        )
                    
                    Spacer().frame(height: 40)
                }
                .padding(24)
            }
        }
    }
}

struct OnboardingStepRow: View {
    let step: Int
    let title: String
    let desc: String
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            HStack(spacing: 16) {
                ZStack {
                    Circle()
                        .fill(Color(red: 59/255, green: 130/255, blue: 246/255))
                        .frame(width: 40, height: 40)
                    
                    Text("\(step)")
                        .font(.system(size: 16, weight: .bold))
                        .foregroundColor(.white)
                }
                
                VStack(alignment: .leading, spacing: 4) {
                    Text(title)
                        .font(.system(size: 16, weight: .bold))
                        .foregroundColor(.white)
                    Text(desc)
                        .font(.system(size: 13))
                        .foregroundColor(Color(red: 148/255, green: 163/255, blue: 184/255))
                        .multilineTextAlignment(.leading)
                }
                
                Spacer()
                
                Image(systemName: "chevron.right")
                    .foregroundColor(.gray)
            }
            .padding()
            .background(Color(red: 30/255, green: 41/255, blue: 59/255))
            .cornerRadius(16)
            .overlay(
                RoundedRectangle(cornerRadius: 16)
                    .stroke(Color(red: 51/255, green: 65/255, blue: 85/255), lineWidth: 1.5)
            )
        }
    }
}

struct ThemeCard: View {
    let themeName: String
    let isSelected: Bool
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            VStack {
                if themeName == "spectrum" {
                    RoundedRectangle(cornerRadius: 12)
                        .fill(LinearGradient(colors: [.blue, .purple, .pink], startPoint: .topLeading, endPoint: .bottomTrailing))
                        .frame(height: 60)
                } else if themeName == "dark" {
                    RoundedRectangle(cornerRadius: 12)
                        .fill(Color(red: 28/255, green: 28/255, blue: 34/255))
                        .frame(height: 60)
                } else {
                    RoundedRectangle(cornerRadius: 12)
                        .fill(Color(red: 244/255, green: 244/255, blue: 240/255))
                        .frame(height: 60)
                }
                
                Text(themeName.capitalized)
                    .font(.system(size: 12, weight: .bold))
                    .foregroundColor(.white)
            }
            .padding(8)
            .background(Color(red: 30/255, green: 41/255, blue: 59/255))
            .cornerRadius(16)
            .overlay(
                RoundedRectangle(cornerRadius: 16)
                    .stroke(isSelected ? Color.blue : Color.clear, lineWidth: 2)
            )
        }
    }
}
