package ru.bio4j.ng.commons.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
//import org.apache.commons.codec.digest.DigestUtils;

public class MD5Checksum {
    private static final Logger LOG = LoggerFactory.getLogger(MD5Checksum.class);

//    public static void main(String args[]) {
//        String file = "C:/temp/abc.txt";
//
//        System.out.println("MD5 checksum for file using Java :                          "
//                + checkSum(file));
//        System.out.println("MD5 checksum of file in Java using Apache commons codec:    "
//                + checkSumApacheCommons(file));
//
//    }

    /*
     * Calculate checksum of a File using MD5 algorithm
     */
    public static String checkSum(String path) throws IOException, NoSuchAlgorithmException {
        String checksum = null;
        try {
            FileInputStream fis = new FileInputStream(path);
            MessageDigest md = MessageDigest.getInstance("MD5");

            //Using MessageDigest update() method to provide input
            byte[] buffer = new byte[8192];
            int numOfBytesRead;
            while( (numOfBytesRead = fis.read(buffer)) > 0){
                md.update(buffer, 0, numOfBytesRead);
            }
            byte[] hash = md.digest();
            checksum = new BigInteger(1, hash).toString(16); //don't use this, truncates leading zero
        } catch (IOException ex) {
            if(LOG.isDebugEnabled())LOG.error(null, ex);
            throw ex;
        } catch (NoSuchAlgorithmException ex) {
            if(LOG.isDebugEnabled())LOG.error(null, ex);
            throw ex;
        }

        return checksum;
    }

//    /*
//     * From Apache commons codec 1.4 md5() and md5Hex() method accepts InputStream as well.
//     * If you are using lower version of Apache commons codec than you need to convert
//     * InputStream to byte array before passing it to md5() or md5Hex() method.
//     */
//    public static String checkSumApacheCommons(String file){
//        String checksum = null;
//        try {
//            checksum = DigestUtils.md5Hex(new FileInputStream(file));
//        } catch (IOException ex) {
//            logger.log(Level.SEVERE, null, ex);
//        }
//        return checksum;
//    }


}
