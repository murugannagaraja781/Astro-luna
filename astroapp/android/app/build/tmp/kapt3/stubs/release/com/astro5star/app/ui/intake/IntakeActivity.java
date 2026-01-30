package com.astro5star.app.ui.intake;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u008e\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010%\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u0006\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0010\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0010\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010*\u001a\u00020+2\u0006\u0010,\u001a\u00020\tH\u0002J \u0010-\u001a\u00020+2\u0006\u0010.\u001a\u00020\u00172\u0006\u0010/\u001a\u00020\u00172\u0006\u00100\u001a\u00020\u0012H\u0002J\b\u00101\u001a\u00020+H\u0002J\u0010\u00102\u001a\u00020+2\u0006\u00103\u001a\u00020\tH\u0002J\u0010\u00104\u001a\u00020+2\u0006\u00105\u001a\u00020\fH\u0002J\b\u00106\u001a\u00020+H\u0002J\u0018\u00107\u001a\u00020+2\u0006\u00103\u001a\u00020\t2\u0006\u0010%\u001a\u00020\tH\u0002J\"\u00108\u001a\u00020+2\u0006\u00109\u001a\u00020\u00042\u0006\u0010:\u001a\u00020\u00042\b\u0010;\u001a\u0004\u0018\u00010<H\u0014J\u0012\u0010=\u001a\u00020+2\b\u0010>\u001a\u0004\u0018\u00010?H\u0014J\b\u0010@\u001a\u00020+H\u0014J\u0010\u0010A\u001a\u00020+2\u0006\u0010;\u001a\u00020\fH\u0002J`\u0010B\u001a\u00020+2\u0006\u0010C\u001a\u00020\t2\u0006\u0010D\u001a\u00020\t2\u0006\u0010E\u001a\u00020\u00042\u0006\u0010F\u001a\u00020\u00042\u0006\u0010G\u001a\u00020\u00042\u0006\u0010H\u001a\u00020\u00042\u0006\u0010I\u001a\u00020\u00042\u0006\u0010J\u001a\u00020\t2\u0006\u0010K\u001a\u00020\t2\u0006\u0010L\u001a\u00020\t2\u0006\u0010M\u001a\u00020\tH\u0002J\u001a\u0010N\u001a\u00020+2\u0006\u0010O\u001a\u00020P2\b\u0010Q\u001a\u0004\u0018\u00010\tH\u0002J\u0018\u0010R\u001a\u00020+2\u0006\u0010S\u001a\u00020T2\u0006\u00100\u001a\u00020\u0012H\u0002J\b\u0010U\u001a\u00020+H\u0002J\b\u0010V\u001a\u00020+H\u0002J\b\u0010W\u001a\u00020+H\u0002J\u0010\u0010X\u001a\u00020+2\u0006\u00103\u001a\u00020\tH\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\f0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u000eX\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0010\u001a\u0004\u0018\u00010\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0012X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0014\u001a\u0004\u0018\u00010\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0015\u001a\u0004\u0018\u00010\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010\u0016\u001a\u0004\u0018\u00010\u0017X\u0082\u000e\u00a2\u0006\u0004\n\u0002\u0010\u0018R\u0012\u0010\u0019\u001a\u0004\u0018\u00010\u0017X\u0082\u000e\u00a2\u0006\u0004\n\u0002\u0010\u0018R\u0010\u0010\u001a\u001a\u0004\u0018\u00010\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010\u001b\u001a\u0004\u0018\u00010\u0017X\u0082\u000e\u00a2\u0006\u0004\n\u0002\u0010\u0018R\u000e\u0010\u001c\u001a\u00020\u001dX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u001e\u001a\u0004\u0018\u00010\u001fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010 \u001a\u0004\u0018\u00010\u0017X\u0082\u000e\u00a2\u0006\u0004\n\u0002\u0010\u0018R\u0012\u0010!\u001a\u0004\u0018\u00010\u0017X\u0082\u000e\u00a2\u0006\u0004\n\u0002\u0010\u0018R\u0012\u0010\"\u001a\u0004\u0018\u00010\u0017X\u0082\u000e\u00a2\u0006\u0004\n\u0002\u0010\u0018R\u000e\u0010#\u001a\u00020$X\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010%\u001a\u0004\u0018\u00010\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010&\u001a\u0004\u0018\u00010\'X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010(\u001a\u0004\u0018\u00010)X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006Y"}, d2 = {"Lcom/astro5star/app/ui/intake/IntakeActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "CITY_SEARCH_REQUEST_CODE", "", "apiInterface", "Lcom/astro5star/app/data/api/ApiInterface;", "cityList", "", "", "cityMap", "", "Lorg/json/JSONObject;", "etPartnerPlace", "Landroid/widget/AutoCompleteTextView;", "etPlace", "existingData", "isEditMode", "", "isSearchingForClient", "partnerId", "partnerImage", "partnerLatitude", "", "Ljava/lang/Double;", "partnerLongitude", "partnerName", "partnerTimezone", "searchHandler", "Landroid/os/Handler;", "searchRunnable", "Ljava/lang/Runnable;", "selectedLatitude", "selectedLongitude", "selectedTimezone", "tokenManager", "Lcom/astro5star/app/data/local/TokenManager;", "type", "waitTimer", "Landroid/os/CountDownTimer;", "waitingDialog", "Landroidx/appcompat/app/AlertDialog;", "fetchIntakeData", "", "userId", "fetchTimezone", "lat", "lon", "isClient", "fillDummyData", "handleNoAnswer", "sessionId", "initiateSession", "birthData", "loadIntakeDetails", "navigateToSession", "onActivityResult", "requestCode", "resultCode", "data", "Landroid/content/Intent;", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onDestroy", "prefillForm", "saveIntakeDetails", "name", "place", "day", "month", "year", "hour", "minute", "gender", "occupation", "marital", "topic", "setSpinnerSelection", "spinner", "Landroid/widget/Spinner;", "value", "setupCitySearch", "view", "Landroid/widget/TextView;", "setupPartnerCheckbox", "setupSpinners", "submitForm", "waitForAnswer", "app_release"})
public final class IntakeActivity extends androidx.appcompat.app.AppCompatActivity {
    @org.jetbrains.annotations.Nullable()
    private java.lang.String partnerId;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String type;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String partnerName;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String partnerImage;
    @org.jetbrains.annotations.Nullable()
    private java.lang.Double selectedLatitude;
    @org.jetbrains.annotations.Nullable()
    private java.lang.Double selectedLongitude;
    @org.jetbrains.annotations.Nullable()
    private java.lang.Double selectedTimezone;
    @org.jetbrains.annotations.NotNull()
    private final com.astro5star.app.data.api.ApiInterface apiInterface = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.String> cityList = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Map<java.lang.String, org.json.JSONObject> cityMap = null;
    private android.widget.AutoCompleteTextView etPlace;
    @org.jetbrains.annotations.NotNull()
    private android.os.Handler searchHandler;
    @org.jetbrains.annotations.Nullable()
    private java.lang.Runnable searchRunnable;
    private android.widget.AutoCompleteTextView etPartnerPlace;
    @org.jetbrains.annotations.Nullable()
    private java.lang.Double partnerLatitude;
    @org.jetbrains.annotations.Nullable()
    private java.lang.Double partnerLongitude;
    @org.jetbrains.annotations.Nullable()
    private java.lang.Double partnerTimezone;
    private boolean isEditMode = false;
    @org.jetbrains.annotations.Nullable()
    private org.json.JSONObject existingData;
    private com.astro5star.app.data.local.TokenManager tokenManager;
    private boolean isSearchingForClient = true;
    private final int CITY_SEARCH_REQUEST_CODE = 1001;
    @org.jetbrains.annotations.Nullable()
    private androidx.appcompat.app.AlertDialog waitingDialog;
    @org.jetbrains.annotations.Nullable()
    private android.os.CountDownTimer waitTimer;
    
