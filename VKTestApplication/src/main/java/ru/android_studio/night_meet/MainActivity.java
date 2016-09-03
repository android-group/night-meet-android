package ru.android_studio.night_meet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import ru.android_studio.night_meet.retrofit.api.NightMeetAPI;
import ru.android_studio.night_meet.retrofit.api.VkAPI;
import ru.android_studio.night_meet.retrofit.model.ConfigParam;
import ru.android_studio.night_meet.retrofit.model.RelationType;
import ru.android_studio.night_meet.retrofit.model.Result;
import ru.android_studio.night_meet.retrofit.model.ResultType;
import ru.android_studio.night_meet.retrofit.model.User;
import ru.android_studio.night_meet.retrofit.model.Users;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private VkAPI vkAPI;
    private RequestType requestType = RequestType.SKIP;
    private NightMeetAPI nightMeetAPI;
    private String userId;
    private Queue<String> userQueue = new LinkedList<>();
    private String relationUserId;
    private View next;
    private View like;
    private View back;
    private ImageView photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        init();

        userId = getIntent().getStringExtra(ConfigParam.USER_ID);
        if (userId == null) {
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            userId = sharedPref.getString(ConfigParam.USER_ID, "");
        }
        Log.i(TAG, "My userId: " + userId);;

        next = findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextMeet(RequestType.SKIP);
            }
        });

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextMeet(RequestType.LOVE);
            }
        });


        like = findViewById(R.id.like);
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                like();
            }
        });

        Toolbar topToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);
        topToolBar.setLogo(R.mipmap.ic_launcher);
        topToolBar.setLogoDescription(getResources().getString(R.string.app_name));

        Call<Result> call = nightMeetAPI.getUsers(userId);
        call.enqueue(new InitCallbackResult());

        loadImageMsg();
    }

    private void loadImageMsg() {
        Call<Result> resultCall = nightMeetAPI.getRelations(userId, RelationType.CONNECT.getId());
        resultCall.enqueue(new ImageMsgCallBack());
    }

    private class ImageMsgCallBack implements retrofit2.Callback<Result> {
        @Override
        public void onResponse(Call<Result> call, Response<Result> response) {
            Log.i(TAG, "onResponse");
            String[] account_ids = response.body().account_ids;

            ActionMenuItemView actionMsg = (ActionMenuItemView) findViewById(R.id.action_msg);
            if(actionMsg != null) {
                Log.i(TAG, "account_ids:" + Arrays.toString(account_ids));
                if (account_ids.length == 0) {
                    actionMsg.setIcon(getResources().getDrawable(R.drawable.chat));
                } else {
                    actionMsg.setIcon(getResources().getDrawable(R.drawable.chat_active));
                }
            }
        }

        @Override
        public void onFailure(Call<Result> call, Throwable t) {
            Log.i(TAG, "NightMeetUserCallBack onFailure");
            Log.e(TAG, "NightMeetUserCallBack onFailure", t);
        }
    }

    private void like() {
        Log.i(TAG, "like userId=" + userId + "&relationUserId=" + relationUserId + "&relationType=" + RelationType.LIKE.getId());
        Call<Result> resultCall = nightMeetAPI.changeStatus(userId, relationUserId, RelationType.LIKE.getId());
        resultCall.enqueue(new NightMeetLikeCallback());

        nextMeet(RequestType.LOVE);
    }

    private void emptyResult() {
        photo.setImageResource(R.drawable.noresults);


        next.setVisibility(View.GONE);
        like.setVisibility(View.GONE);
        back.setVisibility(View.GONE);
    }

    private void init() {
        Retrofit.Builder retrofit = new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create()) // конвертер JSON
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create());

        vkAPI = retrofit
                .baseUrl("https://api.vk.com/method/")
                .build()
                .create(VkAPI.class);

        nightMeetAPI = retrofit
                .baseUrl("http://android-studio.ru:8888/api/v1/")
                .build()
                .create(NightMeetAPI.class);

        photo = (ImageView) findViewById(R.id.photo);
    }

    private void nextMeet(RequestType requestType) {
        this.requestType = requestType;
        try {
            nextMeet();
        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    private void nextMeet() throws IOException {
        if (userQueue.size() > 0) {
            relationUserId = userQueue.poll();
            Call<Users> call = vkAPI.getUsers(relationUserId, "photo_max_orig");
            call.enqueue(new CallbackUsers());
        } else {
            emptyResult();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_msg) {
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra(ConfigParam.USER_ID, userId);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private enum RequestType {
        LOVE, SKIP
    }

    private class CallbackUsers implements Callback<Users> {

        public void onResponse(Call<Users> call, Response<Users> response) {
            User user = response.body().getUser().get(0);
            String url = user.getPhotoMaxOrig();
            if (photo != null) {
                Log.i(TAG, "photo view is found");

                boolean isLiked = requestType == RequestType.LOVE;
                ImageLoader.loadByUrlToImageView(url, photo, isLiked);
            }
        }

        @Override
        public void onFailure(Call<Users> call, Throwable t) {
            Log.e(TAG, t.getLocalizedMessage());
            t.printStackTrace();
        }
    }

    private class InitCallbackResult implements Callback<Result> {

        @Override
        public void onResponse(Call<Result> call, Response<Result> response) {
            List<String> collection = Arrays.asList(response.body().account_ids);
            Log.i(TAG, "result collection: " + collection);
            if (collection.isEmpty()) {
                emptyResult();
                return;
            }

            userQueue.addAll(collection);
            nextMeet(RequestType.SKIP);
        }

        @Override
        public void onFailure(Call<Result> call, Throwable t) {
            Log.e(TAG, t.getLocalizedMessage());
            t.printStackTrace();
        }
    }

    private class NightMeetLikeCallback implements Callback<Result> {
        @Override
        public void onResponse(Call<Result> call, Response<Result> response) {
            Result body = response.body();
            if (body == null) {
                Log.i(TAG, "response message: " + response.message());
                return;
            }

            if (ResultType.SUCCESS_RESULT.equals(body.result)) {
                Toast.makeText(getBaseContext(), "like success", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<Result> call, Throwable t) {
            Log.e(TAG, t.getLocalizedMessage());
            t.printStackTrace();
        }
    }
}
