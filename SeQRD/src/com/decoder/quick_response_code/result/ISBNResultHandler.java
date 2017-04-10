
package com.decoder.quick_response_code.result;

import com.google.zxing.Result;

import com.decoder.quick_response_code.R;

import com.google.zxing.client.result.ISBNParsedResult;
import com.google.zxing.client.result.ParsedResult;

import android.app.Activity;

public final class ISBNResultHandler extends ResultHandler {

    public ISBNResultHandler(Activity activity, ParsedResult result, Result rawResult) {
        super(activity, result, rawResult);
    }
    @Override
    public CharSequence getDisplayContents() {
        ISBNParsedResult result = (ISBNParsedResult) getResult();
        StringBuilder contents = new StringBuilder(100);
        ParsedResult.maybeAppend(result.getISBN(), contents);
        contents.trimToSize();
        return contents.toString();
    }
    @Override
    public int getDisplayTitle() {
        return R.string.result_isbn;
    }
}
