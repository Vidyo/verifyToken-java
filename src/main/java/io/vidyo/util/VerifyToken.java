package io.vidyo.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

public class VerifyToken {

    public static void main(String[] args) throws IOException {
        try (InputStreamReader isr = new InputStreamReader(System.in);
             BufferedReader br = new BufferedReader(isr)) {

            while (true) {
                System.out.print("Enter token: ");

                String line = br.readLine();
                if (StringUtils.isBlank(line)) {
                    System.out.println("Exiting");
                    break;
                }

                AuthToken token = AuthToken.parse(line);
                if (token == null) {
                    System.out.println("Unable to parse token, please try again.");
                    continue;
                }

                long expires = Long.parseLong(token.getExpires());
                long currentUnixTimestamp = System.currentTimeMillis() / 1000;
                long unixTimestampExpires = expires - AuthToken.EPOCH_SECONDS;
                long secondsLeft = unixTimestampExpires - currentUnixTimestamp;

                System.out.println("Payload: " + token.getPayload().replaceAll("\0", "\\\\0"));
                System.out.println("Expires: " + new Date(unixTimestampExpires * 1000) + " | " + secondsLeft + " seconds left (" + (token.isExpired() ? "EXPIRED" : "OK") + ")");

                System.out.print("Verify signature (y/N): ");
                String verifySig = br.readLine();
                if ("y".equalsIgnoreCase(verifySig)) {
                    System.out.print("Developer key: ");
                    String devKey = br.readLine();
                    String calculatedHash = AuthToken.calculateHash(devKey, token.getPayload());
                    boolean validHash = calculatedHash.equals(token.getHmac());
                    System.out.println("Signature: " + (validHash ? "VERIFIED" : "INVALID SIGNATURE (or developer key)"));
                }

                System.out.println();
            } // while

        } // try
    }

}
