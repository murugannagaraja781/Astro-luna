package com.astro5star.app.ui.wallet;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\b\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0015H\u0014J\b\u0010\u0016\u001a\u00020\u0013H\u0014J\b\u0010\u0017\u001a\u00020\u0013H\u0014J\b\u0010\u0018\u001a\u00020\u0013H\u0002J\b\u0010\u0019\u001a\u00020\u0013H\u0002J\b\u0010\u001a\u001a\u00020\u0013H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082.\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R+\u0010\u000b\u001a\u00020\n2\u0006\u0010\t\u001a\u00020\n8B@BX\u0082\u008e\u0002\u00a2\u0006\u0012\n\u0004\b\u0010\u0010\u0011\u001a\u0004\b\f\u0010\r\"\u0004\b\u000e\u0010\u000f\u00a8\u0006\u001b"}, d2 = {"Lcom/astro5star/app/ui/wallet/WalletActivity;", "Landroidx/activity/ComponentActivity;", "<init>", "()V", "tokenManager", "Lcom/astro5star/app/data/local/TokenManager;", "transactionsState", "Landroidx/compose/runtime/snapshots/SnapshotStateList;", "Lorg/json/JSONObject;", "<set-?>", "", "balanceState", "getBalanceState", "()D", "setBalanceState", "(D)V", "balanceState$delegate", "Landroidx/compose/runtime/MutableDoubleState;", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "onResume", "onPause", "updateBalanceFromSession", "refreshWalletBalance", "loadPaymentHistory", "app_release"})
public final class WalletActivity extends androidx.activity.ComponentActivity {
    private com.astro5star.app.data.local.TokenManager tokenManager;
    @org.jetbrains.annotations.NotNull()
    private final androidx.compose.runtime.snapshots.SnapshotStateList<org.json.JSONObject> transactionsState = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.compose.runtime.MutableDoubleState balanceState$delegate = null;
    
    public WalletActivity() {
        super();
    }
    
    private final void setBalanceState(double p0) {
    }
    
    private final double getBalanceState() {
        return 0.0;
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override()
    protected void onResume() {
    }
    
    @java.lang.Override()
    protected void onPause() {
    }
    
    private final void updateBalanceFromSession() {
    }
    
    private final void refreshWalletBalance() {
    }
    
    private final void loadPaymentHistory() {
    }
}