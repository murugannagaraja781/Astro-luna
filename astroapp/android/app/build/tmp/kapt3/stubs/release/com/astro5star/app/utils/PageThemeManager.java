package com.astro5star.app.utils;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0003\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J&\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00042\u0006\u0010\u0015\u001a\u00020\u00042\u0006\u0010\u0016\u001a\u00020\u0011J\u0016\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0004J&\u0010\u0019\u001a\u00020\u00182\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00042\u0006\u0010\u0015\u001a\u00020\u00042\u0006\u0010\u001a\u001a\u00020\u0011R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u0017\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00040\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f\u00a8\u0006\u001b"}, d2 = {"Lcom/astro5star/app/utils/PageThemeManager;", "", "()V", "ATTR_BG", "", "ATTR_BORDER", "ATTR_BUTTON", "ATTR_CARD", "ATTR_FONT", "ATTR_FOOTER", "ATTR_HEADER", "PREF_NAME", "pages", "", "getPages", "()Ljava/util/List;", "getPageColor", "", "context", "Landroid/content/Context;", "pageName", "attribute", "defaultColor", "resetPage", "", "savePageColor", "color", "app_release"})
public final class PageThemeManager {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String PREF_NAME = "page_theme_prefs";
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<java.lang.String> pages = null;
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ATTR_BG = "background";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ATTR_CARD = "card";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ATTR_FONT = "font";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ATTR_BUTTON = "button";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ATTR_BORDER = "border";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ATTR_HEADER = "header";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ATTR_FOOTER = "footer";
    @org.jetbrains.annotations.NotNull()
    public static final com.astro5star.app.utils.PageThemeManager INSTANCE = null;
    
    private PageThemeManager() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getPages() {
        return null;
    }
    
    public final void savePageColor(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String pageName, @org.jetbrains.annotations.NotNull()
    java.lang.String attribute, int color) {
    }
    
    public final int getPageColor(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String pageName, @org.jetbrains.annotations.NotNull()
    java.lang.String attribute, int defaultColor) {
        return 0;
    }
    
    public final void resetPage(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String pageName) {
    }
}