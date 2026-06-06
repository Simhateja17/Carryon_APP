import UIKit
import ComposeApp
import StripePaymentSheet

final class StripePresenterImpl: NSObject, StripePresenter {
    static let shared = StripePresenterImpl()
    private override init() { super.init() }

    func present(
        clientSecret: String,
        publishableKey: String,
        onCompleted: @escaping () -> Void,
        onCanceled: @escaping () -> Void,
        onFailed: @escaping () -> Void
    ) {
        StripeAPI.defaultPublishableKey = publishableKey

        var config = PaymentSheet.Configuration()
        config.merchantDisplayName = "CarryOn"
        config.defaultBillingDetails.address.country = "MY"
        config.allowsDelayedPaymentMethods = true

        let sheet = PaymentSheet(paymentIntentClientSecret: clientSecret, configuration: config)

        guard let topVC = UIApplication.shared.topViewController() else {
            onFailed()
            return
        }

        sheet.present(from: topVC) { result in
            switch result {
            case .completed: onCompleted()
            case .canceled:  onCanceled()
            case .failed:    onFailed()
            }
        }
    }
}

private extension UIApplication {
    func topViewController() -> UIViewController? {
        let root = connectedScenes
            .compactMap { $0 as? UIWindowScene }
            .flatMap { $0.windows }
            .first { $0.isKeyWindow }?
            .rootViewController
        return walk(root)
    }

    private func walk(_ vc: UIViewController?) -> UIViewController? {
        if let nav = vc as? UINavigationController { return walk(nav.visibleViewController) }
        if let tab = vc as? UITabBarController    { return walk(tab.selectedViewController) }
        if let presented = vc?.presentedViewController { return walk(presented) }
        return vc
    }
}
