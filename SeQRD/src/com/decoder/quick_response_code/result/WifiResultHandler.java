
package com.decoder.quick_response_code.result;

import android.app.Activity;

import com.decoder.quick_response_code.R;

import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.WifiParsedResult;

public final class WifiResultHandler extends ResultHandler {

    private final Activity parent;

    public WifiResultHandler(Activity activity, ParsedResult result) {
        super(activity, result);
        parent = activity;
    }

    @Override
    public CharSequence getDisplayContents() {
        WifiParsedResult wifiResult = (WifiParsedResult) getResult();
        StringBuilder contents = new StringBuilder(50);
        String wifiLabel = parent.getString(R.string.wifi_ssid_label);
        ParsedResult.maybeAppend(wifiLabel + '\n' + wifiResult.getSsid(), contents);
        String typeLabel = parent.getString(R.string.wifi_type_label);
        ParsedResult.maybeAppend(typeLabel + '\n' + wifiResult.getNetworkEncryption(), contents);
        return contents.toString();
    }

    @Override
    public int getDisplayTitle() {
        return R.string.result_wifi;
    }
}