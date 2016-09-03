package ru.android_studio.night_meet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
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
import ru.android_studio.night_meet.login.VkLoginActivity;
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
    private ImageView photo;

    private VkAPI vkAPI;
    private RequestType requestType = RequestType.SKIP;
    private NightMeetAPI nightMeetAPI;
    private String userId;
    private Queue<String> userQueue = new LinkedList<>();
    private String relationUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        init();

        userId = getIntent().getStringExtra(ConfigParam.USER_ID);
        if(userId == null) {
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            userId = sharedPref.getString(ConfigParam.USER_ID, "");
        }
        Log.i(TAG, "My userId: " + userId);

        Call<Result> call = nightMeetAPI.getUsers(userId);
        call.enqueue(new CallbackResult());

        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextMeet(RequestType.SKIP);
            }
        });

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextMeet(RequestType.LOVE);
            }
        });


        findViewById(R.id.like).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                like();
            }
        });

        Toolbar topToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);
        topToolBar.setLogo(R.mipmap.ic_launcher);
        topToolBar.setLogoDescription(getResources().getString(R.string.app_name));
    }

    private void like() {
        int relationType = RelationType.LIKE.ordinal() + 1;
        Log.i(TAG, "like userId=" + userId + "&relationUserId=" + relationUserId + "&relationType=" + relationType);

        Call<Result> resultCall = nightMeetAPI.changeStatus(userId, relationUserId, relationType);
        resultCall.enqueue(new NightMeetLikeCallback());

        nextMeet(RequestType.LOVE);
    }

    private void emptyResult() {
        ImageView photo = (ImageView) findViewById(R.id.photo);
        photo.setImageResource(R.drawable.noresults);

        findViewById(R.id.next).setVisibility(View.GONE);
        findViewById(R.id.like).setVisibility(View.GONE);
        findViewById(R.id.back).setVisibility(View.GONE);
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private class CallbackResult implements Callback<Result> {

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

        }
    }
}
