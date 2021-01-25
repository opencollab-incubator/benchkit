package io.github.lukeeey.skin2server.http;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface APIService {

    @GET("/join-session/{id}")
    Call<Session> joinSession(@Path("id") String id);

    @GET("/session/{id}")
    Call<Session> fetchSession(@Path("id") String id);
}
