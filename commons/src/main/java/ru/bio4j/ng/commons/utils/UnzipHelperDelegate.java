package ru.bio4j.ng.commons.utils;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Делегат для UnzipHelper
 */
public interface UnzipHelperDelegate {
    void callback(ZipInputStream zis, ZipEntry entry) throws Exception;
}
