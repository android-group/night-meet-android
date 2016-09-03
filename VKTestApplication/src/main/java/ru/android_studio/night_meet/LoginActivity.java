package ru.android_studio.night_meet;

import android.os.Bundle;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import ru.android_studio.night_meet.login.VkLoginActivity;
import ru.android_studio.night_meet.retrofit.api.NightMeetAPI;


/**
 * Activity which displays a login screen to the user, offering registration as well.
 */
public class LoginActivity extends VkLoginActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}