package com.germaniumhq.spark.voice;

import java.net.URI;

public class UriUtil {
    /**
     * Concatenates URI components ensuring there's a single slash between them.
     */
    public static URI createUri(String... paths) {
        String result = createUriString(paths);
        return URI.create(result);
    }

    /**
     * Concatenates URI components ensuring there's a single slash between them.
     */
    public static String createUriString(String... paths) {
        StringBuilder sb = new StringBuilder();

        for (String path: paths) {
            if (path.startsWith("/")) {
                path = path.substring(1);
            }

            if (!sb.isEmpty() && !sb.toString().endsWith("/")) {
                sb.append("/");
            }

            sb.append(path);
        }

        String result = sb.toString();
        return result;
    }
}
