package ru.android_studio.night_meet;

import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.concurrent.atomic.AtomicBoolean;

/*
* Класс отвечающий за загрузку изображений
* Использую 2 библиотеки
 * Picasso - для загрузки изображений с сервера
 * OkHttpClient - кэш
 * Если не нашли изображения в кэше, тогда скачиваем их с сервера
* */
public class ImageLoader {
    public static void loadByUrlToImageView(final String url, final ImageView imageView, boolean isLiked) {
        if (isLiked) {
            loadByUrlToImageView(url, imageView, R.drawable.like_red);
        } else {
            loadByUrlToImageView(url, imageView, R.drawable.like_white);
        }
    }

    public static void loadByUrlToImageView(final String url, final ImageView imageView) {
        loadByUrlToImageView(url, imageView, false);
    }

    public static void loadByUrlToImageView(final String url, final ImageView imageView, final int resDrawable) {
        final AtomicBoolean playAnimation = new AtomicBoolean(true);
        final GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setColor(imageView.getContext().getResources().getColor(R.color.com_facebook_button_background_color));

        Picasso.with(imageView.getContext())
                .load(url)
                .placeholder(R.drawable.progress_animation)
                .transform(new CircleTransform())
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(imageView, new Callback() {

                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                //Try again online if cache failed
                                Picasso.with(imageView.getContext())
                                        .load(url)
                                        .transform(new CircleTransform())
                                        .placeholder(R.drawable.progress_animation)
                                        .into(imageView, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                Log.d("Load img from url:", url);
                                            }

                                            @Override
                                            public void onError() {
                                                Log.v("Picasso", "Could not fetch image");
                                            }
                                        });
                            }
                        }

                );

        playAnimation.set(false);
    }
}
