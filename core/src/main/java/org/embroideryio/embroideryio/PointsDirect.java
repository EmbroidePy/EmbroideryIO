package org.embroideryio.embroideryio;

import java.util.Arrays;

/**
 * Created by Tat on 8/2/2015.
 * <p>
 * Sequential float array backed implementation of Points.
 * Serves as a array of points stored as two sequential floats.
 * <p>
 * Maintains bounds.
 * <p>
 * Derived from proprietary code, 6/21/2016.
 * Released under EmbroiderModder/MobileView licensing. 6/21/2016.
 * Updated 12/6/2017
 * <p>
 * <p/>
 * The core importance of such a class is to allow for speed with regard to Android.
 * The canvas can very quickly render segments and with the system setup as such,
 * A point list can be rendered in two canvas calls, and the underlying memory is
 * maximally compact.
 * <p>
 * <p/>
 * if (count >= 4) {
 * if ((count & 2) != 0) {
 * canvas.drawLines(pointlist, 0, count - 2, paint);
 * canvas.drawLines(pointlist, 2, count - 2, paint);
 * } else {
 * canvas.drawLines(pointlist, 0, count, paint);
 * canvas.drawLines(pointlist, 2, count - 4, paint);
 * }
 * }
 * <p/>
 * This class can easily allow for 50,000+ stitch projects to be run with proper speed on an android device.
 */

class PointsDirect implements Points {
    public static final int MIN_CAPACITY_INCREMENT = 12;
    public static final int INVALID_POINT = -1;

    public float[] pointlist;
    public int count;

    protected float minX;
    protected float minY;
    protected float maxX;
    protected float maxY;
    protected boolean dirtybounds;
    protected boolean excessbounds;

    public PointsDirect() {
        pointlist = new float[MIN_CAPACITY_INCREMENT * 2];

        count = 0;
        resetBounds();
    }

    public PointsDirect(float[] pack) {
        this();
        add(0, pack);
    }

    public PointsDirect(PointsDirect p) {
        pointlist = p.pack();
        count = p.count();
        dirtybounds = true;
    }

    public PointsDirect(PointsDirect p, boolean trueCloneFlag) {
        pointlist = p.pointlist;
        count = p.count;
        dirtybounds = true;
    }

    private static int newCapacity(int currentCapacity) {
        if (currentCapacity < MIN_CAPACITY_INCREMENT) {
            return currentCapacity + MIN_CAPACITY_INCREMENT;
        }
        int newCapacity = currentCapacity + currentCapacity;
        if (newCapacity < 0) {
            newCapacity = Integer.MAX_VALUE;
        }
        return newCapacity;
    }

    protected void moveArray(int from, int to, int length) {
        System.arraycopy(pointlist, from, pointlist, to, length);
    }

    protected void changeCapacity(int newCapacity) {
        pointlist = Arrays.copyOf(pointlist, newCapacity);
    }

    public void ensureCapacity(int capacity) {
        if (pointlist.length > capacity) {
            return;
        }
        changeCapacity(newCapacity(capacity));
    }

    public void add(float px, float py, int data) {
        ensureCapacity(count + 2);
        pointlist[count] = px;
        pointlist[count + 1] = py;
        count += 2;
        checkBounds(px, py);
    }

    public final void add(float px, float py) {
        ensureCapacity(count + 2);
        pointlist[count] = px;
        pointlist[count + 1] = py;
        count += 2;
        checkBounds(px, py);
    }

    public final void add(int index, double px, double py) {
        ensureCapacity(count + 2);
        index <<= 1;
        moveArray(index, index + 2, count - index);
        pointlist[index] = (float) px;
        pointlist[index + 1] = (float) py;
        count += 2;
        checkBounds((float) px, (float) py);
    }

    private void add(int arrayindex, float[] vars, int arraylength) {
        ensureCapacity(count + arraylength);

        if (arrayindex != count) {
            moveArray(arrayindex, arrayindex + arraylength, count - arrayindex);
        }
        System.arraycopy(vars, 0, pointlist, arrayindex, arraylength);
        count += arraylength;
        dirtybounds = true;
    }

    public void add(Points points) {
        ensureCapacity(count + (points.size() << 1));
        for (int i = 0, ie = points.size(); i < ie; i++) {
            add(points.getX(i), points.getY(i), points.getData(i));
        }
        dirtybounds = true;
    }

    public void add(PointsDirect pointList) {
        ensureCapacity(count + pointList.count);
        System.arraycopy(pointList.pointlist, 0, pointlist, count, pointList.count);
        count += pointList.count;
        dirtybounds = true;
    }

    public void add(int index, PointsDirect pointList) {
        add(index << 1, pointList.pointlist, pointList.count);
    }

    public final void addAll(PointsDirect add) {
        add(count, add.pointlist, add.count);
    }

    public final void addAll(int index, PointsDirect add) {
        add(index << 1, add.pointlist, add.count);
    }

    public final void add(int index, float... vars) {
        if (vars == null) {
            return;
        }
        add(index << 1, vars, vars.length);
    }

    public final void add(float[] pts) {
        add(size(), pts);
    }

    public final void remove(int index) {
        float px = getX(index);
        float py = getY(index);
        index <<= 1;
        moveArray(index + 2, index, count - index - 2);
        count -= 2;
        if (isBoundEdge(px, py)) {
            excessbounds = true;
        }
    }

