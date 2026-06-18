import UIKit

class KeyboardViewController: UIInputViewController {
    
    var keyboardView: UIView!
    var settings = SettingsManager.shared
    
    // Layout states
    var isArabic = true
    var isShifted = false
    var isSymbols = false
    
    // UI References
    var enterButton: UIButton?
    
    // Backspace continuous delete timer
    private var deleteTimer: Timer?
    private var deleteCount = 0
    
    // Sensitive Fields Flag
    private var isSecureField = false
    
    // Layout characters definitions
    let arabicRow1 = ["ض", "ص", "ث", "ق", "ف", "غ", "ع", "ه", "خ", "ح", "ج"]
    let arabicRow2 = ["ش", "س", "ي", "ب", "ل", "ا", "ت", "ن", "م", "ك", "ط"]
    let arabicRow3 = ["ئ", "ء", "ؤ", "ر", "لا", "ى", "ة", "و", "ز", "ظ", "د", "ذ"]
    
    let englishRow1 = ["q", "w", "e", "r", "t", "y", "u", "i", "o", "p"]
    let englishRow2 = ["a", "s", "d", "f", "g", "h", "j", "k", "l"]
    let englishRow3 = ["z", "x", "c", "v", "b", "n", "m"]
    
    override func updateViewConstraints() {
        super.updateViewConstraints()
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupKeyboard()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        updateAutoShiftState()
        updateEnterKeyTitle()
        applyTheme()
    }
    
    override func textDidChange(_ textInput: UITextInput?) {
        super.textDidChange(textInput)
        
        // Check secure input fields (Sensitive Fields Management)
        if let traits = textInput as? UITextInputTraits {
            isSecureField = traits.isSecureTextEntry == .yes
        } else {
            isSecureField = false
        }
        
        updateAutoShiftState()
        updateEnterKeyTitle()
    }
    
    func setupKeyboard() {
        keyboardView = UIView()
        keyboardView.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(keyboardView)
        
        NSLayoutConstraint.activate([
            keyboardView.leftAnchor.constraint(equalTo: view.leftAnchor),
            keyboardView.rightAnchor.constraint(equalTo: view.rightAnchor),
            keyboardView.topAnchor.constraint(equalTo: view.topAnchor),
            keyboardView.bottomAnchor.constraint(equalTo: view.bottomAnchor)
        ])
        
        buildKeyboardLayout()
    }
    
