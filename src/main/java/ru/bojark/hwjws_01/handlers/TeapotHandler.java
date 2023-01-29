package ru.bojark.hwjws_01.handlers;

import ru.bojark.hwjws_01.Handler;
import ru.bojark.hwjws_01.Request;

import java.io.BufferedOutputStream;
import java.io.IOException;

public class TeapotHandler implements Handler {
    @Override
    public void handle(Request request, BufferedOutputStream responseStream) {
        try {
            responseStream.write((
                    "HTTP/1.1 418 I’m a teapot\r\n" +
                            "Content-Type: " + "text/plain" + "\r\n" +
                            "Content-Length: " + 0 + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            responseStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
