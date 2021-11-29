package org.embroideryio.embroideryio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.embroideryio.embroideryio.EmbPattern.BaseIO;
import org.embroideryio.embroideryio.EmbPattern.Reader;
import org.embroideryio.embroideryio.EmbPattern.Writer;

/*
This class is deprecated. Instead use EmbPattern.<command>.
*/
public class EmbroideryIO {

    public static void setSettings(BaseIO obj, Object... settings) {
        EmbPattern.setSettings(obj, settings);
    }

    public static void writeEmbroidery(EmbPattern pattern, Writer writer, OutputStream out) throws IOException {
        EmbPattern.writeEmbroidery(pattern, writer, out);
    }

    public static void writeStream(EmbPattern pattern, String path, OutputStream out, Object... settings) throws IOException {
        EmbPattern.writeStream(pattern, path, out, settings);
    }

    public static void write(EmbPattern pattern, String path, Object... settings) throws IOException {
        EmbPattern.write(pattern, path, settings);
    }

    public static EmbPattern readEmbroidery(Reader reader, InputStream in) throws IOException {
        return EmbPattern.readEmbroidery(reader, in);
    }

    public static EmbPattern readStream(String path, InputStream out, Object... settings) throws IOException {
        return EmbPattern.readStream(path, out, settings);
    }

    public static EmbPattern read(String path, Object... settings) throws IOException {
        return EmbPattern.read(path, settings);
    }

    public static String getExtensionByFileName(String name) {
        return EmbPattern.getExtensionByFileName(name);
    }

    public static EmbPattern.Reader getReaderByFilename(String filename) {
        return EmbPattern.getReaderByFilename(filename);
    }

    public static Reader getReaderByMime(String mime) {
        return EmbPattern.getReaderByMime(mime);
    }

    public static EmbPattern.Writer getWriterByFilename(String filename) {
        return EmbPattern.getWriterByFilename(filename);
    }

    public static Writer getWriterByMime(String mime) {
        return EmbPattern.getWriterByMime(mime);
    }

}
