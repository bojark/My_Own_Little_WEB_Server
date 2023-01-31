package ru.bojark.hwjws_01.handlers;

import ru.bojark.hwjws_01.Handler;
import ru.bojark.hwjws_01.Request;
import ru.bojark.hwjws_01.misc.Colors;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BasicHandler implements Handler {
    @Override
    public void handle(Request request, BufferedOutputStream responseStream) {
        final var filePath = Path.of(".", "public", request.getPath());
        System.out.println("Handler tries to find something on " + Colors.YELLOW_BOLD + filePath + Colors.RESET);
        try{
            final var mimeType = Files.probeContentType(filePath);
            final var length = Files.size(filePath);
            responseStream.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            Files.copy(filePath, responseStream);
            responseStream.flush();
        } catch (IOException e){
            e.printStackTrace();
        }



    }
}
