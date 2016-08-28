package ru.android_studio.night_meet.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

import ru.android_studio.night_meet.MainActivity;
import ru.android_studio.night_meet.R;
import ru.android_studio.night_meet.config.OdnoklassnikiConfig;
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
public class VkLoginActivity extends OkLoginActivity {

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

    private boolean isResumed = false;
    static CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

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
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private static LoginButton loginButton;

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
    }

    public static class LoginFragment extends android.support.v4.app.Fragment {

        public LoginFragment() {
            super();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_login, container, false);

            initVk(v);
            initOk(v);
            initFacebook(v);

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

        private void initOk(View v) {
            View.OnClickListener okOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //вызываем запрос авторизации. После OAuth будет вызван callback, определенный для объекта
                    //request authorization
                    mOdnoklassniki.requestAuthorization(getContext(), false, OkScope.VALUABLE_ACCESS);
                }
            };

            v.findViewById(R.id.ok).setOnClickListener(okOnClickListener);
        }

        private void initFacebook(View v) {
            loginButton = (LoginButton) v.findViewById(R.id.login_button);
            // "publish_actions"
            loginButton.setReadPermissions(Arrays.asList("user_status", "public_profile", "email"));
            // If using in a fragment
            loginButton.setFragment(this);
            // Other app specific specialization

            // Callback registration
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.d(TAG, "facebook:onSuccess:" + loginResult);
                    //handleFacebookAccessToken(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {
                    Log.d(TAG, "facebook:onCancel");
                }

                @Override
                public void onError(FacebookException exception) {
                    Log.d(TAG, "facebook:onError", exception);
                }
            });
        }
    }
}