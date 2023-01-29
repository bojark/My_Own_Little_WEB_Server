package ru.bojark.hwjws_01;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final int PORT;
    private int limit = 4096;
    private final byte[] REQUEST_LINE_DELIMETER = new byte[]{'\r', '\n'};
    private final byte[] HEADERS_DELIMETER = new byte[]{'\r', '\n', '\r', '\n'};
    private int carriage;

    private Map<String, Map<String, Handler>> handlers = new ConcurrentHashMap<>() {
    };
//    private Map<String, Handler> getHandlers = new HashMap<>();

    public Server(int port) {
        this.PORT = port;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void addHandler(String method, String path, Handler handler) {

            if(handlers.containsKey(method)){
               handlers.get(method).put(path, handler);

            } else {
                handlers.put(method, new ConcurrentHashMap<>());
                handlers.get(method).put(path, handler);
            }
        System.out.println("Положили хендлер для " + method + " " + path);
    }

    public void connect(Socket socket) {
        System.out.println("Новое подключение! Порт: " + socket.getPort());
        try (final var in = new BufferedInputStream(socket.getInputStream());
             final var out = new BufferedOutputStream(socket.getOutputStream())) {

            Request request = requestParser(in, out);

            String method = request.getMethod();
            String path = request.getPath();
            executeHandler(method, path, request, out);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void executeHandler(String method, String path, Request request, BufferedOutputStream out)
            throws IOException {
        if (handlers.containsKey(method)) {
            if (handlers.get(method).containsKey(path)) {
                System.out.println("Нашли хендлер для " + path);
                handlers.get(method).get(path).handle(request, out);
            } else {
                badRequest(out);
            }
        } else {
            badRequest(out);
        }
    }

    public void start() {
        System.out.println("Сервер запускается. Порт: " + PORT);
        final ExecutorService threadPool = Executors.newFixedThreadPool(64);
        try (final var serverSocket = new ServerSocket(PORT)) {
            while (true) {
                try {
                    final var socket = serverSocket.accept();
                    threadPool.submit(() -> connect(socket));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Request requestParser(BufferedInputStream in,
                                  BufferedOutputStream out) throws IOException {
        Request.RequestBuilder rb = new Request.RequestBuilder();

        String[] requestLine = extractRequestLine(in, out);
        rb.setRequestLine(requestLine);
        System.out.println(Arrays.toString(requestLine));

        //пока делаю Request только с requestLine, больше в этом задании не требуется:
        return rb.build();

    }

    private String[] extractRequestLine(BufferedInputStream in, BufferedOutputStream out) throws IOException {
        in.mark(limit);
        final var buffer = new byte[limit];
        final var read = in.read(buffer);
        //request line
        carriage = indexOf(buffer, REQUEST_LINE_DELIMETER, 0, read);
        if (carriage == -1) {
            badRequest(out);
            return null;
        }
        final var requestLine = new String(Arrays.copyOf(buffer, carriage)).split(" ");
        if (requestLine.length != 3) {
            badRequest(out);
            return null;
        }
        in.reset();
        return requestLine;
    }

    private List<String> extractHeaders(BufferedInputStream in, BufferedOutputStream out) throws IOException {
        in.mark(limit);
        final var buffer = new byte[limit];
        final var read = in.read(buffer);

        final var headersStart = carriage + REQUEST_LINE_DELIMETER.length;
        carriage = indexOf(buffer, HEADERS_DELIMETER, headersStart, read);
        if (carriage == -1) {
            badRequest(out);
        }

        in.reset();
        // пропускаем requestLine
        in.skip(headersStart);

        final var headersBytes = in.readNBytes(carriage - headersStart);
        return Arrays.asList(new String(headersBytes).split("\r\n"));

    }

    private static void badRequest(BufferedOutputStream out) throws IOException {
        out.write((
                "HTTP/1.1 400 Bad Request\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.flush();
    }

    // from google guava with modifications
    private static int indexOf(byte[] array, byte[] target, int start, int max) {
        outer:
        for (int i = start; i < max - target.length + 1; i++) {
            for (int j = 0; j < target.length; j++) {
                if (array[i + j] != target[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }


}
