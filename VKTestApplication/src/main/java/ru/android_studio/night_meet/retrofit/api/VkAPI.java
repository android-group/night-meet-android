package ru.android_studio.night_meet.retrofit.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.android_studio.night_meet.retrofit.model.Example;

/**
 * Плучение информации о пользователях из vk.com
 */
public interface VkAPI {

    //https://api.vk.com/method/users.get?user_ids=210700286&fields=photo_max_orig
    @GET("users.get")
    Call<Example> listUsers(@Query("user_ids") int user, @Query("fields") String fields);
}
