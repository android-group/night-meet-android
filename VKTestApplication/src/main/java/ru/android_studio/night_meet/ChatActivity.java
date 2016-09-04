package ru.android_studio.night_meet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import ru.android_studio.night_meet.retrofit.api.NightMeetAPI;
import ru.android_studio.night_meet.retrofit.api.VkAPI;
import ru.android_studio.night_meet.retrofit.model.ConfigParam;
import ru.android_studio.night_meet.retrofit.model.RelationType;
import ru.android_studio.night_meet.retrofit.model.Result;
import ru.android_studio.night_meet.retrofit.model.User;
import ru.android_studio.night_meet.retrofit.model.Users;

public class ChatActivity extends AppCompatActivity {

    private final static String TAG = "ChatActivity";
    private ArrayList<User> users = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    //private ImageView photo;
    private VkAPI vkAPI;
    private String userId;
    private NightMeetAPI nightMeetAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        emptyView = findViewById(R.id.empty_view);
        userId = getIntent().getStringExtra(ConfigParam.USER_ID);
        Log.i(TAG, "My userId: " + userId);

        Toolbar topToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);
        topToolBar.setLogo(R.mipmap.ic_launcher);
        topToolBar.setLogoDescription(getResources().getString(R.string.app_name));

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        init();

        // specify an adapter (see also next example)
        mAdapter = new ChatAdapter(this, users, nightMeetAPI, userId);
        mRecyclerView.setAdapter(mAdapter);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(itemDecoration);

        Call<Result> relations = nightMeetAPI.getRelations(userId, RelationType.LIKE.getId());
        relations.enqueue(new NightMeetUserCallBack());

        //photo = (ImageView) findViewById(R.id.small_photo);

    }
    View emptyView;

    private void isEmptyList() {

        if (users.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            mRecyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    private void init() {
        Log.i(TAG, "init");
        Retrofit.Builder builder = new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create()) // конвертер JSON
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create());

        vkAPI = builder.baseUrl("https://api.vk.com/method/").build().create(VkAPI.class);
        nightMeetAPI = builder.baseUrl("http://android-studio.ru:8888/api/v1/").build().create(NightMeetAPI.class);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_meet) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private class NightMeetUserCallBack implements retrofit2.Callback<Result> {
        @Override
        public void onResponse(Call<Result> call, Response<Result> response) {
            Log.i(TAG, "onResponse");
            String[] account_ids = response.body().account_ids;
            Log.i(TAG, "account_ids:" + Arrays.toString(account_ids));
            if (account_ids.length == 0) {
                Log.i(TAG, "account_ids is empty");
                return;
            }

            String usersIds = Arrays.toString(account_ids);
            Log.i(TAG, "usersIds: " + usersIds);
            Call<Users> vkCallBack = vkAPI.getUsers(usersIds.substring(1, usersIds.length() - 1), "photo_200");
            vkCallBack.enqueue(new VkUserCallBack());
        }

        @Override
        public void onFailure(Call<Result> call, Throwable t) {
            Log.i(TAG, "NightMeetUserCallBack onFailure");
            Log.e(TAG, "NightMeetUserCallBack onFailure", t);
        }
    }

    private class VkUserCallBack implements retrofit2.Callback<Users> {
        @Override
        public void onResponse(Call<Users> call, Response<Users> response) {
            users.clear();
            users.addAll(response.body().getUser());
            mAdapter.notifyDataSetChanged();
            isEmptyList();
        }

        @Override
        public void onFailure(Call<Users> call, Throwable t) {

        }
    }
}
