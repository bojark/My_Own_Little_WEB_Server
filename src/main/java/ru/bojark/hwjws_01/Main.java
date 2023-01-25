package ru.bojark.hwjws_01;

import ru.bojark.hwjws_01.handlers.BasicHandler;
import ru.bojark.hwjws_01.handlers.ClassicHandler;

import java.io.IOException;
import java.util.List;

public class Main {
    private static final List<String> VALIDPATHS = List.of("/index.html", "/spring.svg", "/spring.png",
            "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html",
            "/events.html", "/events.js");

    public static void main(String[] args) {
        Server server = new Server(9999);
        for (String path : VALIDPATHS) {
            server.addHandler("GET", path, new BasicHandler());
        }
        server.addHandler("GET",
                "/classic.html",
                new ClassicHandler());
        server.addHandler("GET",
                "/teapot.html",
                (request, responseStream) -> {
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

                });
        server.start();

    }
}


