package ru.bojark.hwjws_01;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

public class Request {

    private final String method;
    private final String path;
    private final String query;
    private final String protocol;
    private final List<String> headers;
    private final String body;

    private Request(String method,
                    String path,
                    String protocol,
                    List<String> headers,
                    String body) {
        this.method = method;
        this.query = path;
        if (path.contains("\\?")) {
            String[] queryLine = path.split(String.valueOf('?'), 2);
            this.path = queryLine[0];
        } else {
            this.path = path;
        }
        this.protocol = protocol;
        this.headers = headers;
        this.body = body;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    private Optional<String> extractHeader(String header) {
        return this.headers.stream()
                .filter(o -> o.startsWith(header))
                .map(o -> o.substring(o.indexOf(" ")))
                .map(String::trim)
                .findFirst();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(method).append(" ").append(path).append(" ").append(protocol).append("\r\n");
        for (String header : headers) {
            sb.append(header);
        }
        sb.append("\r\n").append(body);
        return sb.toString();
    }

    public List<NameValuePair> getQueryParams() throws URISyntaxException {
        //todo???
        return URLEncodedUtils.parse(URI.create(query), "utf-8");
    }

    public void getQueryParam(String name){
        //todo тут может быть несколько параметров с одним и тем же именем

    }


    //класс-фабрика
    public static class RequestBuilder {
        private String[] requestLine = null;
        private List<String> headers = List.of("Empty header");
        private String body = "Empty body";

        public void setRequestLine(String[] requestLine) {
            this.requestLine = requestLine;
        }

        public void setHeaders(List<String> headers) {
            this.headers = headers;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public Request build() {

            return new Request(requestLine[0], requestLine[1], requestLine[2], headers, body);
        }

    }
}
