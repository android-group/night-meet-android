//
//  Copyright (c) 2014 VK.com
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy of
//  this software and associated documentation files (the "Software"), to deal in
//  the Software without restriction, including without limitation the rights to
//  use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
//  the Software, and to permit persons to whom the Software is furnished to do so,
//  subject to the following conditions:
//
//  The above copyright notice and this permission notice shall be included in all
//  copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
//  FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
//  COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
//  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
//  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//

package ru.android_studio.sdk.api.model;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Array of Post
 * Created by alex_xpert on 29.01.14.
 */
public class VKPostArray extends VKList<VKApiPost> {
    @Override
    public VKApiModel parse(JSONObject response) throws JSONException {
        fill(response, VKApiPost.class);
        return this;
    }

    @SuppressWarnings("unused")
    public VKPostArray() {
    }

    public VKPostArray(Parcel in) {
        super(in);
    }

    public static Creator<VKPostArray> CREATOR = new Creator<VKPostArray>() {
        public VKPostArray createFromParcel(Parcel source) {
            return new VKPostArray(source);
        }

        public VKPostArray[] newArray(int size) {
            return new VKPostArray[size];
        }
    };
}
