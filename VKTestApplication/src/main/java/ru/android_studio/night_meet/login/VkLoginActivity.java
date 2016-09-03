package ru.android_studio.night_meet.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import ru.android_studio.night_meet.MainActivity;
import ru.android_studio.night_meet.R;
import ru.android_studio.night_meet.retrofit.api.NightMeetAPI;
import ru.android_studio.night_meet.retrofit.model.ConfigParam;
import ru.android_studio.night_meet.retrofit.model.Result;
import ru.android_studio.night_meet.retrofit.model.ResultType;
import ru.android_studio.sdk.VKAccessToken;
import ru.android_studio.sdk.VKCallback;
import ru.android_studio.sdk.VKScope;
import ru.android_studio.sdk.VKSdk;
import ru.android_studio.sdk.api.VKError;
import ru.ok.android.sdk.util.OkScope;


/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class VkLoginActivity extends AppCompatActivity {


    private static final String TAG = "VkLoginActivity";
    /**
     * Scope is set of required permissions for your application
     *
     * @see <a href="https://vk.com/dev/permissions">vk.com api permissions documentation</a>
     */
    private static final String[] sMyScope = new String[]{
            VKScope.FRIENDS,
            VKScope.WALL,
            VKScope.PHOTOS,
            VKScope.NOHTTPS,
            VKScope.MESSAGES,
            VKScope.DOCS
    };
    private NightMeetAPI nightMeetAPI;

    private boolean isResumed = false;
    private String userId;

    private void showLogout() {
        updateUserIdSharedPreferences();
        startMainActivity();
    }

    private void init() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create()) // конвертер JSON
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create());

        nightMeetAPI = builder
                .baseUrl("http://android-studio.ru:8888/api/v1/")
                .build()
                .create(NightMeetAPI.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start);

        VKSdk.wakeUpSession(this, new VKCallback<VKSdk.LoginState>() {
            @Override
            public void onResult(VKSdk.LoginState res) {
                if (isResumed) {
                    switch (res) {
                        case LoggedOut:
                            showLogin();
                            break;
                        case LoggedIn:
                            showLogout();
                            break;
                    }
                }
            }

            @Override
            public void onError(VKError error) {

            }
        });
        init();
    }

    private void showLogin() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new LoginFragment())
                .commitAllowingStateLoss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isResumed = true;
        if (VKSdk.isLoggedIn()) {
            showLogout();
        } else {
            showLogin();
        }
    }

    @Override
    protected void onPause() {
        isResumed = false;
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        VKCallback<VKAccessToken> callback = new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                // User passed Authorization
                userId = res.userId;
                Log.i(TAG, "login userId: " + userId);
                addUserIdSharedPreferences(userId);
                Call<Result> resultCall = nightMeetAPI.login(userId);
                resultCall.enqueue(new NightMeetCallbackLogin());


            }

            @Override
            public void onError(VKError error) {
                // User didn't pass Authorization
            }
        };

        if (!VKSdk.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(ConfigParam.USER_ID, userId);
        Log.i(TAG, "VkLoginActivity putExtra userId: " + userId);
        startActivity(intent);
    }

    private void addUserIdSharedPreferences(String userId) {
        Log.i(TAG, "VkLoginActivity addUserIdSharedPreferences");
        Log.i(TAG, "VkLoginActivity USER_ID: " + userId);
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(ConfigParam.USER_ID, userId);
        editor.apply();
    }

    private void updateUserIdSharedPreferences() {
        Log.i(TAG, "VkLoginActivity updateUserIdSharedPreferences");
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        userId = sharedPref.getString(ConfigParam.USER_ID, "");
    }

    public static class LoginFragment extends android.support.v4.app.Fragment {

        public LoginFragment() {
            super();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_login, container, false);

            initVk(v);
            //initOk(v);

            return v;
        }

        private void initVk(View v) {
            View.OnClickListener vkOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VKSdk.login(getActivity(), sMyScope);
                }
            };
            v.findViewById(R.id.vk).setOnClickListener(vkOnClickListener);
        }
    }

    class NightMeetCallbackLogin implements Callback<Result> {

        @Override
        public void onResponse(Call<Result> call, Response<Result> response) {
            Result body = response.body();
            Log.i(TAG, response.toString());
            if (ResultType.SUCCESS_RESULT.equals(body.result)) {
                Log.i(TAG, "Success login");
                startMainActivity();
            }
        }

        @Override
        public void onFailure(Call<Result> call, Throwable t) {

        }
    }
}