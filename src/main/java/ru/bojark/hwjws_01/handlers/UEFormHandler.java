package ru.bojark.hwjws_01.handlers;

import ru.bojark.hwjws_01.Handler;
import ru.bojark.hwjws_01.Request;

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
                    "ЗАГЛУШКА"
            ).replace(
                    "{value1}",
                    "ЗАГЛУШКА"
            ).replace(
                    "{value2}",
                    "ЗАГЛУШКА"
            ).replace(
                    "{image}",
                    "ЗАГЛУШКА"
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
            e.printStackTrace();
        }
    }
}
