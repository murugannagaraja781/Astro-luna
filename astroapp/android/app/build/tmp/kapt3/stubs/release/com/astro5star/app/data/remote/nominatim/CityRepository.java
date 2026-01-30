package com.astro5star.app.data.remote.nominatim;

@kotlin.Metadata(mv = {2, 1, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010%\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u001c\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000e2\u0006\u0010\u0011\u001a\u00020\u0005H\u0086@\u00a2\u0006\u0002\u0010\u0012R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u0006\u001a\u00020\u00078BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\n\u0010\u000b\u001a\u0004\b\b\u0010\tR \u0010\f\u001a\u0014\u0012\u0004\u0012\u00020\u0005\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0\u000e0\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0013"}, d2 = {"Lcom/astro5star/app/data/remote/nominatim/CityRepository;", "", "<init>", "()V", "BASE_URL", "", "api", "Lcom/astro5star/app/data/remote/nominatim/NominatimApi;", "getApi", "()Lcom/astro5star/app/data/remote/nominatim/NominatimApi;", "api$delegate", "Lkotlin/Lazy;", "cache", "", "", "Lcom/astro5star/app/data/remote/nominatim/NominatimResult;", "searchCities", "query", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_release"})
public final class CityRepository {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String BASE_URL = "https://nominatim.openstreetmap.org/";
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.Lazy api$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Map<java.lang.String, java.util.List<com.astro5star.app.data.remote.nominatim.NominatimResult>> cache = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.astro5star.app.data.remote.nominatim.CityRepository INSTANCE = null;
    
    private CityRepository() {
        super();
    }
    
    private final com.astro5star.app.data.remote.nominatim.NominatimApi getApi() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object searchCities(@org.jetbrains.annotations.NotNull()
    java.lang.String query, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.astro5star.app.data.remote.nominatim.NominatimResult>> $completion) {
        return null;
    }
}