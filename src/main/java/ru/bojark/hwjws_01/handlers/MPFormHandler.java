package ru.bojark.hwjws_01.handlers;

import ru.bojark.hwjws_01.Handler;
import ru.bojark.hwjws_01.Request;

import java.io.BufferedOutputStream;
import java.io.IOException;

public class MPFormHandler implements Handler {
    @Override
    public void handle(Request request, BufferedOutputStream out) {
        try {
            out.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Length: 0\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
