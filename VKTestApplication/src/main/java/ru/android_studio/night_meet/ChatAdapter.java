package ru.android_studio.night_meet;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;
import ru.android_studio.night_meet.retrofit.api.NightMeetAPI;
import ru.android_studio.night_meet.retrofit.model.RelationType;
import ru.android_studio.night_meet.retrofit.model.Result;
import ru.android_studio.night_meet.retrofit.model.User;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.UserHolder> {
    private static String TAG = "MyRecyclerViewAdapter";
    private static Activity activity;
    private ArrayList<User> userArrayList;
    private static NightMeetAPI nightMeetAPI;
    private static String myUserId;

    public ChatAdapter(Activity activity, ArrayList<User> myDataset, NightMeetAPI nightMeetAPI, String myUserId) {
        userArrayList = myDataset;
        ChatAdapter.activity = activity;
        ChatAdapter.nightMeetAPI = nightMeetAPI;
        ChatAdapter.myUserId = myUserId;
    }

    @Override
    public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recyclerview_item, parent, false);

        UserHolder userHolder = new UserHolder(view);
        return userHolder;
    }

    @Override
    public void onBindViewHolder(UserHolder holder, int position) {
        User user = userArrayList.get(position);
        holder.fio.setText(user.getFirstName() + " " + user.getLastName());
        holder.setUserId(user.getUid());

        String url = user.getPhoto200();
        ImageLoader.loadByUrlToImageView(url, holder.photo);
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public class UserHolder extends RecyclerView.ViewHolder {
        private ImageView photo;
        private TextView fio;
        private Integer userId;

        public UserHolder(View itemView) {
            super(itemView);

            photo = (ImageView) itemView.findViewById(R.id.small_photo);
            fio = (TextView) itemView.findViewById(R.id.fio);

            Log.i(TAG, "Adding Listener");
            View.OnClickListener addListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/id" + userId));
                    activity.startActivity(browserIntent);

                    Log.i(TAG, "Viewed in browser");
                    Log.i(TAG, "userId: " + userId);
                    Log.i(TAG, "myUserId: " + myUserId);
                    Call<Result> resultCall = nightMeetAPI.changeStatus(myUserId, String.valueOf(userId), RelationType.VIEWED.ordinal() + 1);
                    resultCall.enqueue(new ViewedCallBack());
                }
            };

            itemView.findViewById(R.id.add).setOnClickListener(addListener);
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        private class ViewedCallBack implements retrofit2.Callback<Result> {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Log.i(TAG, "Viewed onResponse");
                notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

            }
        }
    }
}