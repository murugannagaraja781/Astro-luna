package com.astro5star.app.ui.home;

/**
 * AstrologerAdapter - RecyclerView adapter for astrologer cards
 *
 * Displays list of astrologers with their online status and action buttons
 */
@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\b\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\u001dBQ\u0012\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u0012\u0012\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\b0\u0007\u0012\u0012\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\b0\u0007\u0012\u0012\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\b0\u0007\u00a2\u0006\u0004\b\u000b\u0010\fJ\u0018\u0010\r\u001a\u00020\u00022\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0016J\u0018\u0010\u0012\u001a\u00020\b2\u0006\u0010\u0013\u001a\u00020\u00022\u0006\u0010\u0014\u001a\u00020\u0011H\u0016J\b\u0010\u0015\u001a\u00020\u0011H\u0016J\u0014\u0010\u0016\u001a\u00020\b2\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004J\u0016\u0010\u0018\u001a\u00020\b2\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001cR\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\b0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\b0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\b0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001e"}, d2 = {"Lcom/astro5star/app/ui/home/AstrologerAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/astro5star/app/ui/home/AstrologerAdapter$ViewHolder;", "astrologers", "", "Lcom/astro5star/app/data/model/Astrologer;", "onChatClick", "Lkotlin/Function1;", "", "onAudioClick", "onVideoClick", "<init>", "(Ljava/util/List;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;)V", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "", "onBindViewHolder", "holder", "position", "getItemCount", "updateList", "newList", "updateAstrologerStatus", "userId", "", "isOnline", "", "ViewHolder", "app_release"})
public final class AstrologerAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.astro5star.app.ui.home.AstrologerAdapter.ViewHolder> {
    @org.jetbrains.annotations.NotNull()
    private java.util.List<com.astro5star.app.data.model.Astrologer> astrologers;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.jvm.functions.Function1<com.astro5star.app.data.model.Astrologer, kotlin.Unit> onChatClick = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.jvm.functions.Function1<com.astro5star.app.data.model.Astrologer, kotlin.Unit> onAudioClick = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.jvm.functions.Function1<com.astro5star.app.data.model.Astrologer, kotlin.Unit> onVideoClick = null;
    
    public AstrologerAdapter(@org.jetbrains.annotations.NotNull()
    java.util.List<com.astro5star.app.data.model.Astrologer> astrologers, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.astro5star.app.data.model.Astrologer, kotlin.Unit> onChatClick, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.astro5star.app.data.model.Astrologer, kotlin.Unit> onAudioClick, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.astro5star.app.data.model.Astrologer, kotlin.Unit> onVideoClick) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public com.astro5star.app.ui.home.AstrologerAdapter.ViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @java.lang.Override()
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
    com.astro5star.app.ui.home.AstrologerAdapter.ViewHolder holder, int position) {
    }
    
    @java.lang.Override()
    public int getItemCount() {
        return 0;
    }
    
    public final void updateList(@org.jetbrains.annotations.NotNull()
    java.util.List<com.astro5star.app.data.model.Astrologer> newList) {
    }
    
    public final void updateAstrologerStatus(@org.jetbrains.annotations.NotNull()
    java.lang.String userId, boolean isOnline) {
    }
    
    @kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0013\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\n\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\r\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0011\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0010R\u0011\u0010\u0013\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0010R\u0013\u0010\u0015\u001a\u0004\u0018\u00010\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0010R\u0011\u0010\u0017\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0010R\u0011\u0010\u0019\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0010R\u0011\u0010\u001b\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0010R\u0011\u0010\u001d\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\fR\u0011\u0010\u001f\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\f\u00a8\u0006!"}, d2 = {"Lcom/astro5star/app/ui/home/AstrologerAdapter$ViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "view", "Landroid/view/View;", "<init>", "(Landroid/view/View;)V", "imgAstrologer", "Landroid/widget/ImageView;", "getImgAstrologer", "()Landroid/widget/ImageView;", "statusIndicator", "getStatusIndicator", "()Landroid/view/View;", "tvName", "Landroid/widget/TextView;", "getTvName", "()Landroid/widget/TextView;", "tvVerified", "getTvVerified", "tvSkills", "getTvSkills", "tvLanguage", "getTvLanguage", "tvExperience", "getTvExperience", "tvPrice", "getTvPrice", "tvOrders", "getTvOrders", "btnCallAction", "getBtnCallAction", "btnVideoAction", "getBtnVideoAction", "app_release"})
    public static final class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        @org.jetbrains.annotations.NotNull()
        private final android.widget.ImageView imgAstrologer = null;
        @org.jetbrains.annotations.NotNull()
        private final android.view.View statusIndicator = null;
        @org.jetbrains.annotations.NotNull()
        private final android.widget.TextView tvName = null;
        @org.jetbrains.annotations.NotNull()
        private final android.widget.TextView tvVerified = null;
        @org.jetbrains.annotations.NotNull()
        private final android.widget.TextView tvSkills = null;
        @org.jetbrains.annotations.Nullable()
        private final android.widget.TextView tvLanguage = null;
        @org.jetbrains.annotations.NotNull()
        private final android.widget.TextView tvExperience = null;
        @org.jetbrains.annotations.NotNull()
        private final android.widget.TextView tvPrice = null;
        @org.jetbrains.annotations.NotNull()
        private final android.widget.TextView tvOrders = null;
        @org.jetbrains.annotations.NotNull()
        private final android.view.View btnCallAction = null;
        @org.jetbrains.annotations.NotNull()
        private final android.view.View btnVideoAction = null;
        
        public ViewHolder(@org.jetbrains.annotations.NotNull()
        android.view.View view) {
            super(null);
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.widget.ImageView getImgAstrologer() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.view.View getStatusIndicator() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.widget.TextView getTvName() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.widget.TextView getTvVerified() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.widget.TextView getTvSkills() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final android.widget.TextView getTvLanguage() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.widget.TextView getTvExperience() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.widget.TextView getTvPrice() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.widget.TextView getTvOrders() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.view.View getBtnCallAction() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.view.View getBtnVideoAction() {
            return null;
        }
    }
}