package org.embroideryio.embroideryio;

/**
 * Wrapper of a Points class which itself implements Points.
 * <p>
 * Used for direct subsequence values delegating to the backing implementation.
 *
 * @param <E>
 */

class PointsIndexRange<E extends Points> implements Points {

    public static final int INVALID_POINT = -1;
    protected E list;
    int start = INVALID_POINT;
    int length = 0;

    public PointsIndexRange(E list, int index_start, int length) {
        this.list = list;
        this.start = index_start;
        this.length = length;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public float getX(int index) {
        int idx = (start + index);
        return list.getX(idx);
    }

    @Override
    public float getY(int index) {
        int idx = (start + index);
        return list.getY(idx);
    }

    @Override
    public int getData(int index) {
        int idx = (start + index);
        return list.getData(idx);
    }

    @Override
    public void setLocation(int index, float x, float y) {
        int idx = (start + index);
        list.setLocation(idx, x, y);
    }

    @Override
    public int size() {
        return length;
    }

}
