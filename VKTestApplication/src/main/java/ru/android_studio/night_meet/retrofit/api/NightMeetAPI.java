package ru.android_studio.night_meet.retrofit.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import ru.android_studio.night_meet.retrofit.model.Result;
import ru.android_studio.night_meet.retrofit.model.Sympathy;

/**
 * Взаимодействие с сервером night meet
 */
public interface NightMeetAPI {

    /**
     * Добавление пользователя
     * POST: account?user_id=<текущий пользователь>
     *
     * @param userId - идентификатор текущего пользователя.
     * @return exempli gratia: { result:"ok" }
     */
    @POST("account")
    Call<Result> postAccount(@Query("user_id") int userId);

    /**
     * поиск пользователя
     * GET: search?user_id=<текущий пользователь>&count=<количество>
     *
     * @param userId - идентификатор текущего пользователя.
     * @param count - колличество пользователей.
     *
     * @return exempli gratia: { users:[id,id,id] }
     */
    @GET("search")
    Call<Sympathy> search(@Query("user_id") int userId, @Query("id") int count);

    /**
     * нажатие кнопки
     * POST: submit?user_id=<текущий пользователь>&relationship_type=<1/2>
     * * relationship_type:
     * 1 - love
     * 2 - hate
     *
     * @param userId - идентификатор текущего пользователя.
     * @param relationshipType - тип отношения
     *
     * @return exempli gratia: { result:"ok" }
     */
    @POST("submit")
    Call<Result> submit(@Query("user_id") int userId, @Query("relationship_type") int relationshipType);


    /**
     * получить симпатии
     * GET: sympathy?user_id=<текущий пользователь>&is_viewed=<true/false>
     *
     * @param userId - идентификатор текущего пользователя.
     * @param isViewed - просмотрел пользователя или нет
     *
     * @return exempli gratia: { users:[id,id,id] }
     */
    @GET("sympathy")
    Call<Sympathy> searchSympathy(@Query("user_id") int userId, @Query("is_viewed") boolean isViewed);


    /**
     * изменение статуса
     * PUT: sympathy?user_id=<текущий пользователь>&is_viewed=<true/false>
     *
     * @param userId - идентификатор текущего пользователя.
     * @param isViewed - просмотрел пользователя или нет
     *
     * @return exempli gratia: { result:"ok" }
     */
    @PUT("sympathy")
    Call<Result> putSympathy(@Query("user_id") int userId, @Query("is_viewed") boolean isViewed);

}
