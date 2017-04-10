
package com.decoder.quick_response_code.result;

import com.decoder.quick_response_code.R;

import com.google.zxing.client.result.ParsedResult;

import android.app.Activity;
import android.telephony.PhoneNumberUtils;

public final class TelResultHandler extends ResultHandler {
    public TelResultHandler(Activity activity, ParsedResult result) {
        super(activity, result);
    }
    @Override
    public CharSequence getDisplayContents() {
        String contents = getResult().getDisplayResult();
        contents = contents.replace("\r", "");
        return PhoneNumberUtils.formatNumber(contents);
    }
    @Override
    public int getDisplayTitle() {
        return R.string.result_tel;
    }
}
