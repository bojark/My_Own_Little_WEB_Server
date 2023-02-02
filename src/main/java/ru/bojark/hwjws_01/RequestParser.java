package ru.bojark.hwjws_01;

import ru.bojark.hwjws_01.misc.ResponceUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class RequestParser {

    private final byte[] REQUEST_LINE_DELIMITER = new byte[]{'\r', '\n'};
    private final byte[] HEADERS_DELIMITER = new byte[]{'\r', '\n', '\r', '\n'};
    private int carriage;
    private int limit;

    public RequestParser(int limit){
        this.limit = limit;
    }

    public void setLimit(int limit){
        this.limit = limit;
    }

    public Request parseRequest(BufferedInputStream in,
                                  BufferedOutputStream out) throws IOException {
        Request.RequestBuilder rb = new Request.RequestBuilder();

        String[] requestLine = extractRequestLine(in, out);
        List<String> headers = extractHeaders(in, out);

        rb.setRequestLine(requestLine)
                .setHeaders(headers);

        if (requestLine[0] != null && requestLine[0].equals("POST")) {
            rb.setBody(extractBody(in, out, headers));
        }

        return rb.build();
    }

    private String[] extractRequestLine(BufferedInputStream in, BufferedOutputStream out) throws IOException {
        in.mark(limit);
        final var buffer = new byte[limit];
        final var read = in.read(buffer);
        carriage = indexOf(buffer, REQUEST_LINE_DELIMITER, 0, read);
        if (carriage == -1) {
            ResponceUtil.badRequest(out);
            return null;
        }
        final var requestLine = new String(Arrays.copyOf(buffer, carriage)).split(" ");
        if (requestLine.length != 3) {
            ResponceUtil.badRequest(out);
            return null;
        }
        in.reset();
        return requestLine;
    }

    private List<String> extractHeaders(BufferedInputStream in, BufferedOutputStream out) throws IOException {
        in.mark(limit);
        final var buffer = new byte[limit];
        final var read = in.read(buffer);

        final var headersStart = carriage + REQUEST_LINE_DELIMITER.length;
        carriage = indexOf(buffer, HEADERS_DELIMITER, headersStart, read);
        if (carriage == -1) {
            ResponceUtil.badRequest(out);
        }

        in.reset();
        // пропускаем requestLine
        in.skip(headersStart);

        final var headersBytes = in.readNBytes(carriage - headersStart);
        List<String> headers = Arrays.asList(new String(headersBytes).split("\r\n"));
//        System.out.println("Headers:\n" + headers);
        return headers;

    }

    private String extractBody(BufferedInputStream in, BufferedOutputStream out, List<String> headers) throws IOException {
        in.reset();
        in.skip(carriage + HEADERS_DELIMITER.length);
        final var contentLength = extractHeader(headers, "Content-Length");
        if (contentLength.isPresent()) {
            final var length = Integer.parseInt(contentLength.get());
            final var bodyBytes = in.readNBytes(length);
            String body = new String(bodyBytes);
//                System.out.println("Body:\n" + body);
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
