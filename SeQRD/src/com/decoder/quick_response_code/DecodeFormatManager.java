
package com.decoder.quick_response_code;

import java.util.Collection;
import java.util.EnumSet;

import com.google.zxing.BarcodeFormat;


final class DecodeFormatManager {

    private DecodeFormatManager() {
    };

    static final Collection<BarcodeFormat> PRODUCT_FORMATS;
    static final Collection<BarcodeFormat> ONE_D_FORMATS;
    static final Collection<BarcodeFormat> QR_CODE_FORMATS = EnumSet.of(BarcodeFormat.QR_CODE);
    static final Collection<BarcodeFormat> DATA_MATRIX_FORMATS = EnumSet.of(BarcodeFormat.DATA_MATRIX);
    static {
        PRODUCT_FORMATS = EnumSet.of(BarcodeFormat.UPC_A, BarcodeFormat.UPC_E, BarcodeFormat.EAN_13, BarcodeFormat.EAN_8, BarcodeFormat.RSS_14);
        ONE_D_FORMATS = EnumSet.of(BarcodeFormat.CODE_39, BarcodeFormat.CODE_93, BarcodeFormat.CODE_128, BarcodeFormat.ITF);
        ONE_D_FORMATS.addAll(PRODUCT_FORMATS);
    }
}