    public final void truncate(int index) {
        index <<= 1;
        count = index;
        excessbounds = true;
    }

    public void clear() {
        count = 0;
        resetBounds();
    }

    @Override
    public final void setLocation(int index, float px, float py) {
        index <<= 1;
        pointlist[index] = px;
        pointlist[index + 1] = py;
        if (isBoundEdge(px, py)) {
            excessbounds = true;
        }
        checkBounds(px, py);
    }

    public final void translateLocation(int index, float dx, float dy) {
        float px = getX(index);
        float py = getY(index);
        index <<= 1;
        if (isBoundEdge(px, py)) {
            excessbounds = true;
        }
        px += dx;
        py += dy;
        pointlist[index] = px;
        pointlist[index + 1] = py;
        checkBounds(px, py);
    }

    @Override
    public int getData(int index) {
        return 0;
    }

    @Override
    public float getX(int index) {
        index <<= 1;
        if (index < 0) {
            return Float.NaN;
        }
        if (index >= count) {
            return Float.NaN;
        }
        return pointlist[index];
    }

    @Override
    public float getY(int index) {
        index <<= 1;
        index++;
        if (index < 0) {
            return Float.NaN;
        }
        if (index >= count) {
            return Float.NaN;
        }
        return pointlist[index];
    }

    @Override
    public final int size() {
        return count >> 1;
    }

    public int count() {
        return count;
    }

    public final boolean isEmpty() {
        return count == 0;
    }

    public float[] pack() {
        return Arrays.copyOf(pointlist, count);
    }

    public float[] subList(int from, int to) {
        return Arrays.copyOfRange(pointlist, from << 1, to << 1);
    }

    public void pack(float[] returnArray, int index, int length) {
        if (count == 0) {
            return;
        }
        if (index > count) {
            return;
        }
        if (index < 0) {
            return;
        }
        index <<= 1;
        length <<= 1;
        int fromPos = index;
        int toPos = 0;
        int remaining = length;
        while (true) {
            int maxAllowed = count - fromPos;
            int len = Math.min(maxAllowed, remaining);
            System.arraycopy(pointlist, fromPos, returnArray, toPos, len);
            remaining -= len;
            if (remaining <= 0) {
                return;
            }
            toPos += len;
            fromPos = (fromPos + len) % count;
            fromPos %= count;
        }
    }

    public void setPack(float[] pack, int count) {
        this.pointlist = pack;
        this.count = count;
        dirtybounds = true;
    }

    public void setPack(float[] pack) {
        this.pointlist = pack;
        this.count = (pack != null) ? pack.length : 0;
        dirtybounds = true;
    }

    public float getMinX() {
        computeBounds(true);
        return minX;
    }

    public float getMaxX() {
        computeBounds(true);
        return maxX;
    }

    public float getMinY() {
        computeBounds(true);
        return minY;
    }

    public float getMaxY() {
        computeBounds(true);
        return maxY;
    }

    public float getWidth() {
        computeBounds(true);
        return maxX - minX;
    }

    public float getHeight() {
        computeBounds(true);
        return maxY - minY;
    }

    public float getCenterX() {
        computeBounds(true);
        return (minX + maxX) / 2;
    }

    public float getCenterY() {
        computeBounds(true);
        return (minY + maxY) / 2;
    }

    protected void computeBounds(boolean exact) {
        if ((dirtybounds) || (excessbounds & exact)) {
            resetBounds();
            for (int i = 0; i < count; i += 2) {
                float px = pointlist[i];
                float py = pointlist[i + 1];
                checkBounds(px, py);
            }
        }
    }

    private boolean isBoundEdge(float px, float py) {
        return (px == minX) || (px == maxX) || (py == minY) || (py == maxY);
    }

    private boolean checkBounds(float px, float py) {
        boolean boundChanged = false;
        if (px < minX) {
            minX = px;
            boundChanged = true;
        }
        if (px > maxX) {
            maxX = px;
            boundChanged = true;
        }
        if (py < minY) {
            minY = py;
            boundChanged = true;
        }
        if (py > maxY) {
            maxY = py;
            boundChanged = true;
        }
        return boundChanged;
    }

    public void resetBounds() {
        minX = Float.POSITIVE_INFINITY;
        minY = Float.POSITIVE_INFINITY;
        maxX = Float.NEGATIVE_INFINITY;
        maxY = Float.NEGATIVE_INFINITY;
        excessbounds = false;
        dirtybounds = false;
    }

//ANDORID:
//    public void transform(Matrix matrix) {
//        matrix.mapPoints(pointlist);
//        dirtybounds = true;
//    }

    public void translate(double dx, double dy) {
        translate((float) dx, (float) dy);
    }

    public void translate(float dx, float dy) {
        for (int i = 0, s = count - 1; i < s; i += 2) {
            pointlist[i] += dx;
            pointlist[i + 1] += dy;
        }
        maxX += dx;
        minX += dx;
        maxY += dy;
        minY += dy;
    }

    public void setNan(int index) {
        float px = getX(index);
        float py = getY(index);
        index <<= 1;
        if (isBoundEdge(px, py)) {
            excessbounds = true;
        }
        pointlist[index] = Float.NaN;
        pointlist[index + 1] = Float.NaN;
    }

}
