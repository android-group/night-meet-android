package ru.android_studio.night_meet;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import ru.android_studio.sdk.VKSdk;
import ru.android_studio.sdk.api.VKApi;
import ru.android_studio.sdk.api.VKApiConst;
import ru.android_studio.sdk.api.VKBatchRequest;
import ru.android_studio.sdk.api.VKBatchRequest.VKBatchRequestListener;
import ru.android_studio.sdk.api.VKError;
import ru.android_studio.sdk.api.VKParameters;
import ru.android_studio.sdk.api.VKRequest;
import ru.android_studio.sdk.api.VKRequest.VKRequestListener;
import ru.android_studio.sdk.api.VKResponse;
import ru.android_studio.sdk.api.methods.VKApiCaptcha;
import ru.android_studio.sdk.api.model.VKApiPhoto;
import ru.android_studio.sdk.api.model.VKApiUser;
import ru.android_studio.sdk.api.model.VKAttachments;
import ru.android_studio.sdk.api.model.VKPhotoArray;
import ru.android_studio.sdk.api.model.VKWallPostResult;
import ru.android_studio.sdk.api.photo.VKImageParameters;
import ru.android_studio.sdk.api.photo.VKUploadImage;
import ru.android_studio.sdk.dialogs.VKShareDialog;
import ru.android_studio.sdk.dialogs.VKShareDialogBuilder;
import ru.android_studio.sdk.payments.VKPaymentsCallback;

