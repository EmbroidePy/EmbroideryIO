package org.embroideryio.embroideryio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Stack;


public class WriteHelper {
    protected OutputStream stream;
    private OutputStream original;
    private Stack<ByteArrayOutputStream> streamStack = new Stack<>();

    int position = 0;

    public WriteHelper() {
        position = 0;
    }

    public WriteHelper(OutputStream stream) {
        this.original = stream;
        this.stream = stream;
    }

    public void setStream(OutputStream stream) {
        this.original = stream;
        this.stream = stream;
    }

    public void space_holder(int skip) {
        position += skip;
        ByteArrayOutputStream push = new ByteArrayOutputStream();
        if (streamStack == null) {
            streamStack = new Stack<>();
        }
        streamStack.push(push);
        stream = push;
    }


    public void writeSpaceHolder32BE(int value) throws IOException {
        ByteArrayOutputStream baos = pop();
        stream.write((value >> 24) & 0xFF);
        stream.write((value >> 16) & 0xFF);
        stream.write((value >> 8) & 0xFF);
        stream.write(value & 0xFF);
        stream.write(baos.toByteArray());
    }
    
    public void writeSpaceHolder32LE(int value) throws IOException {
        ByteArrayOutputStream baos = pop();
        stream.write(value & 0xFF);
        stream.write((value >> 8) & 0xFF);
        stream.write((value >> 16) & 0xFF);
        stream.write((value >> 24) & 0xFF);
        stream.write(baos.toByteArray());
    }

    public void writeSpaceHolder24LE(int value) throws IOException {
        ByteArrayOutputStream baos = pop();
        stream.write(value & 0xFF);
        stream.write((value >> 8) & 0xFF);
        stream.write((value >> 16) & 0xFF);
        stream.write(baos.toByteArray());
    }

    public void writeSpaceHolder16LE(int value) throws IOException {
        ByteArrayOutputStream baos = pop();
        stream.write(value & 0xFF);
        stream.write((value >> 8) & 0xFF);
        stream.write(baos.toByteArray());
    }

    private ByteArrayOutputStream pop() {
        ByteArrayOutputStream pop = streamStack.pop();
        if (streamStack.isEmpty()) {
            stream = original;
        } else {
            stream = streamStack.peek();
        }
        return pop;
    }

    public void writeInt8(int value) throws IOException {
        position += 1;
        stream.write(value);
    }

    public void writeInt16LE(int value) throws IOException {
        position += 2;
        stream.write(value & 0xFF);
        stream.write((value >> 8) & 0xFF);
    }

    public void writeInt16BE(int value) throws IOException {
        position += 2;
        stream.write((value >> 8) & 0xFF);
        stream.write(value & 0xFF);
    }

    public void writeInt24LE(int value) throws IOException {
        position += 3;
        stream.write(value & 0xFF);
        stream.write((value >> 8) & 0xFF);
        stream.write((value >> 16) & 0xFF);
    }

    public void writeInt24BE(int value) throws IOException {
        position += 3;
        stream.write((value >> 16) & 0xFF);
        stream.write((value >> 8) & 0xFF);
        stream.write(value & 0xFF);
    }

    public void writeInt32(int value) throws IOException { //Little endian.
        position += 4;
        stream.write(value & 0xFF);
        stream.write((value >> 8) & 0xFF);
        stream.write((value >> 16) & 0xFF);
        stream.write((value >> 24) & 0xFF);
    }

    public void writeInt32LE(int value) throws IOException {
        position += 4;
        stream.write(value & 0xFF);
        stream.write((value >> 8) & 0xFF);
        stream.write((value >> 16) & 0xFF);
        stream.write((value >> 24) & 0xFF);
    }

    public void writeInt32BE(int value) throws IOException {
        position += 4;
        stream.write((value >> 24) & 0xFF);
        stream.write((value >> 16) & 0xFF);
        stream.write((value >> 8) & 0xFF);
        stream.write(value & 0xFF);
    }

    public void write(byte[] bytes) throws IOException {
        position += bytes.length;
        stream.write(bytes);
    }

    public void write(String string) throws IOException {
        position += string.length();
        stream.write(string.getBytes());
    }

    public int tell() {
        return position;
    }
}
