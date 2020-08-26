package xyz.acrylicstyle.region.api.util;

public class ByteToInt {
    public static int b2i(byte b) {
        if (b < 0) {
            return 128 + (128 + b);
        } else {
            return b;
        }
    }
}
