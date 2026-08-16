package com.animal.java.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.security.*;
import java.util.Base64;

@Slf4j
public class KeyGen {

    private static final Base64.Encoder encoder = Base64.getEncoder();

    private static void writeBase64(Writer out, Key key) throws IOException {
        byte[] buf = key.getEncoded();
        out.write(encoder.encodeToString(buf));
        out.write("\n");
    }

//    public static void main(String[] args) throws Exception {
//        int keySize = 2048;
//        String outFile = "app";
//        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
//
//        keyPairGenerator.initialize(keySize);
//        KeyPair kp = keyPairGenerator.generateKeyPair();
//
//        try (Writer out = (outFile != null) ? new FileWriter(outFile + ".key") : new OutputStreamWriter(System.out)) {
//            log.info("Private key format: " + kp.getPrivate().getFormat());
//            out.write("-----BEGIN PRIVATE KEY-----\n");
//            writeBase64(out, kp.getPrivate());
//            out.write("-----END PRIVATE KEY-----\n");
//
//            if (outFile != null) {
//                out.close();
//                try (Writer pubOut = new FileWriter(outFile + ".pub")) {
//                    log.info("Public key format: " + kp.getPublic().getFormat());
//                    pubOut.write("-----BEGIN PUBLIC KEY-----\n");
//                    writeBase64(pubOut, kp.getPublic());
//                    pubOut.write("-----END PUBLIC KEY-----\n");
//                }
//            }
//        }
//    }
}
