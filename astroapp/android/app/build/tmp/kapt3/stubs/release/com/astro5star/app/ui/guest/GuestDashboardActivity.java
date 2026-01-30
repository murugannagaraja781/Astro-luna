package com.astro5star.app.ui.guest;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\f\u001a\u00020\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\u000fH\u0014J\b\u0010\u0010\u001a\u00020\rH\u0002J\u0010\u0010\u0011\u001a\u00020\r2\u0006\u0010\u0012\u001a\u00020\u0013H\u0002J\b\u0010\u0014\u001a\u00020\rH\u0002J\b\u0010\u0015\u001a\u00020\rH\u0002J\b\u0010\u0016\u001a\u00020\rH\u0002J\u000e\u0010\u0017\u001a\u00020\u0006H\u0082@\u00a2\u0006\u0002\u0010\u0018J\u0014\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u0082@\u00a2\u0006\u0002\u0010\u0018J\u0010\u0010\u001a\u001a\u00020\t2\u0006\u0010\u001b\u001a\u00020\u001cH\u0002R\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0007\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\b0\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001d"}, d2 = {"Lcom/astro5star/app/ui/guest/GuestDashboardActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "<init>", "()V", "_horoscope", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "_astrologers", "", "Lcom/astro5star/app/data/model/Astrologer;", "_isLoading", "", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "setupSocket", "updateAstrologerList", "jsonArray", "Lorg/json/JSONArray;", "redirectToLogin", "loadDailyHoroscope", "loadAstrologers", "fetchHoroscope", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "fetchAstrologers", "parseAstrologer", "json", "Lorg/json/JSONObject;", "app_release"})
public final class GuestDashboardActivity extends androidx.appcompat.app.AppCompatActivity {
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _horoscope = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.List<com.astro5star.app.data.model.Astrologer>> _astrologers = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _isLoading = null;
    
    public GuestDashboardActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void setupSocket() {
    }
    
    private final void updateAstrologerList(org.json.JSONArray jsonArray) {
    }
    
    private final void redirectToLogin() {
    }
    
    private final void loadDailyHoroscope() {
    }
    
    private final void loadAstrologers() {
    }
    
    private final java.lang.Object fetchHoroscope(kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    private final java.lang.Object fetchAstrologers(kotlin.coroutines.Continuation<? super java.util.List<com.astro5star.app.data.model.Astrologer>> $completion) {
        return null;
    }
    
    private final com.astro5star.app.data.model.Astrologer parseAstrologer(org.json.JSONObject json) {
        return null;
    }
}