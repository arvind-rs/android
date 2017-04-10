
package com.decoder.quick_response_code.qrcode.detector;

import com.google.zxing.ResultPoint;

public final class FinderPattern extends ResultPoint {

    private final float estimatedModuleSize;
    private int count;

    FinderPattern(float posX, float posY, float estimatedModuleSize) {
        this(posX, posY, estimatedModuleSize, 1);
    }

    FinderPattern(float posX, float posY, float estimatedModuleSize, int count) {
        super(posX, posY);
        this.estimatedModuleSize = estimatedModuleSize;
        this.count = count;
    }

    public float getEstimatedModuleSize() {
        return estimatedModuleSize;
    }

    int getCount() {
        return count;
    }

    void incrementCount() {
        this.count++;
    }

    boolean aboutEquals(float moduleSize, float i, float j) {
        if (Math.abs(i - getY()) <= moduleSize && Math.abs(j - getX()) <= moduleSize) {
            float moduleSizeDiff = Math.abs(moduleSize - estimatedModuleSize);
            return moduleSizeDiff <= 1.0f || moduleSizeDiff <= estimatedModuleSize;
        }
        return false;
    }

    FinderPattern combineEstimate(float i, float j, float newModuleSize) {
        int combinedCount = count + 1;
        float combinedX = (count * getX() + j) / combinedCount;
        float combinedY = (count * getY() + i) / combinedCount;
        float combinedModuleSize = (count * estimatedModuleSize + newModuleSize) / combinedCount;
        return new FinderPattern(combinedX, combinedY, combinedModuleSize, combinedCount);
    }

}
