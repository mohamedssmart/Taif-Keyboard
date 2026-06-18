import UIKit

class PreviewManager {
    static let shared = PreviewManager()
    
    private var previewView: UIView?
    private var label: UILabel?
    private var gradientLayer: CAGradientLayer?
    
    private init() {}
    
    func showPreview(character: String, forButton button: UIButton, inParentView parentView: UIView) {
        if previewView == nil {
            let pView = UIView()
            pView.layer.cornerRadius = 12
            pView.layer.masksToBounds = true
            
            let grad = CAGradientLayer()
            grad.colors = [
                UIColor(red: 59.0/255.0, green: 130.0/255.0, blue: 246.0/255.0, alpha: 1).cgColor,
                UIColor(red: 236.0/255.0, green: 72.0/255.0, blue: 153.0/255.0, alpha: 1).cgColor
            ]
            grad.startPoint = CGPoint(x: 0, y: 0)
            grad.endPoint = CGPoint(x: 1, y: 1)
            pView.layer.insertSublayer(grad, at: 0)
            gradientLayer = grad
            
            let lbl = UILabel()
            lbl.textColor = .white
            lbl.font = .systemFont(ofSize: 28, weight: .bold)
            lbl.textAlignment = .center
            lbl.translatesAutoresizingMaskIntoConstraints = false
            pView.addSubview(lbl)
            
            NSLayoutConstraint.activate([
                lbl.centerXAnchor.constraint(equalTo: pView.centerXAnchor),
                lbl.centerYAnchor.constraint(equalTo: pView.centerYAnchor)
            ])
            label = lbl
            previewView = pView
        }
        
        guard let previewView = previewView else { return }
        
        label?.text = character
        
        if previewView.superview == nil {
            parentView.addSubview(previewView)
        }
        
        let buttonFrame = parentView.convert(button.bounds, from: button)
        let previewWidth: CGFloat = 55
        let previewHeight: CGFloat = 65
        let xOffset = buttonFrame.midX - (previewWidth / 2)
        let yOffset = buttonFrame.minY - previewHeight + 8
        
        previewView.frame = CGRect(x: xOffset, y: yOffset, width: previewWidth, height: previewHeight)
        gradientLayer?.frame = previewView.bounds
        
        previewView.isHidden = false
        parentView.bringSubviewToFront(previewView)
    }
    
    func hidePreview() {
        previewView?.isHidden = true
    }
}