    public IntakeActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void prefillForm(org.json.JSONObject data) {
    }
    
    private final void setupSpinners() {
    }
    
    private final void setupPartnerCheckbox() {
    }
    
    private final void setupCitySearch(android.widget.TextView view, boolean isClient) {
    }
    
    @java.lang.Override()
    protected void onActivityResult(int requestCode, int resultCode, @org.jetbrains.annotations.Nullable()
    android.content.Intent data) {
    }
    
    private final void fetchTimezone(double lat, double lon, boolean isClient) {
    }
    
    private final void fetchIntakeData(java.lang.String userId) {
    }
    
    private final void loadIntakeDetails() {
    }
    
    private final void fillDummyData() {
    }
    
    private final void setSpinnerSelection(android.widget.Spinner spinner, java.lang.String value) {
    }
    
    private final void saveIntakeDetails(java.lang.String name, java.lang.String place, int day, int month, int year, int hour, int minute, java.lang.String gender, java.lang.String occupation, java.lang.String marital, java.lang.String topic) {
    }
    
    private final void submitForm() {
    }
    
    private final void initiateSession(org.json.JSONObject birthData) {
    }
    
    private final void waitForAnswer(java.lang.String sessionId) {
    }
    
    private final void handleNoAnswer(java.lang.String sessionId) {
    }
    
    private final void navigateToSession(java.lang.String sessionId, java.lang.String type) {
    }
    
    @java.lang.Override()
    protected void onDestroy() {
    }
}