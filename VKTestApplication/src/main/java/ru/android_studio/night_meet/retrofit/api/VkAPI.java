package ru.android_studio.night_meet.retrofit.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.android_studio.night_meet.retrofit.model.Users;

/**
 * Плучение информации о пользователях из vk.com
 */
public interface VkAPI {

    /**
     * Возвращает расширенную информацию о пользователях.
     *
     * https://api.vk.com/method/users.get?user_ids=210700286&fields=photo_max_orig
     *
     * @param user - идентификатор текущего пользователя.
     * @param fields - список дополнительных полей профилей, которые необходимо вернуть. <a href="https://vk.com/dev/fields">См. подробное описание.</a>
     *
     * @see  <a href="https://vk.com/dev/users.get">https://vk.com/dev/users.get</a>
     *
     * @return exempli gratia:
     * {
     * response: [{
     *       id: 210700286,
     *       first_name: 'Lindsey',
     *       last_name: 'Stirling',
     *       city: {
     *             id: 5331,
     *             title: 'Los Angeles'
     *      },
     *      photo_50: 'https://pp.vk.me/...f6e/4-funfNRMwg.jpg',
     *      verified: 1
     * }]
     * }
     */
    @GET("users.get")
    Call<Users> getUsers(@Query("user_ids") String user, @Query("fields") String fields);
}
