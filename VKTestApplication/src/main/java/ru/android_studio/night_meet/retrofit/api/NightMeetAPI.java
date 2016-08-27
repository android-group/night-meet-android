package ru.android_studio.night_meet.retrofit.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.android_studio.night_meet.retrofit.model.Example;

/**
 * Взаимодействие с сервером night meet
 */
public interface NightMeetAPI {

    @GET("users.get")
    Call<Example> listUsers(@Query("user_ids") int user, @Query("fields") String fields);
}
