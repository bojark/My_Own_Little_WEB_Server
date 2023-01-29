package ru.bojark.hwjws_01;

import ru.bojark.hwjws_01.handlers.BasicHandler;
import ru.bojark.hwjws_01.handlers.ClassicHandler;
import ru.bojark.hwjws_01.handlers.FormHandler;
import ru.bojark.hwjws_01.handlers.TeapotHandler;

import java.io.IOException;
import java.util.List;

public class Main {
    private static final List<String> VALIDPATHS = List.of("/index.html", "/spring.svg", "/seagull.png",
            "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html",
            "/events.html", "/events.js");

    public static void main(String[] args) {
        Server server = new Server(9999);
        addHandlers(server);
        server.start();

    }

    private static void addHandlers(Server server){
        server.addHandler("GET", "/post_form.html", new BasicHandler());
        server.addHandler("GET", "/?value=get-value", new FormHandler());
        for (String path : VALIDPATHS) {
            server.addHandler("GET", path, new BasicHandler());
        }
        server.addHandler("GET", "/classic.html", new ClassicHandler());
        server.addHandler("GET", "/teapot.html", new TeapotHandler());
    }

}


