package org.embroideryio.embroideryio;

/**
 * Points interface.
 *
 * Returns points as well as x, y, and data values for particular objects, within it.
 *
 */
public interface Points {

    float getX(int index);

    float getY(int index);

    void setLocation(int index, float x, float y);

    int getData(int index);

    int size();

}
