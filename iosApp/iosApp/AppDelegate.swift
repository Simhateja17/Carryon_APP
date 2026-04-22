import UIKit
import UserNotifications
import ComposeApp
#if canImport(FirebaseCore)
import FirebaseCore
#endif
#if canImport(FirebaseMessaging)
import FirebaseMessaging
#endif

private let pushTokenDefaultsKey = "push_token"
private let pushTypeDefaultsKey = "push_type"
private let pushBookingDefaultsKey = "push_booking_id"
private let pushTargetScreenDefaultsKey = "push_target_screen"

final class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate {
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        UNUserNotificationCenter.current().delegate = self
        configureFirebaseIfAvailable()
        requestNotificationAuthorization(application: application)

        if let userInfo = launchOptions?[.remoteNotification] as? [AnyHashable: Any] {
            persistPendingPushNavigation(userInfo: userInfo)
        }
        return true
    }

    func application(
        _ application: UIApplication,
        didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data
    ) {
        #if canImport(FirebaseMessaging)
        Messaging.messaging().apnsToken = deviceToken
        #endif
    }

    func application(
        _ application: UIApplication,
        didFailToRegisterForRemoteNotificationsWithError error: Error
    ) {
        print("[Push] Failed to register for remote notifications: \(error)")
    }

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification,
        withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void
    ) {
        completionHandler([.banner, .sound, .list])
    }

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse,
        withCompletionHandler completionHandler: @escaping () -> Void
    ) {
        persistPendingPushNavigation(userInfo: response.notification.request.content.userInfo)
        completionHandler()
    }

    private func requestNotificationAuthorization(application: UIApplication) {
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .badge, .sound]) { granted, error in
            if let error {
                print("[Push] Notification authorization error: \(error)")
            }
            guard granted else { return }
            DispatchQueue.main.async {
                application.registerForRemoteNotifications()
            }
        }
    }

    private func persistPendingPushNavigation(userInfo: [AnyHashable: Any]) {
        let type = (userInfo["type"] as? String)?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
        guard !type.isEmpty else { return }

        let bookingId = (userInfo["bookingId"] as? String)?.trimmingCharacters(in: .whitespacesAndNewlines)
        let targetScreen = (userInfo["targetScreen"] as? String)?.trimmingCharacters(in: .whitespacesAndNewlines)

        UserDefaults.standard.set(type, forKey: pushTypeDefaultsKey)
        UserDefaults.standard.set(bookingId, forKey: pushBookingDefaultsKey)
        UserDefaults.standard.set(targetScreen, forKey: pushTargetScreenDefaultsKey)
        PushNavigationSignal.shared.signalPendingNavigation()
    }

    private func configureFirebaseIfAvailable() {
        #if canImport(FirebaseCore) && canImport(FirebaseMessaging)
        guard let plistPath = Bundle.main.path(forResource: "GoogleService-Info", ofType: "plist"),
              let options = FirebaseOptions(contentsOfFile: plistPath)
        else {
            print("[Push] GoogleService-Info.plist not found; Firebase Messaging disabled on iOS.")
            return
        }

        if FirebaseApp.app() == nil {
            FirebaseApp.configure(options: options)
        }
        Messaging.messaging().delegate = self
        #else
        print("[Push] Firebase iOS SDK not linked; Firebase Messaging disabled on iOS.")
        #endif
    }
}

#if canImport(FirebaseMessaging)
extension AppDelegate: MessagingDelegate {
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        guard let fcmToken, !fcmToken.isEmpty else { return }
        UserDefaults.standard.set(fcmToken, forKey: pushTokenDefaultsKey)
    }
}
#endif