    func buildKeyboardLayout() {
        for subview in keyboardView.subviews {
            subview.removeFromSuperview()
        }
        
        let mainStack = UIStackView()
        mainStack.axis = .vertical
        mainStack.distribution = .fillEqually
        mainStack.spacing = 8
        mainStack.translatesAutoresizingMaskIntoConstraints = false
        keyboardView.addSubview(mainStack)
        
        NSLayoutConstraint.activate([
            mainStack.leftAnchor.constraint(equalTo: keyboardView.leftAnchor, constant: 6),
            mainStack.rightAnchor.constraint(equalTo: keyboardView.rightAnchor, constant: -6),
            mainStack.topAnchor.constraint(equalTo: keyboardView.topAnchor, constant: 8),
            mainStack.bottomAnchor.constraint(equalTo: keyboardView.bottomAnchor, constant: -8)
        ])
        
        // 1. Build character rows
        let row1Keys = isArabic ? arabicRow1 : (isShifted ? englishRow1.map { $0.uppercased() } : englishRow1)
        let row2Keys = isArabic ? arabicRow2 : (isShifted ? englishRow2.map { $0.uppercased() } : englishRow2)
        let row3Keys = isArabic ? arabicRow3 : (isShifted ? englishRow3.map { $0.uppercased() } : englishRow3)
        
        mainStack.addArrangedSubview(createRowStack(keys: row1Keys))
        mainStack.addArrangedSubview(createRowStack(keys: row2Keys))
        
        // Row 3 with Shift and Backspace
        let row3Stack = UIStackView()
        row3Stack.axis = .horizontal
        row3Stack.distribution = .fillProportionally
        row3Stack.spacing = 6
        
        let shiftBtn = createButton(title: "⇧", isSpecial: true)
        shiftBtn.addTarget(self, action: #selector(shiftPressed), for: .touchUpInside)
        row3Stack.addArrangedSubview(shiftBtn)
        
        for key in row3Keys {
            let keyBtn = createButton(title: key, isSpecial: false)
            keyBtn.addTarget(self, action: #selector(keyPressed(_:)), for: .touchUpInside)
            row3Stack.addArrangedSubview(keyBtn)
        }
        
        let backspaceBtn = createButton(title: "⌫", isSpecial: true)
        backspaceBtn.addTarget(self, action: #selector(backspacePressed), for: .touchUpInside)
        
        // Add gesture recognizer for accelerated deletion
        let longPress = UILongPressGestureRecognizer(target: self, action: #selector(handleBackspaceLongPress(_:)))
        longPress.minimumPressDuration = 0.4
        backspaceBtn.addGestureRecognizer(longPress)
        
        row3Stack.addArrangedSubview(backspaceBtn)
        
        mainStack.addArrangedSubview(row3Stack)
        
        // 2. Bottom Actions Row
        let bottomStack = UIStackView()
        bottomStack.axis = .horizontal
        bottomStack.distribution = .fillProportionally
        bottomStack.spacing = 6
        
        let globeBtn = createButton(title: "🌐", isSpecial: true)
        globeBtn.addTarget(self, action: #selector(toggleLanguage), for: .touchUpInside)
        bottomStack.addArrangedSubview(globeBtn)
        
        let spaceTitle = isArabic ? "طيف" : "Space"
        let spaceBtn = createButton(title: spaceTitle, isSpecial: false)
        spaceBtn.addTarget(self, action: #selector(spacePressed), for: .touchUpInside)
        bottomStack.addArrangedSubview(spaceBtn)
        
        // Setup Enter button
        let enterBtn = createButton(title: getEnterKeyTitle(), isSpecial: true)
        enterBtn.addTarget(self, action: #selector(enterPressed), for: .touchUpInside)
        bottomStack.addArrangedSubview(enterBtn)
        enterButton = enterBtn
        
        mainStack.addArrangedSubview(bottomStack)
        
        applyTheme()
    }
    
    func createRowStack(keys: [String]) -> UIStackView {
        let stack = UIStackView()
        stack.axis = .horizontal
        stack.distribution = .fillEqually
        stack.spacing = 6
        
        for key in keys {
            let btn = createButton(title: key, isSpecial: false)
            btn.addTarget(self, action: #selector(keyPressed(_:)), for: .touchUpInside)
            stack.addArrangedSubview(btn)
        }
        return stack
    }
    
    func createButton(title: String, isSpecial: Bool) -> UIButton {
        let btn = UIButton(type: .system)
        btn.setTitle(title, for: .normal)
        btn.titleLabel?.font = .systemFont(ofSize: 19, weight: .semibold)
        btn.layer.cornerRadius = 6
        btn.layer.masksToBounds = true
        
        // Add Floating Key Press Previews targets for character buttons
        if !isSpecial && title.count == 1 {
            btn.addTarget(self, action: #selector(keyTouchDown(_:)), for: .touchDown)
            btn.addTarget(self, action: #selector(keyTouchUp(_:)), for: [.touchUpInside, .touchUpOutside, .touchCancel])
        }
        
        return btn
    }
    
    func getEnterKeyTitle() -> String {
        let isArabic = self.isArabic
        let returnType = textDocumentProxy.returnKeyType
        
        switch returnType {
        case .go:
            return isArabic ? "ذهاب" : "Go"
        case .search:
            return isArabic ? "بحث" : "Search"
        case .send:
            return isArabic ? "إرسال" : "Send"
        case .next:
            return isArabic ? "التالي" : "Next"
        case .done:
            return isArabic ? "تم" : "Done"
        default:
            return "⏎"
        }
    }
    
    func updateEnterKeyTitle() {
        let title = getEnterKeyTitle()
        enterButton?.setTitle(title, for: .normal)
        applyTheme() // Re-apply theme styling for updated action label
    }
    
    func updateAutoShiftState() {
        guard !isArabic else { return }
        
        let autoCapType = textDocumentProxy.autocapitalizationType ?? .sentences
        if autoCapType == .none {
            isShifted = false
            return
        }
        
        if autoCapType == .allCharacters {
            isShifted = true
            return
        }
        
        let context = textDocumentProxy.documentContextBeforeInput ?? ""
        if context.isEmpty {
            isShifted = true
        } else {
            if autoCapType == .sentences {
                let trimmed = context.trimmingCharacters(in: .whitespacesAndNewlines)
                if trimmed.isEmpty || trimmed.hasSuffix(".") || trimmed.hasSuffix("!") || trimmed.hasSuffix("?") {
                    isShifted = true
                } else {
                    isShifted = false
                }
            } else if autoCapType == .words {
                isShifted = context.hasSuffix(" ") || context.hasSuffix("\n")
            }
        }
        buildKeyboardLayout()
    }
    
    func applyTheme() {
        let theme = settings.selectedTheme
        let isLight = theme == "light"
        
        // Background colors
        if theme == "spectrum" {
            let gradient = CAGradientLayer()
            gradient.frame = view.bounds
            gradient.colors = [
                UIColor(red: 29.0/255.0, green: 78.0/255.0, blue: 216.0/255.0, alpha: 1).cgColor,
                UIColor(red: 236.0/255.0, green: 72.0/255.0, blue: 153.0/255.0, alpha: 1).cgColor
            ]
            gradient.startPoint = CGPoint(x: 0, y: 0)
            gradient.endPoint = CGPoint(x: 1, y: 1)
            
            view.layer.sublayers?.filter { $0 is CAGradientLayer }.forEach { $0.removeFromSuperlayer() }
            view.layer.insertSublayer(gradient, at: 0)
        } else {
            view.layer.sublayers?.filter { $0 is CAGradientLayer }.forEach { $0.removeFromSuperlayer() }
            view.backgroundColor = isLight ? UIColor(red: 244.0/255.0, green: 244.0/255.0, blue: 240.0/255.0, alpha: 1) : UIColor(red: 28.0/255.0, green: 28.0/255.0, blue: 34.0/255.0, alpha: 1)
        }
        
        // Stylize all buttons
        guard let mainStack = keyboardView.subviews.first as? UIStackView else { return }
        for rowStack in mainStack.arrangedSubviews {
            guard let stack = rowStack as? UIStackView else { continue }
            for btnView in stack.arrangedSubviews {
                guard let btn = btnView as? UIButton else { continue }
                
                let title = btn.currentTitle ?? ""
                let isSpaceOrEnter = title == "Space" || title == "طيف" || title == "⏎" ||
                                     title == "Go" || title == "Search" || title == "Send" || title == "Next" || title == "Done" ||
                                     title == "ذهاب" || title == "بحث" || title == "إرسال" || title == "التالي" || title == "تم"
                
                if isSpaceOrEnter {
                    btn.backgroundColor = UIColor(red: 236.0/255.0, green: 72.0/255.0, blue: 153.0/255.0, alpha: 1) // Fallback pink
                    btn.setTitleColor(.white, for: .normal)
                } else {
                    btn.backgroundColor = isLight ? .white : UIColor(red: 45.0/255.0, green: 45.0/255.0, blue: 53.0/255.0, alpha: 1)
                    btn.setTitleColor(isLight ? .black : .white, for: .normal)
                }
            }
        }
    }
    
    // Actions
    @objc func keyPressed(_ sender: UIButton) {
        playFeedback()
        if let title = sender.currentTitle {
            textDocumentProxy.insertText(title)
        }
        // Capitalization updates on character press
        updateAutoShiftState()
    }
    
    @objc func spacePressed() {
        playFeedback()
        textDocumentProxy.insertText(" ")
        updateAutoShiftState()
    }
    
    @objc func backspacePressed() {
        playFeedback()
        textDocumentProxy.deleteBackward()
        updateAutoShiftState()
    }
    
    @objc func handleBackspaceLongPress(_ gesture: UILongPressGestureRecognizer) {
        if gesture.state == .began {
            deleteCount = 0
            deleteTimer = Timer.scheduledTimer(withTimeInterval: 0.08, repeats: true) { [weak self] _ in
                guard let self = self else { return }
                self.playFeedback()
                self.textDocumentProxy.deleteBackward()
                self.deleteCount += 1
                self.updateAutoShiftState()
            }
            RunLoop.current.add(deleteTimer!, forMode: .common)
        } else if gesture.state == .ended || gesture.state == .cancelled {
            deleteTimer?.invalidate()
            deleteTimer = nil
        }
    }
    
    @objc func shiftPressed() {
        playFeedback()
        isShifted = !isShifted
        buildKeyboardLayout()
    }
    
    @objc func enterPressed() {
        playFeedback()
        textDocumentProxy.insertText("\n")
        updateAutoShiftState()
    }
    
    @objc func toggleLanguage() {
        playFeedback()
        isArabic = !isArabic
        buildKeyboardLayout()
    }
    
    @objc func keyTouchDown(_ sender: UIButton) {
        guard let title = sender.currentTitle else { return }
        PreviewManager.shared.showPreview(character: title, forButton: sender, inParentView: self.view)
    }
    
    @objc func keyTouchUp(_ sender: UIButton) {
        PreviewManager.shared.hidePreview()
    }
    
    func playFeedback() {
        if settings.isSoundEnabled {
            UIDevice.current.playInputClick()
        }
        if settings.isHapticEnabled {
            let feedback = UIImpactFeedbackGenerator(style: .light)
            feedback.impactOccurred()
        }
    }
}

