package com.astro5star.app.data.api;

/**
 * ApiService - HTTP client for backend communication
 *
 * PURPOSE:
 * Handles all HTTP communication with the Node.js backend server.
 * Currently only handles /register endpoint, but can be extended.
 *
 * WHY OKHTTP:
 * - Industry standard HTTP client for Android
 * - Efficient connection pooling
 * - Automatic retry and redirect handling
 * - Easy to use synchronous and async APIs
 * - Good timeout configuration
 *
 * THREAD SAFETY:
 * OkHttpClient is thread-safe and should be shared.
 * All API calls should be made from background threads.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0002\b\t\b\u00c7\u0002\u0018\u00002\u00020\u0001:\u0001\u0018B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J1\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u00062\u0006\u0010\f\u001a\u00020\u00062\b\b\u0002\u0010\r\u001a\u00020\u000e2\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u0010\u00a2\u0006\u0002\u0010\u0011J\u001e\u0010\u0012\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u00062\u0006\u0010\u0013\u001a\u00020\u00062\u0006\u0010\u0014\u001a\u00020\u0006J\u001e\u0010\u0015\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u00062\u0006\u0010\u0016\u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\u0006R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0019"}, d2 = {"Lcom/astro5star/app/data/api/ApiService;", "", "()V", "JSON_MEDIA_TYPE", "Lokhttp3/MediaType;", "TAG", "", "client", "Lokhttp3/OkHttpClient;", "getChatHistory", "Lcom/astro5star/app/data/api/ApiService$ApiResult;", "serverUrl", "sessionId", "limit", "", "before", "", "(Ljava/lang/String;Ljava/lang/String;ILjava/lang/Long;)Lcom/astro5star/app/data/api/ApiService$ApiResult;", "initiateCall", "callerId", "calleeId", "register", "userId", "fcmToken", "ApiResult", "app_release"})
public final class ApiService {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "ApiService";
    @org.jetbrains.annotations.NotNull()
    private static final okhttp3.OkHttpClient client = null;
    @org.jetbrains.annotations.NotNull()
    private static final okhttp3.MediaType JSON_MEDIA_TYPE = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.astro5star.app.data.api.ApiService INSTANCE = null;
    
    private ApiService() {
        super();
    }
    
    /**
     * Register userId and FCM token with the backend server
     *
     * ENDPOINT: POST /register
     * BODY: { "userId": "xxx", "fcmToken": "yyy" }
     *
     * THE SERVER STORES THIS MAPPING:
     * When someone calls this user, the server looks up the FCM token
     * and sends a push notification to wake up this device.
     *
     * MUST BE CALLED FROM BACKGROUND THREAD (uses synchronous HTTP)
     */
    @org.jetbrains.annotations.NotNull()
    public final com.astro5star.app.data.api.ApiService.ApiResult register(@org.jetbrains.annotations.NotNull()
    java.lang.String serverUrl, @org.jetbrains.annotations.NotNull()
    java.lang.String userId, @org.jetbrains.annotations.NotNull()
    java.lang.String fcmToken) {
        return null;
    }
    
    /**
     * Initiate a call to another user
     *
     * ENDPOINT: POST /call
     * BODY: { "callerId": "xxx", "calleeId": "yyy", "callerName": "zzz" }
     */
    @org.jetbrains.annotations.NotNull()
    public final com.astro5star.app.data.api.ApiService.ApiResult initiateCall(@org.jetbrains.annotations.NotNull()
    java.lang.String serverUrl, @org.jetbrains.annotations.NotNull()
    java.lang.String callerId, @org.jetbrains.annotations.NotNull()
    java.lang.String calleeId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.astro5star.app.data.api.ApiService.ApiResult getChatHistory(@org.jetbrains.annotations.NotNull()
    java.lang.String serverUrl, @org.jetbrains.annotations.NotNull()
    java.lang.String sessionId, int limit, @org.jetbrains.annotations.Nullable()
    java.lang.Long before) {
        return null;
    }
    
    /**
     * Result class for API operations
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\u0002\u0010\bJ\t\u0010\u000f\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\u0010\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u000b\u0010\u0011\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J+\u0010\u0012\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u00c6\u0001J\u0013\u0010\u0013\u001a\u00020\u00032\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0015\u001a\u00020\u0016H\u00d6\u0001J\t\u0010\u0017\u001a\u00020\u0005H\u00d6\u0001R\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e\u00a8\u0006\u0018"}, d2 = {"Lcom/astro5star/app/data/api/ApiService$ApiResult;", "", "success", "", "error", "", "data", "Lorg/json/JSONObject;", "(ZLjava/lang/String;Lorg/json/JSONObject;)V", "getData", "()Lorg/json/JSONObject;", "getError", "()Ljava/lang/String;", "getSuccess", "()Z", "component1", "component2", "component3", "copy", "equals", "other", "hashCode", "", "toString", "app_release"})
    public static final class ApiResult {
        private final boolean success = false;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.String error = null;
        @org.jetbrains.annotations.Nullable()
        private final org.json.JSONObject data = null;
        
        public final boolean component1() {
            return false;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String component2() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final org.json.JSONObject component3() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.astro5star.app.data.api.ApiService.ApiResult copy(boolean success, @org.jetbrains.annotations.Nullable()
        java.lang.String error, @org.jetbrains.annotations.Nullable()
        org.json.JSONObject data) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
        
        public ApiResult(boolean success, @org.jetbrains.annotations.Nullable()
        java.lang.String error, @org.jetbrains.annotations.Nullable()
        org.json.JSONObject data) {
            super();
        }
        
        public final boolean getSuccess() {
            return false;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String getError() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final org.json.JSONObject getData() {
            return null;
        }
    }
}