package ru.android_studio.night_meet.retrofit.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ru.android_studio.night_meet.retrofit.model.Result;
import ru.android_studio.night_meet.retrofit.model.Sympathy;

/**
 * Взаимодействие с сервером night meet
 */
public interface NightMeetAPI {
    @POST("account/{id}")
    Call<Result> login(@Path("id") String userId);

    @GET("account/{id}/candidates")
    Call<Result> getUsers(@Path("id") String userId);

    @PUT("account/{id}/relations/{other_id}/{type}")
    Call<Result> changeStatus(@Path("id") String userId, @Path("other_id") String relationUserId, @Path("type") int relationType);


    @GET("account/{id}/relations/{type}")
    Call<Result> getRelations(@Path("id") String userId, @Path("type") int relationType);
}
