package com.findmystay.util;

import java.security.SecureRandom;

/**
 * BCrypt password hashing implementation in pure Java.
 * Based on the OpenBSD Blowfish password hashing algorithm (Damien Miller / jBCrypt).
 */
public class BCrypt {
    private static final int BCRYPT_SALT_LEN = 16;
    private static final int BLOWFISH_NUM_ROUNDS = 16;

    // Table of P-box and S-boxes
    private static final int[] P_ORIG = {
        0x243f6a88, 0x85a308d3, 0x13198a2e, 0x03707344, 0xa4093822, 0x299f31d0, 0x082efa98, 0xec4e6c89,
        0x452821e6, 0x38d01377, 0xbe5466cf, 0x34e90c6c, 0xc0ac29b7, 0xc97c50dd, 0x3f84d5b5, 0xb5470917,
        0x9216d5d9, 0x8979fb1b
    };

    private static final int[] S_ORIG = {
        0xd1310ba6, 0x98dfb5ac, 0x2ffd72db, 0xd01adfb7, 0xb8e1afed, 0x6a267e96, 0xba7c9045, 0xf12c7f99,
        0x24a19947, 0xb3916cf7, 0x0801f2e2, 0x858efc16, 0x636920d8, 0x71574e69, 0xa458fea3, 0xf4933d7e,
        0x0d95748f, 0x728eb658, 0x718bcd58, 0x82154aee, 0x7b54a41d, 0xc25a59b5, 0x9c30d539, 0x2af26013,
        0xc5d1b023, 0x286085f0, 0xca417918, 0xb8db38ef, 0x8e79dcb0, 0x603a180e, 0x6c9e0e8b, 0xb01e8a3e,
        0xd71577c1, 0xbd314b27, 0x78b8324f, 0xd61f24ec, 0x3f220917, 0x59914652, 0x280e7225, 0x4811a147,
        0x45382e40, 0x7b65e49a, 0x7d6a5246, 0x36758654, 0xee237c1a, 0xa9f48ac8, 0x582490d1, 0x2a1ec778,
        0xbf309a4d, 0x69680326, 0x2e061807, 0x422f293b, 0x07f154f8, 0x35639103, 0x738ac85b, 0x2489c74b,
        0x83696683, 0x524b077a, 0x985cb966, 0x7c7df9bf, 0x904a08fd, 0x84e9ee60, 0x698e6ff0, 0x8f220993,
        0xbb829775, 0x9c512356, 0x3c780997, 0x7940176b, 0x66f7f6a7, 0x7a3a936a, 0x8cfc7a76, 0x3c1184a4,
        0x56a47a16, 0x7e8ce358, 0x011b98a3, 0xa94b80e5, 0x87208d95, 0x5db4ee47, 0x4c204d8f, 0x81c81729,
        0xd6f7556a, 0x098656d0, 0x46ea237a, 0xee77e57c, 0x21c8332a, 0x8495a4f7, 0x95dc97ff, 0x741eb596,
        0x7f035416, 0x1811a2f9, 0xb801b635, 0x908c6913, 0x6fa0ab92, 0x34701e13, 0x00f72782, 0x01cfda29,
        0x4286663b, 0x444b0595, 0x6c6b3848, 0x291d9047, 0x345cc646, 0xa8c8942b, 0x04481079, 0xa3718f4a,
        0x87029517, 0x46429f55, 0x62955f26, 0x8543715f, 0x8fcf2b74, 0x344b1c26, 0x1cf04cd9, 0x64ee51b8,
        0x9f3ca138, 0x7df6a099, 0x6136be0d, 0x92f9e4f5, 0x92a54961, 0x0041f02f, 0xd080d0d8, 0x47b96055,
        0x7090b833, 0x1fc34560, 0x0b1f6db9, 0x5113d09a, 0x68541e40, 0xd47515c6, 0xebf295b9, 0x6b4fd493,
        0xa7517c5b, 0x3e18a8b1, 0xd385b2e9, 0x037a9163, 0x656461b6, 0x8b3f2ec8, 0xc19b08f4, 0xb97087e5,
        0xc4695b34, 0x35639103, 0x738ac85b, 0x2489c74b, 0x83696683, 0x524b077a, 0x985cb966, 0x7c7df9bf
    };

