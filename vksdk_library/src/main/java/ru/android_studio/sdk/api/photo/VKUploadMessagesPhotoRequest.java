package ru.android_studio.sdk.api.photo;

import ru.android_studio.sdk.api.VKApi;
import ru.android_studio.sdk.api.VKParameters;
import ru.android_studio.sdk.api.VKRequest;
import ru.android_studio.sdk.util.VKJsonHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class VKUploadMessagesPhotoRequest extends VKUploadPhotoBase {
    private static final long serialVersionUID = 1L;

    public VKUploadMessagesPhotoRequest(File image) {
        super();
        mImages = new File[]{image};
    }

    public VKUploadMessagesPhotoRequest(VKUploadImage image) {
        super();
        mImages = new File[]{image.getTmpFile()};
    }

    @Override
    protected VKRequest getServerRequest() {
        return VKApi.photos().getMessagesUploadServer();
    }

    @Override
    protected VKRequest getSaveRequest(JSONObject response) {
        VKRequest saveRequest;
        try {
            saveRequest = VKApi.photos().saveMessagesPhoto(new VKParameters(VKJsonHelper.toMap(response)));
        } catch (JSONException e) {
            return null;
        }
        return saveRequest;
    }
}
