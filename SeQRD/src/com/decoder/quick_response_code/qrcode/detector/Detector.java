
package com.decoder.quick_response_code.qrcode.detector;

import java.util.Map;

import com.decoder.quick_response_code.qrcode.decoder.Version;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.DetectorResult;
import com.google.zxing.common.GridSampler;
import com.google.zxing.common.PerspectiveTransform;

public class Detector {

    private final BitMatrix image;
    private ResultPointCallback resultPointCallback;

    public Detector(BitMatrix image) {
        this.image = image;
    }

    protected BitMatrix getImage() {
        return image;
    }

    protected ResultPointCallback getResultPointCallback() {
        return resultPointCallback;
    }

    public DetectorResult detect() throws NotFoundException, FormatException {
        return detect(null);
    }

    public DetectorResult detect(Map<DecodeHintType, ?> hints) throws NotFoundException, FormatException {

        resultPointCallback = hints == null ? null : (ResultPointCallback) hints.get(DecodeHintType.NEED_RESULT_POINT_CALLBACK);

        FinderPatternFinder finder = new FinderPatternFinder(image, resultPointCallback);
        FinderPatternInfo info = finder.find(hints);

        return processFinderPatternInfo(info);
    }

    protected DetectorResult processFinderPatternInfo(FinderPatternInfo info) throws NotFoundException, FormatException {

        FinderPattern topLeft = info.getTopLeft();
        FinderPattern topRight = info.getTopRight();
        FinderPattern bottomLeft = info.getBottomLeft();

        float moduleSize = calculateModuleSize(topLeft, topRight, bottomLeft);
        if (moduleSize < 1.0f) {
            throw NotFoundException.getNotFoundInstance();
        }
        int dimension = computeDimension(topLeft, topRight, bottomLeft, moduleSize);
        Version provisionalVersion = Version.getProvisionalVersionForDimension(dimension);
        int modulesBetweenFPCenters = provisionalVersion.getDimensionForVersion() - 7;

        AlignmentPattern alignmentPattern = null;

        if (provisionalVersion.getAlignmentPatternCenters().length > 0) {

            float bottomRightX = topRight.getX() - topLeft.getX() + bottomLeft.getX();
            float bottomRightY = topRight.getY() - topLeft.getY() + bottomLeft.getY();

            float correctionToTopLeft = 1.0f - 3.0f / (float) modulesBetweenFPCenters;
            int estAlignmentX = (int) (topLeft.getX() + correctionToTopLeft * (bottomRightX - topLeft.getX()));
            int estAlignmentY = (int) (topLeft.getY() + correctionToTopLeft * (bottomRightY - topLeft.getY()));

            for (int i = 4; i <= 16; i <<= 1) {
                try {
                    alignmentPattern = findAlignmentInRegion(moduleSize, estAlignmentX, estAlignmentY, (float) i);
                    break;
                } catch (NotFoundException re) {
                }
            }

        }

        PerspectiveTransform transform = createTransform(topLeft, topRight, bottomLeft, alignmentPattern, dimension);

        BitMatrix bits = sampleGrid(image, transform, dimension);

        ResultPoint[] points;
        if (alignmentPattern == null) {
            points = new ResultPoint[] { bottomLeft, topLeft, topRight };
        } else {
            points = new ResultPoint[] { bottomLeft, topLeft, topRight, alignmentPattern };
        }
        return new DetectorResult(bits, points);
    }

    public static PerspectiveTransform createTransform(ResultPoint topLeft, ResultPoint topRight, ResultPoint bottomLeft, ResultPoint alignmentPattern,
            int dimension) {
        float dimMinusThree = (float) dimension - 3.5f;
        float bottomRightX;
        float bottomRightY;
        float sourceBottomRightX;
        float sourceBottomRightY;
        if (alignmentPattern != null) {
            bottomRightX = alignmentPattern.getX();
            bottomRightY = alignmentPattern.getY();
            sourceBottomRightX = sourceBottomRightY = dimMinusThree - 3.0f;
        } else {
            bottomRightX = (topRight.getX() - topLeft.getX()) + bottomLeft.getX();
            bottomRightY = (topRight.getY() - topLeft.getY()) + bottomLeft.getY();
            sourceBottomRightX = sourceBottomRightY = dimMinusThree;
        }

        return PerspectiveTransform.quadrilateralToQuadrilateral(3.5f, 3.5f, dimMinusThree, 3.5f, sourceBottomRightX, sourceBottomRightY, 3.5f, dimMinusThree,
                topLeft.getX(), topLeft.getY(), topRight.getX(), topRight.getY(), bottomRightX, bottomRightY, bottomLeft.getX(), bottomLeft.getY());
    }

    private static BitMatrix sampleGrid(BitMatrix image, PerspectiveTransform transform, int dimension) throws NotFoundException {

        GridSampler sampler = GridSampler.getInstance();
        return sampler.sampleGrid(image, dimension, dimension, transform);
    }

    protected static int computeDimension(ResultPoint topLeft, ResultPoint topRight, ResultPoint bottomLeft, float moduleSize) throws NotFoundException {
        int tltrCentersDimension = round(ResultPoint.distance(topLeft, topRight) / moduleSize);
        int tlblCentersDimension = round(ResultPoint.distance(topLeft, bottomLeft) / moduleSize);
        int dimension = ((tltrCentersDimension + tlblCentersDimension) >> 1) + 7;
        switch (dimension & 0x03) { 
            case 0:
                dimension++;
                break;
            case 2:
                dimension--;
                break;
            case 3:
                throw NotFoundException.getNotFoundInstance();
        }
        return dimension;
    }

