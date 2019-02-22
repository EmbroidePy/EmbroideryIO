package org.embroideryio.embroideryio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Points Direct extension with integer data attached at a 1:1 connection to the points.
 */

class DataPoints extends PointsDirect {
    public int[] data;

    public DataPoints() {
        data = new int[MIN_CAPACITY_INCREMENT];
    }

    public DataPoints(DataPoints p) {
        super(p);
        data = p.datapack();
    }

    @Override
    public void add(float px, float py, int data) {
        super.add(px, py);
        setData(data);
    }

    @Override
    protected void moveArray(int from, int to, int length) {
        super.moveArray(from, to, length);
        System.arraycopy(data, from / 2, data, to / 2, length / 2);
    }

    @Override
    protected void changeCapacity(int newCapacity) {
        super.changeCapacity(newCapacity);
        data = Arrays.copyOf(data, newCapacity / 2);
    }

    @Override
    public int getData(int index) {
        return data[index];
    }

    public void setData(int index, int v) {
        data[index] = v;
    }

    public void setData(int v) {
        data[size() - 1] = v;
    }

    public int[] datapack() {
        return Arrays.copyOf(data, size());
    }


    public void write(OutputStream stream) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4 + (4 * count) + (4 * count / 2));
        //4 bytes int size. 4 bytes for each #count floats. 4 bytes integer for each pair of floats (#count/2) for the data.
        buffer.putInt(count >> 1); //size
        buffer.asFloatBuffer().put(pointlist, 0, count); //points
        buffer.position(4 + 4 * count); //float buffer has independent count and needs to be manually updated to correct position.
        buffer.asIntBuffer().put(data, 0, count / 2); //data;
        stream.write(buffer.array());
    }

    public void read(InputStream in) throws IOException {
        byte[] bytes = new byte[4];
        if (readFully(in, bytes) != bytes.length) return;
        int loadcount = ByteBuffer.wrap(bytes).getInt() << 1;
        bytes = new byte[loadcount * 4]; //4 bytes per float, number of floats.
        if (readFully(in, bytes) != bytes.length) return;
        ensureCapacity(loadcount);
        count = loadcount;
        dirtybounds = true;
        ByteBuffer.wrap(bytes).asFloatBuffer().get(pointlist, 0, count);
        bytes = new byte[loadcount * 2]; //2 = * (4 / 2), 4 bytes per int, #count/2 of ints.
        if (readFully(in, bytes) != bytes.length) return;
        ByteBuffer.wrap(bytes).asIntBuffer().get(data, 0, count / 2);
    }


    public static int readFully(InputStream in, byte[] data) throws IOException {
        int offset = 0;
        int bytesRead;
        boolean read = false;
        while ((bytesRead = in.read(data, offset, data.length - offset)) != -1) {
            read = true;
            offset += bytesRead;
            if (offset >= data.length) {
                break;
            }
        }
        return (read) ? offset : -1;
    }

    public int[] getData() {
        return data;
    }

    public float[] getPointlist() {
        return pointlist;
    }
}
