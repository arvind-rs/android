
package com.decoder.quick_response_code.result;

import com.decoder.quick_response_code.R;

import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.URIParsedResult;

import android.app.Activity;

import java.util.Locale;

public final class URIResultHandler extends ResultHandler {

    private static final String[] SECURE_PROTOCOLS = { "otpauth:" };

    public URIResultHandler(Activity activity, ParsedResult result) {
        super(activity, result);
    }

    @Override
    public CharSequence getDisplayContents() {
        URIParsedResult uriResult = (URIParsedResult) getResult();
        String uri = uriResult.getURI().toLowerCase(Locale.ENGLISH);
        StringBuilder contents = new StringBuilder(100);
        ParsedResult.maybeAppend(uri, contents);
        contents.trimToSize();
        return contents.toString();
    }

    @Override
    public int getDisplayTitle() {
        return R.string.result_uri;
    }

    @Override
    public boolean areContentsSecure() {
        URIParsedResult uriResult = (URIParsedResult) getResult();
        String uri = uriResult.getURI().toLowerCase(Locale.ENGLISH);
        for (String secure : SECURE_PROTOCOLS) {
            if (uri.startsWith(secure)) {
                return true;
            }
        }
        return false;
    }
}
