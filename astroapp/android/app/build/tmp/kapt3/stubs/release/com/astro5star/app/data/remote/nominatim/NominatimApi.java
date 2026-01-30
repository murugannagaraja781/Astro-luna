package com.astro5star.app.data.remote.nominatim;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0005\bf\u0018\u00002\u00020\u0001JP\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0003\u0010\u0007\u001a\u00020\u00062\b\b\u0003\u0010\b\u001a\u00020\t2\b\b\u0003\u0010\n\u001a\u00020\u00062\b\b\u0003\u0010\u000b\u001a\u00020\t2\b\b\u0003\u0010\f\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\r\u00a8\u0006\u000e"}, d2 = {"Lcom/astro5star/app/data/remote/nominatim/NominatimApi;", "", "searchCity", "", "Lcom/astro5star/app/data/remote/nominatim/NominatimResult;", "query", "", "format", "addressDetails", "", "countryCodes", "limit", "userAgent", "(Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;ILjava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_release"})
public abstract interface NominatimApi {
    
    @retrofit2.http.GET(value = "search")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object searchCity(@retrofit2.http.Query(value = "q")
    @org.jetbrains.annotations.NotNull()
    java.lang.String query, @retrofit2.http.Query(value = "format")
    @org.jetbrains.annotations.NotNull()
    java.lang.String format, @retrofit2.http.Query(value = "addressdetails")
    int addressDetails, @retrofit2.http.Query(value = "countrycodes")
    @org.jetbrains.annotations.NotNull()
    java.lang.String countryCodes, @retrofit2.http.Query(value = "limit")
    int limit, @retrofit2.http.Header(value = "User-Agent")
    @org.jetbrains.annotations.NotNull()
    java.lang.String userAgent, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.astro5star.app.data.remote.nominatim.NominatimResult>> $completion);
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
    }
}