    protected float calculateModuleSize(ResultPoint topLeft, ResultPoint topRight, ResultPoint bottomLeft) {
        return (calculateModuleSizeOneWay(topLeft, topRight) + calculateModuleSizeOneWay(topLeft, bottomLeft)) / 2.0f;
    }

    private float calculateModuleSizeOneWay(ResultPoint pattern, ResultPoint otherPattern) {
        float moduleSizeEst1 = sizeOfBlackWhiteBlackRunBothWays((int) pattern.getX(), (int) pattern.getY(), (int) otherPattern.getX(),
                (int) otherPattern.getY());
        float moduleSizeEst2 = sizeOfBlackWhiteBlackRunBothWays((int) otherPattern.getX(), (int) otherPattern.getY(), (int) pattern.getX(),
                (int) pattern.getY());
        if (Float.isNaN(moduleSizeEst1)) {
            return moduleSizeEst2 / 7.0f;
        }
        if (Float.isNaN(moduleSizeEst2)) {
            return moduleSizeEst1 / 7.0f;
        }
        return (moduleSizeEst1 + moduleSizeEst2) / 14.0f;
    }

    private float sizeOfBlackWhiteBlackRunBothWays(int fromX, int fromY, int toX, int toY) {

        float result = sizeOfBlackWhiteBlackRun(fromX, fromY, toX, toY);

        float scale = 1.0f;
        int otherToX = fromX - (toX - fromX);
        if (otherToX < 0) {
            scale = (float) fromX / (float) (fromX - otherToX);
            otherToX = 0;
        } else if (otherToX >= image.getWidth()) {
            scale = (float) (image.getWidth() - 1 - fromX) / (float) (otherToX - fromX);
            otherToX = image.getWidth() - 1;
        }
        int otherToY = (int) (fromY - (toY - fromY) * scale);

        scale = 1.0f;
        if (otherToY < 0) {
            scale = (float) fromY / (float) (fromY - otherToY);
            otherToY = 0;
        } else if (otherToY >= image.getHeight()) {
            scale = (float) (image.getHeight() - 1 - fromY) / (float) (otherToY - fromY);
            otherToY = image.getHeight() - 1;
        }
        otherToX = (int) (fromX + (otherToX - fromX) * scale);

        result += sizeOfBlackWhiteBlackRun(fromX, fromY, otherToX, otherToY);

        return result - 1.0f;
    }

    private float sizeOfBlackWhiteBlackRun(int fromX, int fromY, int toX, int toY) {

        boolean steep = Math.abs(toY - fromY) > Math.abs(toX - fromX);
        if (steep) {
            int temp = fromX;
            fromX = fromY;
            fromY = temp;
            temp = toX;
            toX = toY;
            toY = temp;
        }

        int dx = Math.abs(toX - fromX);
        int dy = Math.abs(toY - fromY);
        int error = -dx >> 1;
        int xstep = fromX < toX ? 1 : -1;
        int ystep = fromY < toY ? 1 : -1;

        int state = 0;

        int xLimit = toX + xstep;
        for (int x = fromX, y = fromY; x != xLimit; x += xstep) {
            int realX = steep ? y : x;
            int realY = steep ? x : y;

            if ((state == 1) == image.get(realX, realY)) {
                if (state == 2) {
                    int diffX = x - fromX;
                    int diffY = y - fromY;
                    return (float) Math.sqrt((double) (diffX * diffX + diffY * diffY));
                }
                state++;
            }

            error += dy;
            if (error > 0) {
                if (y == toY) {
                    break;
                }
                y += ystep;
                error -= dx;
            }
        }

        if (state == 2) {
            int diffX = toX + xstep - fromX;
            int diffY = toY - fromY;
            return (float) Math.sqrt((double) (diffX * diffX + diffY * diffY));
        }

        return Float.NaN;
    }

    protected AlignmentPattern findAlignmentInRegion(float overallEstModuleSize, int estAlignmentX, int estAlignmentY, float allowanceFactor)
            throws NotFoundException {

        int allowance = (int) (allowanceFactor * overallEstModuleSize);
        int alignmentAreaLeftX = Math.max(0, estAlignmentX - allowance);
        int alignmentAreaRightX = Math.min(image.getWidth() - 1, estAlignmentX + allowance);
        if (alignmentAreaRightX - alignmentAreaLeftX < overallEstModuleSize * 3) {
            throw NotFoundException.getNotFoundInstance();
        }

        int alignmentAreaTopY = Math.max(0, estAlignmentY - allowance);
        int alignmentAreaBottomY = Math.min(image.getHeight() - 1, estAlignmentY + allowance);
        if (alignmentAreaBottomY - alignmentAreaTopY < overallEstModuleSize * 3) {
            throw NotFoundException.getNotFoundInstance();
        }

        AlignmentPatternFinder alignmentFinder = new AlignmentPatternFinder(image, alignmentAreaLeftX, alignmentAreaTopY, alignmentAreaRightX
                - alignmentAreaLeftX, alignmentAreaBottomY - alignmentAreaTopY, overallEstModuleSize, resultPointCallback);
        return alignmentFinder.find();
    }

    private static int round(float d) {
        return (int) (d + 0.5f);
    }
}
