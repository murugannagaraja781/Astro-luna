package com.astro5star.app.data.remote;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000b\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u000e\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\u000b\u001a\u00020\fJ&\u0010\r\u001a\u00020\f2\u0006\u0010\u000e\u001a\u00020\u00052\u0016\b\u0002\u0010\u000f\u001a\u0010\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\f\u0018\u00010\u0010J\b\u0010\u0011\u001a\u0004\u0018\u00010\u0007J<\u0010\u0012\u001a\u00020\f2\u0006\u0010\u0013\u001a\u00020\u00052\u0006\u0010\u0014\u001a\u00020\u00052\n\b\u0002\u0010\u0015\u001a\u0004\u0018\u00010\u00162\u0018\b\u0002\u0010\u000f\u001a\u0012\u0012\u0006\u0012\u0004\u0018\u00010\u0016\u0012\u0004\u0012\u00020\f\u0018\u00010\u0010J\u001a\u0010\u0017\u001a\u00020\f2\u0012\u0010\u0018\u001a\u000e\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\f0\u0010J\u001a\u0010\u0019\u001a\u00020\f2\u0012\u0010\u0018\u001a\u000e\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\f0\u0010J\u000e\u0010\u001a\u001a\u00020\f2\u0006\u0010\u001b\u001a\u00020\u0016J\u001a\u0010\u001c\u001a\u00020\f2\u0012\u0010\u0018\u001a\u000e\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\f0\u0010J\u0010\u0010\u001d\u001a\u00020\f2\b\u0010\u001e\u001a\u0004\u0018\u00010\u0005J\u0014\u0010\u001f\u001a\u00020\f2\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\f0 Jh\u0010!\u001a\u00020\f2`\u0010\u0018\u001a\\\u0012\u0013\u0012\u00110\u0005\u00a2\u0006\f\b#\u0012\b\b$\u0012\u0004\b\b(%\u0012\u0013\u0012\u00110&\u00a2\u0006\f\b#\u0012\b\b$\u0012\u0004\b\b(\'\u0012\u0013\u0012\u00110&\u00a2\u0006\f\b#\u0012\b\b$\u0012\u0004\b\b((\u0012\u0013\u0012\u00110)\u00a2\u0006\f\b#\u0012\b\b$\u0012\u0004\b\b(*\u0012\u0004\u0012\u00020\f0\"J)\u0010+\u001a\u00020\f2!\u0010\u0018\u001a\u001d\u0012\u0013\u0012\u00110,\u00a2\u0006\f\b#\u0012\b\b$\u0012\u0004\b\b(-\u0012\u0004\u0012\u00020\f0\u0010J\u001a\u0010.\u001a\u00020\f2\u0012\u0010\u0018\u001a\u000e\u0012\u0004\u0012\u00020&\u0012\u0004\u0012\u00020\f0\u0010J\u000e\u0010/\u001a\u00020\f2\u0006\u00100\u001a\u00020\u0005J\u0014\u00101\u001a\u00020\f2\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\f0 J\u001e\u00102\u001a\u00020\f2\u0006\u0010\u000e\u001a\u00020\u00052\u0006\u00103\u001a\u00020\u00052\u0006\u00104\u001a\u00020\tJ\u001a\u00105\u001a\u00020\f2\u0012\u0010\u0018\u001a\u000e\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\f0\u0010J\u001a\u00106\u001a\u00020\f2\u0012\u0010\u0018\u001a\u000e\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\f0\u0010J\u0006\u00107\u001a\u00020\fJ\u0006\u00108\u001a\u00020\fJ\u0006\u00109\u001a\u00020\fR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006:"}, d2 = {"Lcom/astro5star/app/data/remote/SocketManager;", "", "<init>", "()V", "TAG", "", "socket", "Lio/socket/client/Socket;", "initialized", "", "currentUserId", "init", "", "registerUser", "userId", "callback", "Lkotlin/Function1;", "getSocket", "requestSession", "toUserId", "type", "birthData", "Lorg/json/JSONObject;", "onSessionAnswered", "listener", "onSignal", "emitSignal", "data", "onMessageStatus", "endSession", "sessionId", "onSessionEnded", "Lkotlin/Function0;", "onSessionEndedWithSummary", "Lkotlin/Function4;", "Lkotlin/ParameterName;", "name", "reason", "", "deducted", "earned", "", "duration", "onBillingStarted", "", "startTime", "onWalletUpdate", "off", "event", "onConnect", "updateServiceStatus", "service", "isEnabled", "onAstrologerUpdate", "onIncomingSession", "offIncomingSession", "disconnect", "removeChatListeners", "app_release"})
public final class SocketManager {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "SocketManager";
    @org.jetbrains.annotations.Nullable()
    private static io.socket.client.Socket socket;
    private static boolean initialized = false;
    @org.jetbrains.annotations.Nullable()
    private static java.lang.String currentUserId;
    @org.jetbrains.annotations.NotNull()
    public static final com.astro5star.app.data.remote.SocketManager INSTANCE = null;
    
    private SocketManager() {
        super();
    }
    
    public final void init() {
    }
    
    public final void registerUser(@org.jetbrains.annotations.NotNull()
    java.lang.String userId, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function1<? super java.lang.Boolean, kotlin.Unit> callback) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final io.socket.client.Socket getSocket() {
        return null;
    }
    
    public final void requestSession(@org.jetbrains.annotations.NotNull()
    java.lang.String toUserId, @org.jetbrains.annotations.NotNull()
    java.lang.String type, @org.jetbrains.annotations.Nullable()
    org.json.JSONObject birthData, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function1<? super org.json.JSONObject, kotlin.Unit> callback) {
    }
    
    public final void onSessionAnswered(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super org.json.JSONObject, kotlin.Unit> listener) {
    }
    
    public final void onSignal(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super org.json.JSONObject, kotlin.Unit> listener) {
    }
    
    public final void emitSignal(@org.jetbrains.annotations.NotNull()
    org.json.JSONObject data) {
    }
    
    public final void onMessageStatus(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super org.json.JSONObject, kotlin.Unit> listener) {
    }
    
    public final void endSession(@org.jetbrains.annotations.Nullable()
    java.lang.String sessionId) {
    }
    
    public final void onSessionEnded(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> listener) {
    }
    
    /**
     * Listen for session-ended with summary data (deducted, earned, duration)
     * This provides better billing feedback to the user
     */
    public final void onSessionEndedWithSummary(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function4<? super java.lang.String, ? super java.lang.Double, ? super java.lang.Double, ? super java.lang.Integer, kotlin.Unit> listener) {
    }
    
    /**
     * Listen for billing-started event when both parties connect
     * This indicates when the billable session actually begins
     */
    public final void onBillingStarted(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Long, kotlin.Unit> listener) {
    }
    
    public final void onWalletUpdate(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Double, kotlin.Unit> listener) {
    }
    
    public final void off(@org.jetbrains.annotations.NotNull()
    java.lang.String event) {
    }
    
    public final void onConnect(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> listener) {
    }
    
    public final void updateServiceStatus(@org.jetbrains.annotations.NotNull()
    java.lang.String userId, @org.jetbrains.annotations.NotNull()
    java.lang.String service, boolean isEnabled) {
    }
    
    public final void onAstrologerUpdate(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super org.json.JSONObject, kotlin.Unit> listener) {
    }
    
    /**
     * Listen for incoming session requests (calls/chats) when app is in foreground.
     * This is CRITICAL because FCM only works reliably when app is in background/killed.
     * When app is in foreground, server sends via socket instead of FCM.
     */
    public final void onIncomingSession(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super org.json.JSONObject, kotlin.Unit> listener) {
    }
    
    /**
     * Remove incoming session listener (call when activity is destroyed)
     */
    public final void offIncomingSession() {
    }
    
    public final void disconnect() {
    }
    
    public final void removeChatListeners() {
    }
}