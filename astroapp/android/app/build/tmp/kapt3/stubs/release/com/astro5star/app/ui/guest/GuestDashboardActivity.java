package com.astro5star.app.ui.guest;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u0082@\u00a2\u0006\u0002\u0010\fJ\u000e\u0010\r\u001a\u00020\bH\u0082@\u00a2\u0006\u0002\u0010\fJ\b\u0010\u000e\u001a\u00020\u000fH\u0002J\b\u0010\u0010\u001a\u00020\u000fH\u0002J\u0012\u0010\u0011\u001a\u00020\u000f2\b\u0010\u0012\u001a\u0004\u0018\u00010\u0013H\u0014J\u0010\u0010\u0014\u001a\u00020\u00062\u0006\u0010\u0015\u001a\u00020\u0016H\u0002J\b\u0010\u0017\u001a\u00020\u000fH\u0002J\b\u0010\u0018\u001a\u00020\u000fH\u0002J\u0010\u0010\u0019\u001a\u00020\u000f2\u0006\u0010\u001a\u001a\u00020\u001bH\u0002R\u001a\u0010\u0003\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00060\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001c"}, d2 = {"Lcom/astro5star/app/ui/guest/GuestDashboardActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "_astrologers", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "Lcom/astro5star/app/data/model/Astrologer;", "_horoscope", "", "_isLoading", "", "fetchAstrologers", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "fetchHoroscope", "loadAstrologers", "", "loadDailyHoroscope", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "parseAstrologer", "json", "Lorg/json/JSONObject;", "redirectToLogin", "setupSocket", "updateAstrologerList", "jsonArray", "Lorg/json/JSONArray;", "app_release"})
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