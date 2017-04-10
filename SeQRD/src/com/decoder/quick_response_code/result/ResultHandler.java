
package com.decoder.quick_response_code.result;

import com.google.zxing.Result;

import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;

import android.app.Activity;

public abstract class ResultHandler {

    private final ParsedResult result;
    private final Activity activity;
    private final Result rawResult;

    ResultHandler(Activity activity, ParsedResult result) {
        this(activity, result, null);
    }

    ResultHandler(Activity activity, ParsedResult result, Result rawResult) {
        this.result = result;
        this.activity = activity;
        this.rawResult = rawResult;
    }

    public ParsedResult getResult() {
        return result;
    }

    Activity getActivity() {
        return activity;
    }

    public Result getRawResult() {
        return rawResult;
    }
    public boolean areContentsSecure() {
        return false;
    }
    public CharSequence getDisplayContents() {
        String contents = result.getDisplayResult();
        return contents.replace("\r", "");
    }
    public abstract int getDisplayTitle();
    public final ParsedResultType getType() {
        return result.getType();
    }
}
