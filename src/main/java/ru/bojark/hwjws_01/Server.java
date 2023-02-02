package ru.bojark.hwjws_01;

import ru.bojark.hwjws_01.misc.Colors;
import ru.bojark.hwjws_01.misc.ResponceUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final int PORT;
    private int limit = 4096;


    private Map<String, Map<String, Handler>> handlers = new ConcurrentHashMap<>() {
    };

    public Server(int port) {
        this.PORT = port;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void addHandler(String method, String path, Handler handler) {

        if (handlers.containsKey(method)) {
            handlers.get(method).put(path, handler);

        } else {
            handlers.put(method, new ConcurrentHashMap<>());
            handlers.get(method).put(path, handler);
        }
        System.out.println(Colors.RESET + "New handler for " + Colors.BLUE_BOLD + method + Colors.YELLOW_BOLD + " " + path);
    }

    public void start() {
        System.out.println(Colors.CYAN + ">> Server started. Port: " + Colors.YELLOW_BOLD + PORT + Colors.CYAN + " <<" + Colors.RESET);
        final ExecutorService threadPool = Executors.newFixedThreadPool(64);
        try (final var serverSocket = new ServerSocket(PORT)) {
            while (true) {
                try {
                    final var socket = serverSocket.accept();
                    threadPool.submit(() -> handleConnection(socket));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleConnection(Socket socket) {
        System.out.println("New connection! Port: " + Colors.YELLOW_BOLD + socket.getPort() + Colors.RESET);
        try (final var in = new BufferedInputStream(socket.getInputStream());
             final var out = new BufferedOutputStream(socket.getOutputStream())) {

            RequestParser rp = new RequestParser(limit);

            Request request = rp.parseRequest(in, out);
            String method = request.getMethod();
            String path = request.getPath();
            executeHandler(method, path, request, out);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void executeHandler(String method, String path, Request request, BufferedOutputStream out)
            throws IOException {
        if (handlers.containsKey(method)) {
            if (handlers.get(method)
                    .containsKey(path)) {
                System.out.println(Colors.RESET + "Handler found for " + Colors.YELLOW_BOLD + path + Colors.RESET);
                handlers.get(method)
                        .get(path)
                        .handle(request, out);
            } else {
                ResponceUtil.notFound(out);
            }
        } else {
            ResponceUtil.badRequest(out);
        }
    }



}
