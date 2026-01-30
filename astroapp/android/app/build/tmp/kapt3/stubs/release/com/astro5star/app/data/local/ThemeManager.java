package com.astro5star.app.data.local;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0015J\u0016\u0010\u0016\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0017\u001a\u00020\u000bJ\u0016\u0010\u0018\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0019\u001a\u00020\tR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\f\u001a\b\u0012\u0004\u0012\u00020\t0\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0017\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u000b0\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u000f\u00a8\u0006\u001a"}, d2 = {"Lcom/astro5star/app/data/local/ThemeManager;", "", "()V", "KEY_CUSTOM_BG", "", "KEY_THEME", "PREF_NAME", "_currentTheme", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/astro5star/app/ui/theme/AppTheme;", "_customBgColor", "", "currentTheme", "Lkotlinx/coroutines/flow/StateFlow;", "getCurrentTheme", "()Lkotlinx/coroutines/flow/StateFlow;", "customBgColor", "getCustomBgColor", "init", "", "context", "Landroid/content/Context;", "setCustomBackground", "color", "setTheme", "theme", "app_release"})
public final class ThemeManager {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String PREF_NAME = "theme_prefs";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_THEME = "app_theme";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_CUSTOM_BG = "custom_bg_color";
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.MutableStateFlow<com.astro5star.app.ui.theme.AppTheme> _currentTheme = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.StateFlow<com.astro5star.app.ui.theme.AppTheme> currentTheme = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Integer> _customBgColor = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.StateFlow<java.lang.Integer> customBgColor = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.astro5star.app.data.local.ThemeManager INSTANCE = null;
    
    private ThemeManager() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.astro5star.app.ui.theme.AppTheme> getCurrentTheme() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Integer> getCustomBgColor() {
        return null;
    }
    
    public final void init(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    public final void setTheme(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    com.astro5star.app.ui.theme.AppTheme theme) {
    }
    
    public final void setCustomBackground(@org.jetbrains.annotations.NotNull()
    android.content.Context context, int color) {
    }
}