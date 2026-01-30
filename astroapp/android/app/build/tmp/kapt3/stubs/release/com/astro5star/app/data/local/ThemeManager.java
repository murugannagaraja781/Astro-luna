package com.astro5star.app.data.local;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0016J\u0016\u0010\u0017\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0018\u001a\u00020\nJ\u0016\u0010\u0019\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u001a\u001a\u00020\u0010R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\n0\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0014\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00100\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00100\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u000e\u00a8\u0006\u001b"}, d2 = {"Lcom/astro5star/app/data/local/ThemeManager;", "", "<init>", "()V", "PREF_NAME", "", "KEY_THEME", "KEY_CUSTOM_BG", "_currentTheme", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/astro5star/app/ui/theme/AppTheme;", "currentTheme", "Lkotlinx/coroutines/flow/StateFlow;", "getCurrentTheme", "()Lkotlinx/coroutines/flow/StateFlow;", "_customBgColor", "", "customBgColor", "getCustomBgColor", "init", "", "context", "Landroid/content/Context;", "setTheme", "theme", "setCustomBackground", "color", "app_release"})
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