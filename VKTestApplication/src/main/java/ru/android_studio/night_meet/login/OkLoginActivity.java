package ru.android_studio.night_meet.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import ru.android_studio.night_meet.MainActivity;
import ru.android_studio.night_meet.R;
import ru.android_studio.night_meet.config.OdnoklassnikiConfig;
import ru.ok.android.sdk.Odnoklassniki;
import ru.ok.android.sdk.OkTokenRequestListener;


/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class OkLoginActivity extends FragmentActivity {

    protected static Odnoklassniki mOdnoklassniki;

    private OkTokenRequestListener okTokenRequestListener = new OkTokenRequestListener() {
        //если всё хорошо, пользователь зашел в наше приложение
        //if all right, user logged in our app
        @Override
        public void onSuccess(String token) {
            Log.v("APIOK", "Your token: " + token);
            //переходим к другой активити, где будем вызывать методы
            //change activity for api interaction
            startActivity(new Intent(OkLoginActivity.this, MainActivity.class));
            finish();
        }

        //если что-то пошло не так, пользователь не зашел в наше приложение
        //if something goes wrong in authorization
        @Override
        public void onError() {
            Log.v("APIOK", "Error");
            Toast.makeText(getBaseContext(), "Что-то пошло не так", Toast.LENGTH_LONG).show();
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
            Toast.makeText(getBaseContext(), "Что-то пошло не так", Toast.LENGTH_LONG).show();
            // Toast.makeText(this, "Something goes wrong", Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       setContentView(R.layout.activity_start);

        //создаем объект, привязанный к контексту приложения
        //create object which will bound to the application context
        mOdnoklassniki = Odnoklassniki.createInstance(
                getApplicationContext(),
                OdnoklassnikiConfig.APP_ID,
                OdnoklassnikiConfig.APP_SECRET_KEY,
                OdnoklassnikiConfig.APP_PUBLIC_KEY
        );

        //определяем callback на операции с получением токена
        //define callback on authorization
        mOdnoklassniki.setTokenRequestListener(okTokenRequestListener);
    }

    @Override
    protected void onDestroy() {
        //удаляем callback
        //remove callback
        mOdnoklassniki.removeTokenRequestListener();

        super.onDestroy();
    }
}