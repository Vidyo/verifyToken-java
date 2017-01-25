package io.vidyo.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.lang3.StringUtils;

public class AuthToken {
    public static final String PROVISION_TOKEN = "provision";
    public static final long EPOCH_SECONDS = 62167219200l;
    private static final int HASH_LENGTH = 96; // sha384 hash always 96 chars
    private static final String DELIM = "\0";


    private String type = PROVISION_TOKEN;
    private String jid; // this will be username@appID
    private String expires;
    private String vcard;
    private String hmac;

    public AuthToken(String type, String jid, String expires, String vcard, String hmac) {
        this.type = type;
        this.jid = jid;
        this.expires = expires;
        this.vcard = vcard;
        this.hmac = hmac;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public String getVcard() {
        return vcard;
    }

    public void setVcard(String vcard) {
        this.vcard = vcard;
    }

    public String getHmac() {
        return hmac;
    }

    public void setHmac(String hmac) {
        this.hmac = hmac;
    }

    public String getPayload() {
        return String.join(DELIM, type, jid, expires, vcard);
    }

    public static AuthToken parse(String tokenBase64) {
        if (StringUtils.isBlank(tokenBase64)) {
            return null;
        }

        String token = new String(Base64.decodeBase64(tokenBase64));
        String split[] = token.split(DELIM);
        if (split.length != 5 || split[4].length() != HASH_LENGTH) {
            return null;
        }

        return new AuthToken(split[0],split[1],split[2],split[3],split[4]);
    }

    public static String calculateHash(String key, String payload) {
        return HmacUtils.hmacSha384Hex(key, payload);
    }

    public String calculateHash(String key) {
        return calculateHash(key, getPayload());
    }

    public static String generateProvisionToken(String key, String jid, String expires, String vcard) {
        String payload = String.join(DELIM, PROVISION_TOKEN, jid, calculateExpiry(expires), vcard);
        return new String(Base64.encodeBase64(
                (String.join(DELIM, payload, calculateHash(key, payload))).getBytes()
        ));
    }

    public static String calculateExpiry(String expires) {
        long expiresLong = 0l;
        long currentUnixTimestamp = System.currentTimeMillis() / 1000;
        try {
            expiresLong = Long.parseLong(expires);
        } catch (NumberFormatException e) {
            // 0 it is!
        }
        return ""+(EPOCH_SECONDS + currentUnixTimestamp + expiresLong);
    }

    public boolean isExpired() {
        if (StringUtils.isBlank(this.expires)) {
            return true;
        }

        long secondsLeftTilExpiry = 0;
        try {
            long unixTimestampExpires = Long.parseLong(this.expires) - EPOCH_SECONDS;
            long currentUnixTimestamp = System.currentTimeMillis() / 1000;
            secondsLeftTilExpiry = unixTimestampExpires - currentUnixTimestamp;
        } catch (NumberFormatException e) {
            System.out.println("Could not parse expiry seconds: " + this.expires);
        }
        return secondsLeftTilExpiry <= 0;
    }
}
