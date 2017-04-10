
package com.decoder.quick_response_code.result;

import com.decoder.quick_response_code.R;

import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.SMSParsedResult;

import android.app.Activity;
import android.telephony.PhoneNumberUtils;

public final class SMSResultHandler extends ResultHandler {

    public SMSResultHandler(Activity activity, ParsedResult result) {
        super(activity, result);
    }

    @Override
    public CharSequence getDisplayContents() {
        SMSParsedResult smsResult = (SMSParsedResult) getResult();
        StringBuilder contents = new StringBuilder(50);
        String[] rawNumbers = smsResult.getNumbers();
        String[] formattedNumbers = new String[rawNumbers.length];
        for (int i = 0; i < rawNumbers.length; i++) {
            formattedNumbers[i] = PhoneNumberUtils.formatNumber(rawNumbers[i]);
        }
        ParsedResult.maybeAppend(formattedNumbers, contents);
        ParsedResult.maybeAppend(smsResult.getSubject(), contents);
        ParsedResult.maybeAppend(smsResult.getBody(), contents);
        return contents.toString();
    }

    @Override
    public int getDisplayTitle() {
        return R.string.result_sms;
    }
}
