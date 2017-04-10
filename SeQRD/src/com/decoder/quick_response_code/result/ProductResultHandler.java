
package com.decoder.quick_response_code.result;

import com.google.zxing.Result;

import com.decoder.quick_response_code.R;

import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ProductParsedResult;

import android.app.Activity;

public final class ProductResultHandler extends ResultHandler {

    public ProductResultHandler(Activity activity, ParsedResult result, Result rawResult) {
        super(activity, result, rawResult);
    }
    @Override
    public CharSequence getDisplayContents() {
        ProductParsedResult result = (ProductParsedResult) getResult();
        StringBuilder contents = new StringBuilder(100);
        ParsedResult.maybeAppend(result.getNormalizedProductID(), contents);
        contents.trimToSize();
        return contents.toString();
    }
    @Override
    public int getDisplayTitle() {
        return R.string.result_product;
    }
}
