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
            Color(red: 15.0/255.0, green: 23.0/255.0, blue: 42.0/255.0)
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
                            .fill(LinearGradient(gradient: Gradient(colors: [.cyan, .blue, .purple]), startPoint: .topLeading, endPoint: .bottomTrailing))
                            .frame(width: 24, height: 24)
                            .offset(x: 22, y: -22)
                        
                        Text("ط")
                            .font(.system(size: 58, weight: .bold))
                            .foregroundColor(Color(red: 30.0/255.0, green: 41.0/255.0, blue: 59.0/255.0))
                    }
                    
                    VStack(spacing: 6) {
                        Text("طيف | Taif")
                            .font(.system(size: 28, weight: .bold))
                            .foregroundColor(.white)
                        
                        Text("لوحة المفاتيح الذكية والجمالية الأولى")
                            .font(.system(size: 14))
                            .foregroundColor(Color(red: 148.0/255.0, green: 163.0/255.0, blue: 184.0/255.0))
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
                                .foregroundColor(Color(red: 148.0/255.0, green: 163.0/255.0, blue: 184.0/255.0))
                            
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
                            Toggle("صوت المفاتيح عند النقر", isOn: Binding(
                                get: { self.isSoundEnabled },
                                set: { newValue in
                                    self.isSoundEnabled = newValue
                                    SettingsManager.shared.isSoundEnabled = newValue
                                }
                            ))
                            .padding()
                            .foregroundColor(.white)
                            
                            Divider().background(Color(red: 51.0/255.0, green: 65.0/255.0, blue: 85.0/255.0))
                            
                            Toggle("اهتزاز المفاتيح عند النقر", isOn: Binding(
                                get: { self.isHapticEnabled },
                                set: { newValue in
                                    self.isHapticEnabled = newValue
                                    SettingsManager.shared.isHapticEnabled = newValue
                                }
                            ))
                            .padding()
                            .foregroundColor(.white)
                        }
                        .background(Color(red: 30.0/255.0, green: 41.0/255.0, blue: 59.0/255.0))
                        .cornerRadius(16)
                    }
                    
                    // Textfield to test keyboard
                    TextField("جرب لوحة المفاتيح واكتب هنا...", text: $testText)
                        .padding()
                        .background(Color(red: 30.0/255.0, green: 41.0/255.0, blue: 59.0/255.0))
                        .cornerRadius(12)
                        .foregroundColor(.white)
                        .overlay(
                            RoundedRectangle(cornerRadius: 12)
                                .stroke(Color(red: 51.0/255.0, green: 65.0/255.0, blue: 85.0/255.0), lineWidth: 1.5)
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
                        .fill(Color(red: 59.0/255.0, green: 130.0/255.0, blue: 246.0/255.0))
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
                        .foregroundColor(Color(red: 148.0/255.0, green: 163.0/255.0, blue: 184.0/255.0))
                        .multilineTextAlignment(.leading)
                }
                
                Spacer()
                
                Image(systemName: "chevron.right")
                    .foregroundColor(.gray)
            }
            .padding()
            .background(Color(red: 30.0/255.0, green: 41.0/255.0, blue: 59.0/255.0))
            .cornerRadius(16)
            .overlay(
                RoundedRectangle(cornerRadius: 16)
                    .stroke(Color(red: 51.0/255.0, green: 65.0/255.0, blue: 85.0/255.0), lineWidth: 1.5)
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
                        .fill(LinearGradient(gradient: Gradient(colors: [.blue, .purple, .pink]), startPoint: .topLeading, endPoint: .bottomTrailing))
                        .frame(height: 60)
                } else if themeName == "dark" {
                    RoundedRectangle(cornerRadius: 12)
                        .fill(Color(red: 28.0/255.0, green: 28.0/255.0, blue: 34.0/255.0))
                        .frame(height: 60)
                } else {
                    RoundedRectangle(cornerRadius: 12)
                        .fill(Color(red: 244.0/255.0, green: 244.0/255.0, blue: 240.0/255.0))
                        .frame(height: 60)
                }
                
                Text(themeName.capitalized)
                    .font(.system(size: 12, weight: .bold))
                    .foregroundColor(.white)
            }
            .padding(8)
            .background(Color(red: 30.0/255.0, green: 41.0/255.0, blue: 59.0/255.0))
            .cornerRadius(16)
            .overlay(
                RoundedRectangle(cornerRadius: 16)
                    .stroke(isSelected ? Color.blue : Color.clear, lineWidth: 2)
            )
        }
    }
}
