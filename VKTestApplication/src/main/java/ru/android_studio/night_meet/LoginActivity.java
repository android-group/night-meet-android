package ru.android_studio.night_meet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import ru.android_studio.sdk.VKAccessToken;
import ru.android_studio.sdk.VKCallback;
import ru.android_studio.sdk.VKScope;
import ru.android_studio.sdk.VKSdk;
import ru.android_studio.sdk.api.VKError;
import ru.ok.android.sdk.Odnoklassniki;
import ru.ok.android.sdk.OkTokenRequestListener;
import ru.ok.android.sdk.util.OkScope;


/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends FragmentActivity implements OkTokenRequestListener, View.OnClickListener  {

    private boolean isResumed = false;

    private Odnoklassniki mOdnoklassniki;

    /**
     * ID приложения
     * Application ID
     */
    private String APP_ID = "1248026368";

    /**
     * Публичный ключ приложения
     * Application public key
     */
    private String APP_PUBLIC_KEY = "CBAQFEGLEBABABABA";

    /**
     * Секретный ключ приложения
     * Application secret key
     */
    private String APP_SECRET_KEY = "B795FF396D5D4848B814605D";


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //создаем объект, привязанный к контексту приложения
        //create object which will bound to the application context
        mOdnoklassniki = Odnoklassniki.createInstance(getApplicationContext(), APP_ID, APP_SECRET_KEY, APP_PUBLIC_KEY);
        //определяем callback на операции с получением токена
        //define callback on authorization
        mOdnoklassniki.setTokenRequestListener(this);

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
                        case Pending:
                            break;
                        case Unknown:
                            break;
                    }
                }
            }

            @Override
            public void onError(VKError error) {

            }
        });

//        String[] fingerprint = VKUtil.getCertificateFingerprint(this, this.getPackageName());
//        Log.d("Fingerprint", fingerprint[0]);
    }

    private void showLogout() {
        startMainActivity();
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

    //если всё хорошо, пользователь зашел в наше приложение
    //if all right, user logged in our app
    @Override
    public void onSuccess(String token) {
        Log.v("APIOK", "Your token: " + token);
        //переходим к другой активити, где будем вызывать методы
        //change activity for api interaction
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    //если что-то пошло не так, пользователь не зашел в наше приложение
    //if something goes wrong in authorization
    @Override
    public void onError() {
        Log.v("APIOK", "Error");
        Toast.makeText(this, "Что-то пошло не так", Toast.LENGTH_LONG).show();
        // Toast.makeText(this, "Something goes wrong", Toast.LENGTH_LONG).show();
    }

    //если что-то пошло не так, пользователь не зашел в наше приложение
    //if something goes wrong in authorization
    @Override
    public void onCancel() {
        //нажал назад
        //press back
        //нажать отменить в вебе
        //press cancel
        Log.v("APIOK", "Cancel");
        Toast.makeText(this, "Что-то пошло не так", Toast.LENGTH_LONG).show();
        // Toast.makeText(this, "Something goes wrong", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        //удаляем callback
        //remove callback
        mOdnoklassniki.removeTokenRequestListener();

        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        VKCallback<VKAccessToken> callback = new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                // User passed Authorization
                startMainActivity();
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
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginButton:
                Log.v("APIOK", "Clicked");

                //вызываем запрос авторизации. После OAuth будет вызван callback, определенный для объекта
                //request authorization
                mOdnoklassniki.requestAuthorization(this, false, OkScope.VALUABLE_ACCESS);

                break;
        }
    }

    public static class LoginFragment extends android.support.v4.app.Fragment {
        public LoginFragment() {
            super();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_login, container, false);

            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VKSdk.login(getActivity(), sMyScope);
                }
            };

            v.findViewById(R.id.vklogo).setOnClickListener(onClickListener);
            return v;
        }

    }

    public static class LogoutFragment extends android.support.v4.app.Fragment {
        public LogoutFragment() {
            super();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_logout, container, false);
            v.findViewById(R.id.continue_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((LoginActivity) getActivity()).startMainActivity();
                }
            });

            v.findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VKSdk.logout();
                    if (!VKSdk.isLoggedIn()) {
                        ((LoginActivity) getActivity()).showLogin();
                    }
                }
            });
            return v;
        }
    }
}