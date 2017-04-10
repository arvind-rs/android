
package com.decoder.quick_response_code.result;

import com.decoder.quick_response_code.R;
import com.google.zxing.client.result.EmailAddressParsedResult;
import com.google.zxing.client.result.ParsedResult;

import android.app.Activity;

public final class EmailAddressResultHandler extends ResultHandler {

    public EmailAddressResultHandler(Activity activity, ParsedResult result) {
        super(activity, result);
    }
    @Override
    public CharSequence getDisplayContents() {
        EmailAddressParsedResult result = (EmailAddressParsedResult) getResult();
        StringBuilder contents = new StringBuilder(100);
        ParsedResult.maybeAppend(result.getEmailAddress(), contents);
        contents.trimToSize();
        return contents.toString();
    }
    @Override
    public int getDisplayTitle() {
        return R.string.result_email_address;
    }
}
