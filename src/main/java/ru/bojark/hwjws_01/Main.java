package ru.bojark.hwjws_01;

import ru.bojark.hwjws_01.handlers.*;

import java.util.List;

public class Main {
    private static final List<String> VALIDPATHS = List.of("/spring.svg", "/seagull.png",
            "/resources.html", "/styles.css", "/app.js", "/links.html", "/clicker.html", "/clicker.js");

    public static void main(String[] args) {
        Server server = new Server(9999);
        addHandlers(server);
        server.start();

    }

    private static void addHandlers(Server server) {
        server.addHandler("GET", "/post_form_multi.html", new BasicHandler());
        server.addHandler("GET", "/post_form_url.html", new BasicHandler());
        server.addHandler("GET", "/forms.html", new QueryFormHandler());
        server.addHandler("POST", "/2", new MPFormHandler());
        server.addHandler("POST", "/1", new UEFormHandler());
        for (String path : VALIDPATHS) {
            server.addHandler("GET", path, new BasicHandler());
        }
        server.addHandler("GET", "/classic.html", new ClassicHandler());
        server.addHandler("GET", "/teapot.html", new TeapotHandler());
    }

}


