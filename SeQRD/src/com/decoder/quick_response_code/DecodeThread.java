
package com.decoder.quick_response_code;

import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.decoder.quick_response_code.data.Preferences;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.ResultPointCallback;

final class DecodeThread extends Thread {

    private static final String TAG = DecodeThread.class.getSimpleName();
    public static final String BARCODE_BITMAP = "barcode_bitmap";

    private final IDecoderActivity activity;
    private final Map<DecodeHintType, Object> hints;
    private final CountDownLatch handlerInitLatch;

    private Handler handler;

    DecodeThread(IDecoderActivity activity, Collection<BarcodeFormat> decodeFormats, String characterSet, ResultPointCallback resultPointCallback) {
        this.activity = activity;
        handlerInitLatch = new CountDownLatch(1);
        hints = new EnumMap<DecodeHintType, Object>(DecodeHintType.class);

        // The prefs can't change while the thread is running, so pick them up
        // once here.
        if (decodeFormats == null || decodeFormats.isEmpty()) {
            if (activity instanceof Activity) {
                decodeFormats = EnumSet.noneOf(BarcodeFormat.class);
                if (Preferences.KEY_DECODE_1D) {
                    decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
                }
                if (Preferences.KEY_DECODE_QR) {
                    decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
                }
                if (Preferences.KEY_DECODE_DATA_MATRIX) {
                    decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
                }
            } else {
                Log.e(TAG, "activity is not an Activity, not handling preferences.");
            }
        }
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);

        if (characterSet != null) {
            hints.put(DecodeHintType.CHARACTER_SET, characterSet);
        }
        hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, resultPointCallback);
    }

    Handler getHandler() {
        try {
            handlerInitLatch.await();
        } catch (InterruptedException ie) {
            // continue?
        }
        return handler;
    }

    @Override
    public void run() {
        Looper.prepare();
        handler = new DecodeHandler(activity, hints);
        handlerInitLatch.countDown();
        Looper.loop();
    }
}
