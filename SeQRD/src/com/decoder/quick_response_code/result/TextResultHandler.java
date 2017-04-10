
package com.decoder.quick_response_code.result;

import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.TextParsedResult;

import com.decoder.quick_response_code.R;

import android.app.Activity;

public final class TextResultHandler extends ResultHandler {

    public TextResultHandler(Activity activity, ParsedResult result, Result rawResult) {
        super(activity, result, rawResult);
    }

    @Override
    public CharSequence getDisplayContents() {
        TextParsedResult result = (TextParsedResult) getResult();
        StringBuilder contents = new StringBuilder(100);
        ParsedResult.maybeAppend(result.getText(), contents);
        contents.trimToSize();
        return contents.toString();
    }

    @Override
    public int getDisplayTitle() {
        return R.string.result_text;
    }
}
