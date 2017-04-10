
package com.decoder.quick_response_code.qrcode.detector;

import com.google.zxing.ResultPoint;

public final class AlignmentPattern extends ResultPoint {

    private final float estimatedModuleSize;

    AlignmentPattern(float posX, float posY, float estimatedModuleSize) {
        super(posX, posY);
        this.estimatedModuleSize = estimatedModuleSize;
    }

    boolean aboutEquals(float moduleSize, float i, float j) {
        if (Math.abs(i - getY()) <= moduleSize && Math.abs(j - getX()) <= moduleSize) {
            float moduleSizeDiff = Math.abs(moduleSize - estimatedModuleSize);
            return moduleSizeDiff <= 1.0f || moduleSizeDiff <= estimatedModuleSize;
        }
        return false;
    }

    AlignmentPattern combineEstimate(float i, float j, float newModuleSize) {
        float combinedX = (getX() + j) / 2.0f;
        float combinedY = (getY() + i) / 2.0f;
        float combinedModuleSize = (estimatedModuleSize + newModuleSize) / 2.0f;
        return new AlignmentPattern(combinedX, combinedY, combinedModuleSize);
    }

}