import org.json.JSONArray;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TestActivity extends ActionBarActivity {

	private static final int[] IDS = {ru.android_studio.night_meet.R.id.users_get, ru.android_studio.night_meet.R.id.friends_get, ru.android_studio.night_meet.R.id.messages_get, ru.android_studio.night_meet.R.id.dialogs_get,
			ru.android_studio.night_meet.R.id.captcha_force, ru.android_studio.night_meet.R.id.upload_photo, ru.android_studio.night_meet.R.id.wall_post, ru.android_studio.night_meet.R.id.wall_getById, ru.android_studio.night_meet.R.id.test_validation,
			ru.android_studio.night_meet.R.id.test_share, ru.android_studio.night_meet.R.id.upload_photo_to_wall, ru.android_studio.night_meet.R.id.upload_doc, ru.android_studio.night_meet.R.id.upload_several_photos_to_wall,
			ru.android_studio.night_meet.R.id.test_send_request};

	public static final int TARGET_GROUP = 60479154;
	public static final int TARGET_ALBUM = 181808365;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(ru.android_studio.night_meet.R.layout.activity_test);
		VKSdk.requestUserState(this, new VKPaymentsCallback() {
			@Override
			public void onUserState(final boolean userIsVk) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
//						Toast.makeText(TestActivity.this, userIsVk ? "user is vk's" : "user is not vk's", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(ru.android_studio.night_meet.R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment implements View.OnClickListener {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View view = inflater.inflate(ru.android_studio.night_meet.R.layout.fragment_test, container, false);
			for (int id : IDS) {
				view.findViewById(id).setOnClickListener(this);
			}
			return view;
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case ru.android_studio.night_meet.R.id.test_send_request: {
					makeRequest();
				}
				break;
				case ru.android_studio.night_meet.R.id.users_get: {
					VKRequest request = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS,
							"id,first_name,last_name,sex,bdate,city,country,photo_50,photo_100," +
									"photo_200_orig,photo_200,photo_400_orig,photo_max,photo_max_orig,online," +
									"online_mobile,lists,domain,has_mobile,contacts,connections,site,education," +
									"universities,schools,can_post,can_see_all_posts,can_see_audio,can_write_private_message," +
									"status,last_seen,common_count,relation,relatives,counters"));
					request.secure = false;
					request.useSystemLanguage = false;
					startApiCall(request);
				}
				break;
				case ru.android_studio.night_meet.R.id.friends_get:
					startApiCall(VKApi.friends().get(VKParameters.from(VKApiConst.FIELDS, "id,first_name,last_name,sex,bdate,city")));
					break;
				case ru.android_studio.night_meet.R.id.messages_get:
					startApiCall(VKApi.messages().get());
					break;
				case ru.android_studio.night_meet.R.id.dialogs_get:
					startApiCall(VKApi.messages().getDialogs());
					break;
				case ru.android_studio.night_meet.R.id.captcha_force:
					startApiCall(new VKApiCaptcha().force());
					break;
				case ru.android_studio.night_meet.R.id.upload_photo: {
					final Bitmap photo = getPhoto();
					VKRequest request = VKApi.uploadAlbumPhotoRequest(new VKUploadImage(photo, VKImageParameters.pngImage()), TARGET_ALBUM, TARGET_GROUP);
					request.executeWithListener(new VKRequestListener() {
						@Override
						public void onComplete(VKResponse response) {
							recycleBitmap(photo);
							VKPhotoArray photoArray = (VKPhotoArray) response.parsedModel;
							Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("https://vk.com/photo-%d_%s", TARGET_GROUP, photoArray.get(0).id)));
							startActivity(i);
						}

						@Override
						public void onError(VKError error) {
							showError(error);
						}
					});
				}
				break;
				case ru.android_studio.night_meet.R.id.wall_post:
					makePost(null, "Hello, friends!");
					break;
				case ru.android_studio.night_meet.R.id.wall_getById:
					startApiCall(VKApi.wall().getById(VKParameters.from(VKApiConst.POSTS, "1_45558")));
					break;
				case ru.android_studio.night_meet.R.id.test_validation:
					startApiCall(new VKRequest("account.testValidation"));
					break;
				case ru.android_studio.night_meet.R.id.test_share: {
					final Bitmap b = getPhoto();
					VKPhotoArray photos = new VKPhotoArray();
					photos.add(new VKApiPhoto("photo-47200925_314622346"));
					new VKShareDialogBuilder()
							.setText("I created this post with VK Android SDK\nSee additional information below\n#vksdk")
							.setUploadedPhotos(photos)
							.setAttachmentImages(new VKUploadImage[]{
									new VKUploadImage(b, VKImageParameters.pngImage())
							})
							.setAttachmentLink("VK Android SDK information", "https://vk.com/dev/android_sdk")
							.setShareDialogListener(new VKShareDialog.VKShareDialogListener() {
								@Override
								public void onVkShareComplete(int postId) {
									recycleBitmap(b);
								}

								@Override
								public void onVkShareCancel() {
									recycleBitmap(b);
								}

								@Override
								public void onVkShareError(VKError error) {
									recycleBitmap(b);
								}
							})
							.show(getFragmentManager(), "VK_SHARE_DIALOG");
				}
				break;
				case ru.android_studio.night_meet.R.id.upload_photo_to_wall: {
					final Bitmap photo = getPhoto();
					VKRequest request = VKApi.uploadWallPhotoRequest(new VKUploadImage(photo, VKImageParameters.jpgImage(0.9f)), 0, TARGET_GROUP);
					request.executeWithListener(new VKRequestListener() {
						@Override
						public void onComplete(VKResponse response) {
							recycleBitmap(photo);
							VKApiPhoto photoModel = ((VKPhotoArray) response.parsedModel).get(0);
							makePost(new VKAttachments(photoModel));
						}

						@Override
						public void onError(VKError error) {
							showError(error);
						}
					});
				}
				break;
				case ru.android_studio.night_meet.R.id.upload_doc:
					startApiCall(VKApi.docs().uploadDocRequest(getFile()));
					break;
				case ru.android_studio.night_meet.R.id.upload_several_photos_to_wall: {
					final Bitmap photo = getPhoto();
					VKRequest request1 = VKApi.uploadWallPhotoRequest(new VKUploadImage(photo, VKImageParameters.jpgImage(0.9f)), 0, TARGET_GROUP);
					VKRequest request2 = VKApi.uploadWallPhotoRequest(new VKUploadImage(photo, VKImageParameters.jpgImage(0.5f)), 0, TARGET_GROUP);
					VKRequest request3 = VKApi.uploadWallPhotoRequest(new VKUploadImage(photo, VKImageParameters.jpgImage(0.1f)), 0, TARGET_GROUP);
					VKRequest request4 = VKApi.uploadWallPhotoRequest(new VKUploadImage(photo, VKImageParameters.pngImage()), 0, TARGET_GROUP);

					VKBatchRequest batch = new VKBatchRequest(request1, request2, request3, request4);
					batch.executeWithListener(new VKBatchRequestListener() {
						@Override
						public void onComplete(VKResponse[] responses) {
							super.onComplete(responses);
							recycleBitmap(photo);
							VKAttachments attachments = new VKAttachments();
							for (VKResponse response : responses) {
								VKApiPhoto photoModel = ((VKPhotoArray) response.parsedModel).get(0);
								attachments.add(photoModel);
							}
							makePost(attachments);
						}

						@Override
						public void onError(VKError error) {
							showError(error);
						}
					});
				}
				break;
			}
		}

		private void startApiCall(VKRequest request) {
			Intent i = new Intent(getActivity(), ApiCallActivity.class);
			i.putExtra("request", request.registerObject());
			startActivity(i);
		}

		private void showError(VKError error) {
			new AlertDialog.Builder(getActivity())
					.setMessage(error.toString())
					.setPositiveButton("OK", null)
					.show();
			if (error.httpError != null) {
				Log.w("Test", "Error in request or upload", error.httpError);
			}
		}

		private Bitmap getPhoto() {
			try {
				return BitmapFactory.decodeStream(getActivity().getAssets().open("android.jpg"));
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}

		private static void recycleBitmap(@Nullable final Bitmap bitmap) {
			if (bitmap != null) {
				bitmap.recycle();
			}
		}

		private File getFile() {
			try {
				InputStream inputStream = getActivity().getAssets().open("android.jpg");
				File file = new File(getActivity().getCacheDir(), "android.jpg");
				OutputStream output = new FileOutputStream(file);
				byte[] buffer = new byte[4 * 1024]; // or other buffer size
				int read;

				while ((read = inputStream.read(buffer)) != -1) {
					output.write(buffer, 0, read);
				}
				output.flush();
				output.close();
				return file;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		private void makePost(VKAttachments attachments) {
			makePost(attachments, null);
		}

		private void makeRequest() {
			VKRequest request = new VKRequest("apps.getFriendsList", VKParameters.from("extended", 1, "type", "request"));
			request.executeWithListener(new VKRequestListener() {
				@Override
				public void onComplete(VKResponse response) {
					final Context context = getContext();
					if (context == null || !isAdded()) {
						return;
					}
					try {
						JSONArray jsonArray = response.json.getJSONObject("response").getJSONArray("items");
						int length = jsonArray.length();
						final VKApiUser[] vkApiUsers = new VKApiUser[length];
						CharSequence[] vkApiUsersNames = new CharSequence[length];
						for (int i = 0; i < length; i++) {
							VKApiUser user = new VKApiUser(jsonArray.getJSONObject(i));
							vkApiUsers[i] = user;
							vkApiUsersNames[i] = user.first_name + " " + user.last_name;
						}
						new AlertDialog.Builder(context)
								.setTitle(ru.android_studio.night_meet.R.string.send_request_title)
								.setItems(vkApiUsersNames, new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										startApiCall(new VKRequest("apps.sendRequest",
												VKParameters.from("user_id", vkApiUsers[which].id, "type", "request")));
									}
								}).create().show();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}

		private void makePost(VKAttachments attachments, String message) {
			VKRequest post = VKApi.wall().post(VKParameters.from(VKApiConst.OWNER_ID, "-" + TARGET_GROUP, VKApiConst.ATTACHMENTS, attachments, VKApiConst.MESSAGE, message));
			post.setModelClass(VKWallPostResult.class);
			post.executeWithListener(new VKRequestListener() {
				@Override
				public void onComplete(VKResponse response) {
					if (isAdded()) {
						VKWallPostResult result = (VKWallPostResult) response.parsedModel;
						Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("https://vk.com/wall-%d_%s", TARGET_GROUP, result.post_id)));
						startActivity(i);
					}
				}

				@Override
				public void onError(VKError error) {
					showError(error.apiError != null ? error.apiError : error);
				}
			});
		}
	}
}