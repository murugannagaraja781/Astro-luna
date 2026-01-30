package com.astro5star.app.ui.chat;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000`\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u000f\n\u0002\u0010\t\n\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u000e\u0010%\u001a\u00020&2\u0006\u0010\'\u001a\u00020\u0014J\u000e\u0010(\u001a\u00020&2\u0006\u0010)\u001a\u00020*J\u000e\u0010+\u001a\u00020&2\u0006\u0010)\u001a\u00020*J\u001e\u0010,\u001a\u00020&2\u0006\u0010-\u001a\u00020*2\u0006\u0010)\u001a\u00020*2\u0006\u0010.\u001a\u00020*J\u001e\u0010/\u001a\u00020&2\u0006\u0010-\u001a\u00020*2\u0006\u0010)\u001a\u00020*2\u0006\u0010.\u001a\u00020*J\u0016\u00100\u001a\u00020&2\u0006\u0010.\u001a\u00020*2\u0006\u0010)\u001a\u00020*J\u000e\u00101\u001a\u00020&2\u0006\u0010.\u001a\u00020*J\u000e\u00102\u001a\u00020&2\u0006\u0010.\u001a\u00020*J\u0006\u00103\u001a\u00020&J\u0006\u00104\u001a\u00020&J\u000e\u00105\u001a\u00020&2\u0006\u0010.\u001a\u00020*J\u0016\u00108\u001a\u00020&2\u0006\u0010.\u001a\u00020*2\u0006\u00109\u001a\u00020:R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\n0\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u001a\u0010\u000f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\u00100\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0011\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\u00100\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u000eR\u0014\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00140\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00140\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u000eR\u0014\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00180\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00180\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u000eR\u0014\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00180\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00180\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u000eR\u0014\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00180\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u00180\f\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\u000eR\u0014\u0010!\u001a\b\u0012\u0004\u0012\u00020\"0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010#\u001a\b\u0012\u0004\u0012\u00020\"0\f\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010\u000eR\u000e\u00106\u001a\u00020\u0018X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u00107\u001a\u00020\u0018X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006;"}, d2 = {"Lcom/astro5star/app/ui/chat/ChatViewModel;", "Landroidx/lifecycle/AndroidViewModel;", "application", "Landroid/app/Application;", "<init>", "(Landroid/app/Application;)V", "repository", "Lcom/astro5star/app/data/repository/ChatRepository;", "_messages", "Landroidx/lifecycle/MutableLiveData;", "Lcom/astro5star/app/ui/chat/ChatMessage;", "messages", "Landroidx/lifecycle/LiveData;", "getMessages", "()Landroidx/lifecycle/LiveData;", "_history", "", "history", "getHistory", "_messageStatus", "Lorg/json/JSONObject;", "messageStatus", "getMessageStatus", "_typingStatus", "", "typingStatus", "getTypingStatus", "_sessionEnded", "sessionEnded", "getSessionEnded", "_billingStarted", "billingStarted", "getBillingStarted", "_sessionSummary", "Lcom/astro5star/app/ui/chat/SessionSummary;", "sessionSummary", "getSessionSummary", "sendMessage", "", "data", "sendTyping", "toUserId", "", "sendStopTyping", "markDelivered", "messageId", "sessionId", "markRead", "acceptSession", "joinSession", "endSession", "startListeners", "stopListeners", "loadHistory", "isHistoryLoading", "isMoreHistoryAvailable", "loadMoreHistory", "oldestTimestamp", "", "app_release"})
public final class ChatViewModel extends androidx.lifecycle.AndroidViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.astro5star.app.data.repository.ChatRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.MutableLiveData<com.astro5star.app.ui.chat.ChatMessage> _messages = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<com.astro5star.app.ui.chat.ChatMessage> messages = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.MutableLiveData<java.util.List<com.astro5star.app.ui.chat.ChatMessage>> _history = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.util.List<com.astro5star.app.ui.chat.ChatMessage>> history = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.MutableLiveData<org.json.JSONObject> _messageStatus = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<org.json.JSONObject> messageStatus = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.MutableLiveData<java.lang.Boolean> _typingStatus = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.lang.Boolean> typingStatus = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.MutableLiveData<java.lang.Boolean> _sessionEnded = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.lang.Boolean> sessionEnded = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.MutableLiveData<java.lang.Boolean> _billingStarted = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.lang.Boolean> billingStarted = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.MutableLiveData<com.astro5star.app.ui.chat.SessionSummary> _sessionSummary = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<com.astro5star.app.ui.chat.SessionSummary> sessionSummary = null;
    private boolean isHistoryLoading = false;
    private boolean isMoreHistoryAvailable = true;
    
    public ChatViewModel(@org.jetbrains.annotations.NotNull()
    android.app.Application application) {
        super(null);
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<com.astro5star.app.ui.chat.ChatMessage> getMessages() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.astro5star.app.ui.chat.ChatMessage>> getHistory() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<org.json.JSONObject> getMessageStatus() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.lang.Boolean> getTypingStatus() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.lang.Boolean> getSessionEnded() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.lang.Boolean> getBillingStarted() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<com.astro5star.app.ui.chat.SessionSummary> getSessionSummary() {
        return null;
    }
    
    public final void sendMessage(@org.jetbrains.annotations.NotNull()
    org.json.JSONObject data) {
    }
    
    public final void sendTyping(@org.jetbrains.annotations.NotNull()
    java.lang.String toUserId) {
    }
    
    public final void sendStopTyping(@org.jetbrains.annotations.NotNull()
    java.lang.String toUserId) {
    }
    
    public final void markDelivered(@org.jetbrains.annotations.NotNull()
    java.lang.String messageId, @org.jetbrains.annotations.NotNull()
    java.lang.String toUserId, @org.jetbrains.annotations.NotNull()
    java.lang.String sessionId) {
    }
    
    public final void markRead(@org.jetbrains.annotations.NotNull()
    java.lang.String messageId, @org.jetbrains.annotations.NotNull()
    java.lang.String toUserId, @org.jetbrains.annotations.NotNull()
    java.lang.String sessionId) {
    }
    
    public final void acceptSession(@org.jetbrains.annotations.NotNull()
    java.lang.String sessionId, @org.jetbrains.annotations.NotNull()
    java.lang.String toUserId) {
    }
    
    public final void joinSession(@org.jetbrains.annotations.NotNull()
    java.lang.String sessionId) {
    }
    
    public final void endSession(@org.jetbrains.annotations.NotNull()
    java.lang.String sessionId) {
    }
    
    public final void startListeners() {
    }
    
    public final void stopListeners() {
    }
    
    public final void loadHistory(@org.jetbrains.annotations.NotNull()
    java.lang.String sessionId) {
    }
    
    public final void loadMoreHistory(@org.jetbrains.annotations.NotNull()
    java.lang.String sessionId, long oldestTimestamp) {
    }
}