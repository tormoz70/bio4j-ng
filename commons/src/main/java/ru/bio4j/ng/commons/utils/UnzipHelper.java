package ru.bio4j.ng.commons.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Помощник для распаковки zip-архивов
 */
public class UnzipHelper implements AutoCloseable {
    private InputStream inputStream  = null;

    public UnzipHelper(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public UnzipHelper(byte[] blob) {
        this.inputStream = new ByteArrayInputStream(blob);
    }

    public void process(UnzipHelperDelegate delegate) throws Exception {
        if (this.inputStream != null) {
            ZipInputStream zis = null;
            try {
                zis = new ZipInputStream(inputStream);
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    if(delegate != null)
                        delegate.callback(zis, entry);
                }
            } finally {
                if(zis != null) {
                    zis.closeEntry();
                    zis.close();
                }
            }
        }
    }


    @Override
    public void close() throws IOException {
        if (this.inputStream != null)
            this.inputStream.close();
    }
}
