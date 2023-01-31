package ru.bojark.hwjws_01.misc;

import java.io.BufferedOutputStream;
import java.io.IOException;

public class ResponceUtil {
    private static final String BAD_REQUEST =
            "HTTP/1.1 400 Bad Request\r\n" +
                    "Content-Length: 0\r\n" +
                    "Connection: close\r\n" +
                    "\r\n";
    private static final String NOT_FOUND =
            "HTTP/1.1 404 Not Found\r\n" +
                    "Content-Length: 0\r\n" +
                    "Connection: close\r\n" +
                    "\r\n";

    public static void badRequest(BufferedOutputStream out) throws IOException {
        sendResponse(out, BAD_REQUEST);
    }

    public static void notFound(BufferedOutputStream out) throws IOException {
        sendResponse(out, NOT_FOUND);
    }

    private static void sendResponse(BufferedOutputStream out, String message) throws IOException {
        out.write((
                message
        ).getBytes());
        out.flush();
    }
}
