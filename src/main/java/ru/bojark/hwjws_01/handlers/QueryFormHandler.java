package ru.bojark.hwjws_01.handlers;

import ru.bojark.hwjws_01.Handler;
import ru.bojark.hwjws_01.Request;
import ru.bojark.hwjws_01.misc.ResponceUtil;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class QueryFormHandler implements Handler {

    @Override
    public void handle(Request request, BufferedOutputStream responseStream) {
        final Path filePath;
        if(request.getPath().contains("login")){
            System.out.println("ПРИВЕТ ИЗ ХЕНДЛЕРА, ВЕТКИ ИФА");
            filePath = Path.of(".", "public", "/password_login.html");
            try {
                final var mimeType = Files.probeContentType(filePath);
                final var template = Files.readString(filePath);

                final var content = template.replace(
                        "{login}",
                        request.getQueryParam("login").get(0).getValue()
                ).replace(
                        "{password}",
                        request.getQueryParam("password").get(0).getValue()
                ).getBytes();

                responseStream.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: " + content.length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                responseStream.write(content);
                responseStream.flush();

            } catch (IOException e) {
                try {
                    ResponceUtil.badRequest(responseStream);
                } catch (IOException ex) {
                    e.printStackTrace();
                }
            }
        } else {
            filePath = Path.of(".", "public", request.getPath());
            BasicHandler.respondOK(responseStream, filePath);
        }



    }
}
