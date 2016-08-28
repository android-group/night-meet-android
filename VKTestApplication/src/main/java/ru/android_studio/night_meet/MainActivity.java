package ru.android_studio.night_meet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.ProfilePictureView;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import ru.android_studio.night_meet.retrofit.api.VkAPI;
import ru.android_studio.night_meet.retrofit.model.User;
import ru.android_studio.night_meet.retrofit.model.Users;

public class MainActivity extends AppCompatActivity implements Callback<Users> {

    private static final String TAG = "MainActivity";
    private ImageView photo;
    private VkAPI vkAPI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        /*profilePictureView = (ProfilePictureView) findViewById(R.id.profilePicture);
        greeting = (TextView) findViewById(R.id.greeting);

        postStatusUpdateButton = (Button) findViewById(R.id.postStatusUpdateButton);
        postStatusUpdateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onClickPostStatusUpdate();
            }
        });

        postPhotoButton = (Button) findViewById(R.id.postPhotoButton);
        postPhotoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onClickPostPhoto();
            }
        });*/
        init();
        try {
            nextMeet(210700286);
        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage());
            e.printStackTrace();
        }

        Toolbar toolbar = (Toolbar) findViewById(ru.android_studio.night_meet.R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton next = (FloatingActionButton) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    nextMeet(9917403);
                } catch (IOException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                    e.printStackTrace();
                }
                Snackbar.make(view, "next", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });


        FloatingActionButton like = (FloatingActionButton) findViewById(R.id.like);
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    nextMeet(9917403);
                } catch (IOException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                    e.printStackTrace();
                }
                Snackbar.make(view, "Like", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
    }

    private void init() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.vk.com/method/")
                .addConverterFactory(JacksonConverterFactory.create()) // конвертер JSON
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) //
                .build();

        vkAPI = retrofit.create(VkAPI.class);

        photo = (ImageView) findViewById(R.id.photo);
    }

    private void nextMeet(int accountId) throws IOException {
        Call<Users> call = vkAPI.getUsers(accountId, "photo_max_orig");
        call.enqueue(this);
    }

    public void onResponse(Call<Users> call, Response<Users> response) {
        User user = response.body().getUser().get(0);
        String url = user.getPhotoMaxOrig();
        if (photo != null) {
            Log.i(TAG, "photo view is found");
            ImageLoader.loadByUrlToImageView(this, url, photo);
        }
    }

    @Override
    public void onFailure(Call<Users> call, Throwable t) {
        Log.e(TAG, t.getLocalizedMessage());
        t.printStackTrace();
    }


}
