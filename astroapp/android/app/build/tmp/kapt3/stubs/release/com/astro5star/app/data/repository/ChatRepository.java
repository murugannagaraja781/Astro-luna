package com.astro5star.app.data.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000d\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0013\b\u0007\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\fJ,\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\u0010\u001a\u00020\u00112\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\u0013H\u0086@\u00a2\u0006\u0002\u0010\u0014J\u001a\u0010\u0015\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00180\u00170\u00162\u0006\u0010\u000b\u001a\u00020\fJ\u001a\u0010\u0019\u001a\u00020\n2\u0012\u0010\u001a\u001a\u000e\u0012\u0004\u0012\u00020\u001c\u0012\u0004\u0012\u00020\n0\u001bJ\u001a\u0010\u001d\u001a\u00020\n2\u0012\u0010\u001e\u001a\u000e\u0012\u0004\u0012\u00020\u001c\u0012\u0004\u0012\u00020\n0\u001bJ\u0014\u0010\u001f\u001a\u00020\n2\f\u0010 \u001a\b\u0012\u0004\u0012\u00020\n0!J\u0014\u0010\"\u001a\u00020\n2\f\u0010#\u001a\b\u0012\u0004\u0012\u00020\n0!J\u0014\u0010$\u001a\u00020\n2\f\u0010%\u001a\b\u0012\u0004\u0012\u00020\n0!J\u001e\u0010&\u001a\u00020\n2\u0006\u0010\'\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\f2\u0006\u0010\u000b\u001a\u00020\fJ\u001e\u0010(\u001a\u00020\n2\u0006\u0010\'\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\f2\u0006\u0010\u000b\u001a\u00020\fJ\u0006\u0010)\u001a\u00020\nJ\u0016\u0010*\u001a\u00020\n2\u0006\u0010+\u001a\u00020\u0018H\u0086@\u00a2\u0006\u0002\u0010,J\u000e\u0010-\u001a\u00020\n2\u0006\u0010.\u001a\u00020\u001cJ\u000e\u0010/\u001a\u00020\n2\u0006\u0010\r\u001a\u00020\fJ\u000e\u00100\u001a\u00020\n2\u0006\u0010\r\u001a\u00020\fJ\u001e\u00101\u001a\u00020\n2\u0006\u0010\'\u001a\u00020\f2\u0006\u00102\u001a\u00020\fH\u0086@\u00a2\u0006\u0002\u00103R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u00064"}, d2 = {"Lcom/astro5star/app/data/repository/ChatRepository;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "chatDao", "Lcom/astro5star/app/data/local/dao/ChatDao;", "socket", "Lio/socket/client/Socket;", "acceptSession", "", "sessionId", "", "toUserId", "fetchHistoryFromServer", "", "limit", "", "before", "", "(Ljava/lang/String;ILjava/lang/Long;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getMessages", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/astro5star/app/data/local/entity/ChatMessageEntity;", "listenIncoming", "onMessage", "Lkotlin/Function1;", "Lorg/json/JSONObject;", "listenMessageStatus", "onStatus", "listenSessionEnded", "onSessionEnded", "Lkotlin/Function0;", "listenStopTyping", "onStopTyping", "listenTyping", "onTyping", "markDelivered", "messageId", "markRead", "removeListeners", "saveMessage", "msg", "(Lcom/astro5star/app/data/local/entity/ChatMessageEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "sendMessage", "data", "sendStopTyping", "sendTyping", "updateMessageStatus", "status", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_release"})
public final class ChatRepository {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final com.astro5star.app.data.local.dao.ChatDao chatDao = null;
    @org.jetbrains.annotations.Nullable()
    private final io.socket.client.Socket socket = null;
    
    public ChatRepository(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object saveMessage(@org.jetbrains.annotations.NotNull()
    com.astro5star.app.data.local.entity.ChatMessageEntity msg, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.astro5star.app.data.local.entity.ChatMessageEntity>> getMessages(@org.jetbrains.annotations.NotNull()
    java.lang.String sessionId) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object updateMessageStatus(@org.jetbrains.annotations.NotNull()
    java.lang.String messageId, @org.jetbrains.annotations.NotNull()
    java.lang.String status, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object fetchHistoryFromServer(@org.jetbrains.annotations.NotNull()
    java.lang.String sessionId, int limit, @org.jetbrains.annotations.Nullable()
    java.lang.Long before, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
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
    
    public final void listenIncoming(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super org.json.JSONObject, kotlin.Unit> onMessage) {
    }
    
    public final void listenMessageStatus(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super org.json.JSONObject, kotlin.Unit> onStatus) {
    }
    
    public final void listenTyping(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onTyping) {
    }
    
    public final void listenStopTyping(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onStopTyping) {
    }
    
    public final void acceptSession(@org.jetbrains.annotations.NotNull()
    java.lang.String sessionId, @org.jetbrains.annotations.NotNull()
    java.lang.String toUserId) {
    }
    
    public final void listenSessionEnded(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onSessionEnded) {
    }
    
    public final void removeListeners() {
    }
}