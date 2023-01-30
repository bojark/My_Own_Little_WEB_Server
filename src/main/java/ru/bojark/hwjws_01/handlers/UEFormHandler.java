package ru.bojark.hwjws_01.handlers;

import ru.bojark.hwjws_01.Handler;
import ru.bojark.hwjws_01.Request;
import ru.bojark.hwjws_01.Server;
import ru.bojark.hwjws_01.misc.BadRequestUtil;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class UEFormHandler implements Handler {
    @Override
    public void handle(Request request, BufferedOutputStream responseStream) {
        final var filePath = Path.of(".", "public", "/parametrised.html");
        try {
            final var mimeType = Files.probeContentType(filePath);
            final var template = Files.readString(filePath);

            //todo здесь сделать экстракцию параметров:
            final var content = template.replace(
                    "{title}",
                    request.getPostParams().get(0).getValue()
            ).replace(
                    "{value1}",
                    request.getPostParams().get(1).getValue()
            ).replace(
                    "{value2}",
                    request.getPostParams().get(2).getValue()
            ).replace(
                    "{image}",
                    request.getPostParams().get(3).getValue()
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
                BadRequestUtil.badRequest(responseStream);
            } catch (IOException ex) {
                e.printStackTrace();
            }
        }
    }
}