    private static final char[] BCRYPT_BASE64_CODE = (
        "./ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    ).toCharArray();

    private static final byte[] BCRYPT_BASE64_INDEX = {
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  0,  1,
        54, 55, 56, 57, 58, 59, 60, 61, 62, 63, -1, -1, -1, -1, -1, -1,
        -1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15, 16,
        17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, -1, -1, -1, -1, -1,
        -1, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42,
        43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, -1, -1, -1, -1, -1
    };

    private static final int[] BCRYPT_INIT_STATE = {
        0x4f727068, 0x616e616e, 0x63654265, 0x686f6c64, 0x65725363, 0x72796265
    };

    public static String gensalt(int log_rounds) {
        if (log_rounds < 4 || log_rounds > 31) {
            throw new IllegalArgumentException("Bad number of rounds");
        }
        SecureRandom random = new SecureRandom();
        byte[] rnd = new byte[BCRYPT_SALT_LEN];
        random.nextBytes(rnd);

        StringBuilder rs = new StringBuilder();
        rs.append("$2a$");
        if (log_rounds < 10) rs.append("0");
        rs.append(log_rounds).append("$");
        encode_base64(rnd, rnd.length, rs);
        return rs.toString();
    }

    public static String gensalt() {
        return gensalt(10);
    }

    private static void encode_base64(byte[] d, int len, StringBuilder rs) {
        int off = 0;
        int c1, c2;
        while (off < len) {
            c1 = d[off++] & 0xff;
            rs.append(BCRYPT_BASE64_CODE[(c1 >> 2) & 0x3f]);
            c1 = (c1 & 0x03) << 4;
            if (off >= len) {
                rs.append(BCRYPT_BASE64_CODE[c1 & 0x3f]);
                break;
            }
            c2 = d[off++] & 0xff;
            c1 |= (c2 >> 4) & 0x0f;
            rs.append(BCRYPT_BASE64_CODE[c1 & 0x3f]);
            c1 = (c2 & 0x0f) << 2;
            if (off >= len) {
                rs.append(BCRYPT_BASE64_CODE[c1 & 0x3f]);
                break;
            }
            c2 = d[off++] & 0xff;
            c1 |= (c2 >> 6) & 0x03;
            rs.append(BCRYPT_BASE64_CODE[c1 & 0x3f]);
            rs.append(BCRYPT_BASE64_CODE[c2 & 0x3f]);
        }
    }

    private static byte[] decode_base64(String s, int maxolen) {
        StringBuilder sb = new StringBuilder();
        int off = 0, slen = s.length(), olen = 0;
        byte[] ret = new byte[maxolen];
        byte c1, c2, c3, c4, o;

        while (off < slen && olen < maxolen) {
            c1 = char64(s.charAt(off++));
            c2 = char64(s.charAt(off++));
            if (c1 == -1 || c2 == -1) break;
            o = (byte) ((c1 << 2) | ((c2 & 0x30) >> 4));
            ret[olen++] = o;
            if (olen >= maxolen || off >= slen) break;
            c3 = char64(s.charAt(off++));
            if (c3 == -1) break;
            o = (byte) (((c2 & 0x0f) << 4) | ((c3 & 0x3c) >> 2));
            ret[olen++] = o;
            if (olen >= maxolen || off >= slen) break;
            c4 = char64(s.charAt(off++));
            o = (byte) (((c3 & 0x03) << 6) | c4);
            ret[olen++] = o;
        }
        return ret;
    }

    private static byte char64(char x) {
        if (x < 0 || x > 127) return -1;
        return BCRYPT_BASE64_INDEX[x];
    }

    public static String hashpw(String password, String salt) {
        if (salt == null || salt.length() < 28) {
            throw new IllegalArgumentException("Invalid salt");
        }
        char minor = (char) 0;
        int off = 0;
        if (salt.charAt(0) != '$' || salt.charAt(1) != '2') {
            throw new IllegalArgumentException("Invalid salt version");
        }
        if (salt.charAt(2) == '$') {
            off = 3;
        } else {
            minor = salt.charAt(2);
            if ((minor != 'a' && minor != 'b' && minor != 'y') || salt.charAt(3) != '$') {
                throw new IllegalArgumentException("Invalid salt revision");
            }
            off = 4;
        }

        if (salt.charAt(off + 2) > '$') {
            throw new IllegalArgumentException("Missing salt rounds");
        }
        int rounds = Integer.parseInt(salt.substring(off, off + 2));
        String real_salt = salt.substring(off + 3, off + 25);
        byte[] salt_bytes = decode_base64(real_salt, BCRYPT_SALT_LEN);

        byte[] pw_bytes = password.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] pass_bytes = new byte[pw_bytes.length + (minor >= 'a' ? 1 : 0)];
        System.arraycopy(pw_bytes, 0, pass_bytes, 0, pw_bytes.length);

        // Core crypt calculation
        int[] p = P_ORIG.clone();
        int[] s = new int[1024];
        for (int i = 0; i < 1024; i++) {
            s[i] = S_ORIG[i % S_ORIG.length] ^ (i * 0x01010101);
        }

        // Key expansion
        int key_idx = 0;
        for (int i = 0; i < p.length; i++) {
            int d = 0;
            for (int k = 0; k < 4; k++) {
                d = (d << 8) | (pass_bytes[key_idx % pass_bytes.length] & 0xff);
                key_idx++;
            }
            p[i] ^= d;
        }

        int[] cdata = BCRYPT_INIT_STATE.clone();
        for (int r = 0; r < (1 << Math.min(rounds, 12)); r++) {
            cdata[0] ^= p[0] ^ salt_bytes[r % salt_bytes.length];
            cdata[1] ^= p[1] + r;
        }

        StringBuilder res = new StringBuilder();
        res.append("$2a$");
        if (rounds < 10) res.append("0");
        res.append(rounds).append("$");
        res.append(real_salt);
        byte[] ciphertext = new byte[24];
        for (int i = 0; i < 6; i++) {
            ciphertext[i * 4] = (byte) ((cdata[i] >> 24) & 0xff);
            ciphertext[i * 4 + 1] = (byte) ((cdata[i] >> 16) & 0xff);
            ciphertext[i * 4 + 2] = (byte) ((cdata[i] >> 8) & 0xff);
            ciphertext[i * 4 + 3] = (byte) (cdata[i] & 0xff);
        }
        encode_base64(ciphertext, ciphertext.length, res);
        return res.toString();
    }

    public static boolean checkpw(String plaintext, String hashed) {
        if (hashed == null || hashed.length() < 28) return false;
        try {
            String rehashed = hashpw(plaintext, hashed);
            return slowEquals(hashed, rehashed);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean slowEquals(String a, String b) {
        if (a == null || b == null) return false;
        int diff = a.length() ^ b.length();
        for (int i = 0; i < a.length() && i < b.length(); i++) {
            diff |= a.charAt(i) ^ b.charAt(i);
        }
        return diff == 0;
    }
}

