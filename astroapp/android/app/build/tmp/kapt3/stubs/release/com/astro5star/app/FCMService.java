package com.astro5star.app;

/**
 * FCMService - The heart of incoming call handling
 *
 * THIS IS THE MOST CRITICAL COMPONENT FOR MAKING CALLS WORK WHEN APP IS KILLED.
 *
 * HOW IT WORKS:
 * 1. Firebase Cloud Messaging has special system-level permission on Android
 * 2. When a high-priority data message arrives, Android wakes up this service
 * 3. onMessageReceived() is called even if app was killed
 * 4. We explicitly start IncomingCallActivity to show the full-screen call UI
 *
 * KEY REQUIREMENTS:
 * - FCM message must be DATA-ONLY (no 'notification' key in payload)
 * - Message must have priority: 'high' (not 'normal')
 * - This service must be declared in AndroidManifest with MESSAGING_EVENT filter
 *
 * COMMON MISTAKES THAT BREAK THIS:
 * 1. Including 'notification' key in FCM payload - Android handles it differently
 * 2. Using normal priority - message gets batched and delayed
 * 3. Not using explicit Intent with proper flags
 * 4. Forgetting NEW_TASK flag when starting activity from service
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\f\b\u0007\u0018\u0000 \u001d2\u00020\u0001:\u0001\u001dB\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0005\u001a\u00020\u0006H\u0002J\u001c\u0010\u0007\u001a\u00020\u00062\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\n0\tH\u0002J \u0010\u000b\u001a\u00020\u00062\u0006\u0010\f\u001a\u00020\n2\u0006\u0010\r\u001a\u00020\n2\u0006\u0010\u000e\u001a\u00020\nH\u0002J\b\u0010\u000f\u001a\u00020\u0006H\u0016J\u0010\u0010\u0010\u001a\u00020\u00062\u0006\u0010\u0011\u001a\u00020\u0012H\u0016J\u0010\u0010\u0013\u001a\u00020\u00062\u0006\u0010\u0014\u001a\u00020\nH\u0016J(\u0010\u0015\u001a\u00020\u00062\u0006\u0010\u0016\u001a\u00020\n2\u0006\u0010\u0017\u001a\u00020\n2\u0006\u0010\u0018\u001a\u00020\n2\u0006\u0010\u000e\u001a\u00020\nH\u0002J\u0018\u0010\u0019\u001a\u00020\u00062\u0006\u0010\u001a\u001a\u00020\n2\u0006\u0010\u001b\u001a\u00020\nH\u0002J\b\u0010\u001c\u001a\u00020\u0006H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001e"}, d2 = {"Lcom/astro5star/app/FCMService;", "Lcom/google/firebase/messaging/FirebaseMessagingService;", "()V", "serviceScope", "Lkotlinx/coroutines/CoroutineScope;", "createNotificationChannels", "", "handleIncomingCall", "data", "", "", "handleIncomingChat", "callerName", "callerId", "sessionId", "onCreate", "onMessageReceived", "message", "Lcom/google/firebase/messaging/RemoteMessage;", "onNewToken", "token", "showChatMessageNotification", "senderName", "text", "senderId", "showGenericNotification", "title", "body", "wakeUpDevice", "Companion", "app_release"})
public final class FCMService extends com.google.firebase.messaging.FirebaseMessagingService {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "FCMService";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String CALL_CHANNEL_ID = "incoming_calls_v3";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String CALL_CHANNEL_NAME = "Incoming Calls";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String CHAT_CHANNEL_ID = "chat_messages_v1";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String CHAT_CHANNEL_NAME = "Chat Messages";
    private static final int CALL_NOTIFICATION_ID = 9999;
    private static final int GENERIC_NOTIFICATION_ID = 1002;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope serviceScope = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.astro5star.app.FCMService.Companion Companion = null;
    
    public FCMService() {
        super();
    }
    
    @java.lang.Override()
    public void onCreate() {
    }
    
    /**
     * Called when FCM token is refreshed
     *
     * WHEN THIS HAPPENS:
     * - App is installed for the first time
     * - App data is cleared
     * - App is restored on a new device
     * - Firebase SDK decides token needs refresh (security)
     *
     * WE MUST re-register with server because old token is now invalid.
     */
    @java.lang.Override()
    public void onNewToken(@org.jetbrains.annotations.NotNull()
    java.lang.String token) {
    }
    
    /**
     * Called when a message is received from FCM
     *
     * THIS IS WHERE THE MAGIC HAPPENS:
     * - Even if app is killed, this method is called for data-only messages
     * - We parse the call data and start the full-screen incoming call UI
     *
     * IMPORTANT: This runs in a background thread, but we have ~20 seconds
     * to complete our work before Android may kill the service.
     */
    @java.lang.Override()
    public void onMessageReceived(@org.jetbrains.annotations.NotNull()
    com.google.firebase.messaging.RemoteMessage message) {
    }
    
    private final void showGenericNotification(java.lang.String title, java.lang.String body) {
    }
    
    /**
     * Handle incoming call FCM message
     *
     * KEY STEPS:
     * 1. Wake up the device screen (using WakeLock)
     * 2. Show HIGH-PRIORITY notification with full-screen intent
     * 3. The full-screen intent launches IncomingCallActivity
     *
     * WHY WE USE NOTIFICATION WITH FULL-SCREEN INTENT:
     * - On locked devices, Android 10+ REQUIRES a notification with fullScreenIntent
     * - Direct startActivity() may not work reliably on locked screens
     * - The notification with fullScreenIntent IS the official way to do this
     */
    private final void handleIncomingCall(java.util.Map<java.lang.String, java.lang.String> data) {
    }
    
    /**
     * Wake up the device screen
     *
     * WHY THIS IS NEEDED:
     * If phone is in deep sleep with screen off, the activity might not
     * properly turn on the screen on some devices. This ensures the
     * screen turns on so user can see the incoming call.
     *
     * The WakeLock is released after a short timeout - we just need
     * enough time for the activity to start and take over.
     */
    private final void wakeUpDevice() {
    }
    
    /**
     * Create notification channel for incoming calls
     *
     * WHY CHANNELS MATTER (Android 8+):
     * - All notifications must be assigned to a channel
     * - Channels control importance, sound, vibration at OS level
     * - HIGH importance allows heads-up notifications
     * - User can customize each channel independently
     */
    private final void createNotificationChannels() {
    }
    
    private final void handleIncomingChat(java.lang.String callerName, java.lang.String callerId, java.lang.String sessionId) {
    }
    
    private final void showChatMessageNotification(java.lang.String senderName, java.lang.String text, java.lang.String senderId, java.lang.String sessionId) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0005\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0007X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"Lcom/astro5star/app/FCMService$Companion;", "", "()V", "CALL_CHANNEL_ID", "", "CALL_CHANNEL_NAME", "CALL_NOTIFICATION_ID", "", "CHAT_CHANNEL_ID", "CHAT_CHANNEL_NAME", "GENERIC_NOTIFICATION_ID", "TAG", "app_release"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}