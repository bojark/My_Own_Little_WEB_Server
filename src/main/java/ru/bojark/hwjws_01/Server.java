package ru.bojark.hwjws_01;

import ru.bojark.hwjws_01.misc.BadRequestUtil;
import ru.bojark.hwjws_01.misc.Colors;

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
                BadRequestUtil.badRequest(out);
            }
        } else {
            BadRequestUtil.badRequest(out);
        }
    }

    public void start() {
        System.out.println(Colors.RESET + "Сервер запускается. Порт: " + PORT);
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
        List<String> headers = extractHeaders(in, out);

        rb.setRequestLine(requestLine)
                .setHeaders(headers);

        if(requestLine[0] != null && requestLine[0].equals("POST")){
            rb.setBody(extractBody(in, out, headers));
        }

        return rb.build();
    }

    private String[] extractRequestLine(BufferedInputStream in, BufferedOutputStream out) throws IOException {
        in.mark(limit);
        final var buffer = new byte[limit];
        final var read = in.read(buffer);
        //request line
        carriage = indexOf(buffer, REQUEST_LINE_DELIMETER, 0, read);
        if (carriage == -1) {
            BadRequestUtil.badRequest(out);
            return null;
        }
        final var requestLine = new String(Arrays.copyOf(buffer, carriage)).split(" ");
        if (requestLine.length != 3) {
            BadRequestUtil.badRequest(out);
            return null;
        }
        in.reset();
        System.out.println("RequestLine:\n" + Arrays.toString(requestLine));
        return requestLine;
    }

    private List<String> extractHeaders(BufferedInputStream in, BufferedOutputStream out) throws IOException {
        in.mark(limit);
        final var buffer = new byte[limit];
        final var read = in.read(buffer);

        final var headersStart = carriage + REQUEST_LINE_DELIMETER.length;
        carriage = indexOf(buffer, HEADERS_DELIMETER, headersStart, read);
        if (carriage == -1) {
            BadRequestUtil.badRequest(out);
        }

        in.reset();
        // пропускаем requestLine
        in.skip(headersStart);

        final var headersBytes = in.readNBytes(carriage - headersStart);
        List<String> headers = Arrays.asList(new String(headersBytes).split("\r\n"));
        System.out.println("Headers:\n" + headers);
        return headers;

    }
    private String extractBody(BufferedInputStream in, BufferedOutputStream out, List<String> headers) throws IOException {
            in.reset();
            in.skip(carriage + HEADERS_DELIMETER.length);
            // вычитываем Content-Length, чтобы прочитать body
            final var contentLength = extractHeader(headers, "Content-Length");
            if (contentLength.isPresent()) {
                final var length = Integer.parseInt(contentLength.get());
                final var bodyBytes = in.readNBytes(length);
                String body = new String(bodyBytes);
                System.out.println("Body:\n" + body);
                return body;
            }
            return null;
    }

    private static Optional<String> extractHeader(List<String> headers, String header) {
        return headers.stream()
                .filter(o -> o.startsWith(header))
                .map(o -> o.substring(o.indexOf(" ")))
                .map(String::trim)
                .findFirst();
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
