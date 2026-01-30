package com.astro5star.app.ui.payment;

/**
 * PaymentActivity - Handles PhonePe Native SDK Payment
 * Uses programmatic UI to avoid layout confusion.
 */
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000R\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 #2\u00020\u0001:\u0001#B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rH\u0014J\u0010\u0010\u0010\u001a\u00020\u000b2\u0006\u0010\u0011\u001a\u00020\u0012H\u0002J\u0010\u0010\u0013\u001a\u00020\u000b2\u0006\u0010\u0014\u001a\u00020\u000fH\u0002J\u0010\u0010\u0015\u001a\u00020\u000b2\u0006\u0010\u0016\u001a\u00020\u0012H\u0002J\u0010\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u000fH\u0002J\u0010\u0010\u001a\u001a\u00020\u000b2\u0006\u0010\u001b\u001a\u00020\u000fH\u0002J\"\u0010\u001c\u001a\u00020\u000b2\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020\u001e2\b\u0010 \u001a\u0004\u0018\u00010!H\u0014J\b\u0010\"\u001a\u00020\u000bH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006$"}, d2 = {"Lcom/astro5star/app/ui/payment/PaymentActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "<init>", "()V", "tokenManager", "Lcom/astro5star/app/data/local/TokenManager;", "statusText", "Landroid/widget/TextView;", "webView", "Landroid/webkit/WebView;", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "pendingTransactionId", "", "startWebPayment", "amount", "", "monitorWebPayment", "txnId", "startPayment", "amountRupees", "handleDeepLink", "", "url", "showError", "message", "onActivityResult", "requestCode", "", "resultCode", "data", "Landroid/content/Intent;", "checkPaymentStatus", "Companion", "app_release"})
public final class PaymentActivity extends androidx.appcompat.app.AppCompatActivity {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "PaymentActivity";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String MERCHANT_ID = "M22LBBWEJKI6A";
    private static final int B2B_PG_REQUEST_CODE = 777;
    private static final boolean USE_NATIVE_SDK = false;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String SERVER_URL = "https://astroluna.in";
    private com.astro5star.app.data.local.TokenManager tokenManager;
    private android.widget.TextView statusText;
    private android.webkit.WebView webView;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String pendingTransactionId;
    @org.jetbrains.annotations.NotNull()
    public static final com.astro5star.app.ui.payment.PaymentActivity.Companion Companion = null;
    
    public PaymentActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void startWebPayment(double amount) {
    }
    
    private final void monitorWebPayment(java.lang.String txnId) {
    }
    
    private final void startPayment(double amountRupees) {
    }
    
    private final boolean handleDeepLink(java.lang.String url) {
        return false;
    }
    
    private final void showError(java.lang.String message) {
    }
    
    @java.lang.Override()
    protected void onActivityResult(int requestCode, int resultCode, @org.jetbrains.annotations.Nullable()
    android.content.Intent data) {
    }
    
    private final void checkPaymentStatus() {
    }
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"Lcom/astro5star/app/ui/payment/PaymentActivity$Companion;", "", "<init>", "()V", "TAG", "", "MERCHANT_ID", "B2B_PG_REQUEST_CODE", "", "USE_NATIVE_SDK", "", "SERVER_URL", "app_release"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}