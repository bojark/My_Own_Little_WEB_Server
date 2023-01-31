package ru.bojark.hwjws_01;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import ru.bojark.hwjws_01.misc.Colors;
import ru.bojark.hwjws_01.misc.KeyValuePair;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Request {

    private static final String EMPTY_HEADER = "Empty header";
    private static final String EMPTY_BODY = "Empty body";
    private static final String CHARSET_NAME = "utf-8";

    private final String method;
    private final String path;
    private final String query;
    private final String protocol;
    private final List<String> headers;
    private final String body;
    private final List<NameValuePair> queryParams;
    private final List<NameValuePair> postParams;

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
        List<NameValuePair> queryParams1;
        try {
            queryParams1 = extractQueryParams();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            queryParams1 = new ArrayList<>();
        }
        queryParams = queryParams1;
        if (!this.body.equals(EMPTY_BODY)) {
            postParams = extractPostParams();
        } else {
            postParams = new ArrayList<>();
        }
        System.out.println(Colors.GREEN + ">> New Request <<\n" + Colors.WHITE + this + Colors.GREEN + "\n>> END <<" + Colors.RESET);
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


    private List<NameValuePair> extractQueryParams() throws URISyntaxException {
        return URLEncodedUtils.parse(URI.create(query), CHARSET_NAME);
    }

    public List<NameValuePair> getQueryParams() {
        return queryParams;
    }

    private List<NameValuePair> getParam(String name, List<NameValuePair> source) {
        return source.stream()
                .filter(p -> p.getName().equals(name))
                .collect(Collectors.toList());
    }

    public List<NameValuePair> getQueryParam(String name) {
        //todo тут может быть несколько параметров с одним и тем же именем, но по факту мы находим один:
        return getParam(name, queryParams);
    }


    private List<NameValuePair> extractPostParams() {
        List<String> params = List.of(body.split("&"));
        System.out.println("POST params extracted: " + Colors.WHITE + params + Colors.RESET);
        List<NameValuePair> keyValues = new ArrayList<>();
        for (String param : params) {
            String[] kv = param.split("=");
            keyValues.add(new KeyValuePair(kv[0], kv[1]));
        }
        return keyValues;

    }

    public List<NameValuePair> getPostParams() {
        return postParams;
    }

    public List<NameValuePair> getPostParam(String name) {
        return getParam(name, postParams);
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

    //класс-фабрика
    public static class RequestBuilder {

        private String[] requestLine = null;
        private List<String> headers = List.of(EMPTY_HEADER);
        private String body = EMPTY_BODY;

        public RequestBuilder setRequestLine(String[] requestLine) {
            this.requestLine = requestLine;
            return this;
        }

        public RequestBuilder setHeaders(List<String> headers) {
            this.headers = headers;
            return this;
        }

        public RequestBuilder setBody(String body) {
            this.body = body;
            return this;
        }

        public Request build() {

            return new Request(requestLine[0], requestLine[1], requestLine[2], headers, body);
        }

    }
}
