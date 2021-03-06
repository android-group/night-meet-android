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

package ru.android_studio.sdk.api.methods;

import ru.android_studio.sdk.api.VKApiConst;
import ru.android_studio.sdk.api.VKParameters;
import ru.android_studio.sdk.api.VKParser;
import ru.android_studio.sdk.api.VKRequest;
import ru.android_studio.sdk.api.model.VKApiGetDialogResponse;
import ru.android_studio.sdk.api.model.VKApiGetMessagesResponse;

import org.json.JSONObject;

/**
 * Builds requests for API.messages part
 */
public class VKApiMessages extends VKApiBase {
    /**
     * Returns messages current user
     *
     * @return Request for load
     */
    public VKRequest get() {
        return get(VKParameters.from(VKApiConst.COUNT, "10"));
    }

    /**
     * https://vk.com/dev/messages.get
     *
     * @param params use parameters from description with VKApiConst class
     * @return Request for load
     */
    public VKRequest get(VKParameters params) {
        return prepareRequest("get", params, new VKParser() {
            @Override
            public Object createModel(JSONObject object) {
                return new VKApiGetMessagesResponse(object);
            }
        });
    }

    /**
     * Returns dialogs current user
     *
     * @return Request for load
     */
    public VKRequest getDialogs() {
        return getDialogs(VKParameters.from(VKApiConst.COUNT, "5"));
    }

    /**
     * https://vk.com/dev/messages.getDialogs
     *
     * @param params use parameters from description with VKApiConst class
     * @return Request for load
     */
    public VKRequest getDialogs(VKParameters params) {
        return prepareRequest("getDialogs", params, new VKParser() {
            @Override
            public Object createModel(JSONObject object) {
                return new VKApiGetDialogResponse(object);
            }
        });
    }

    @Override
    protected String getMethodsGroup() {
        return "messages";
    }
}