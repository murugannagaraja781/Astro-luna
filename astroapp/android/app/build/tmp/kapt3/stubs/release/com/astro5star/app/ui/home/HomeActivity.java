package com.astro5star.app.ui.home;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000R\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u0007\u0018\u0000 *2\u00020\u0001:\u0001*B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0014\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u0082@\u00a2\u0006\u0002\u0010\u0012J\u000e\u0010\u0013\u001a\u00020\bH\u0082@\u00a2\u0006\u0002\u0010\u0012J(\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\b2\u0006\u0010\u0017\u001a\u00020\b2\u0006\u0010\u0018\u001a\u00020\b2\u0006\u0010\u0019\u001a\u00020\bH\u0002J\b\u0010\u001a\u001a\u00020\u0015H\u0002J\b\u0010\u001b\u001a\u00020\u0015H\u0002J\b\u0010\u001c\u001a\u00020\u0015H\u0002J\u0012\u0010\u001d\u001a\u00020\u00152\b\u0010\u001e\u001a\u0004\u0018\u00010\u001fH\u0014J\b\u0010 \u001a\u00020\u0015H\u0014J\b\u0010!\u001a\u00020\u0015H\u0014J\u0010\u0010\"\u001a\u00020\u00062\u0006\u0010#\u001a\u00020$H\u0002J\b\u0010%\u001a\u00020\u0015H\u0002J\b\u0010&\u001a\u00020\u0015H\u0002J\u0018\u0010\'\u001a\u00020\u00152\u0006\u0010(\u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\bH\u0002J\u0010\u0010)\u001a\u00020\u00152\u0006\u0010(\u001a\u00020\u0006H\u0002R\u001a\u0010\u0003\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00060\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006+"}, d2 = {"Lcom/astro5star/app/ui/home/HomeActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "_astrologers", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "Lcom/astro5star/app/data/model/Astrologer;", "_horoscope", "", "_isLoading", "", "_walletBalance", "", "client", "Lokhttp3/OkHttpClient;", "tokenManager", "Lcom/astro5star/app/data/local/TokenManager;", "fetchAstrologers", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "fetchHoroscope", "initiateSession", "", "astrologerId", "type", "astroName", "astroImage", "loadAstrologers", "loadDailyHoroscope", "loadWalletBalance", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onDestroy", "onResume", "parseAstrologer", "json", "Lorg/json/JSONObject;", "refreshWalletBalance", "setupSocket", "startCall", "astro", "startChat", "Companion", "app_release"})
public final class HomeActivity extends androidx.appcompat.app.AppCompatActivity {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "HomeActivity";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String SERVER_URL = "https://astro5star.com";
    private com.astro5star.app.data.local.TokenManager tokenManager;
    @org.jetbrains.annotations.NotNull()
    private final okhttp3.OkHttpClient client = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Double> _walletBalance = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _horoscope = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.List<com.astro5star.app.data.model.Astrologer>> _astrologers = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _isLoading = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.astro5star.app.ui.home.HomeActivity.Companion Companion = null;
    
    public HomeActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void loadWalletBalance() {
    }
    
    private final void refreshWalletBalance() {
    }
    
    private final void loadDailyHoroscope() {
    }
    
    private final java.lang.Object fetchHoroscope(kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    private final void loadAstrologers() {
    }
    
    private final java.lang.Object fetchAstrologers(kotlin.coroutines.Continuation<? super java.util.List<com.astro5star.app.data.model.Astrologer>> $completion) {
        return null;
    }
    
    private final com.astro5star.app.data.model.Astrologer parseAstrologer(org.json.JSONObject json) {
        return null;
    }
    
    private final void setupSocket() {
    }
    
    private final void startChat(com.astro5star.app.data.model.Astrologer astro) {
    }
    
    private final void startCall(com.astro5star.app.data.model.Astrologer astro, java.lang.String type) {
    }
    
    private final void initiateSession(java.lang.String astrologerId, java.lang.String type, java.lang.String astroName, java.lang.String astroImage) {
    }
    
    @java.lang.Override()
    protected void onResume() {
    }
    
    @java.lang.Override()
    protected void onDestroy() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0006"}, d2 = {"Lcom/astro5star/app/ui/home/HomeActivity$Companion;", "", "()V", "SERVER_URL", "", "TAG", "app_release"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}