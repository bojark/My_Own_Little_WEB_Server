package ru.bojark.hwjws_01;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import ru.bojark.hwjws_01.misc.Colors;
import ru.bojark.hwjws_01.misc.KeyValuePair;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Request {

    private static final String EMPTY_HEADER = "Empty header";
    private static final String EMPTY_BODY = "Empty body";
    private static final String CHARSET_NAME = "utf-8";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String URLENCODED = "application/x-www-form-urlencoded";

    private final String method;
    private final String path;
    private final String query;
    private final String protocol;
    private final List<NameValuePair> headers;
    private final String body;
    private final List<NameValuePair> queryParams;
    private final List<NameValuePair> postParams;

    private Request(String method,
                    String path,
                    String protocol,
                    List<NameValuePair> headers,
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

        if(doesContain(headers, CONTENT_TYPE_HEADER)){
            var contentType = headers.stream()
                    .filter(p -> Objects.equals(p.getName(), CONTENT_TYPE_HEADER))
                    .findFirst();
            System.out.println(contentType);
            if(contentType.isPresent()){
                if(Objects.equals(contentType.get().getValue(), URLENCODED)){
                    postParams = extractPostParams();
                } else {
                    postParams = new ArrayList<>();
                }
            } else {
                postParams = new ArrayList<>();
            }
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

    public List<NameValuePair> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    private Boolean doesContain(List<NameValuePair> list, String name){
        for (NameValuePair pair : list) {
            if(pair.getName().equals(name)){
                return true;
            }
        }
        return false;
    }

    private List<NameValuePair> getParam(String name, List<NameValuePair> source) {
        return source.stream()
                .filter(p -> p.getName().equals(name))
                .collect(Collectors.toList());
    }


    private List<NameValuePair> extractQueryParams() throws URISyntaxException {
        return URLEncodedUtils.parse(URI.create(query), CHARSET_NAME);
    }

    public List<NameValuePair> getQueryParams() {
        return queryParams;
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

    private NameValuePair findHeader(String name){
        return getParam(name, headers).get(0);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(method).append(" ").append(path).append(" ").append(protocol).append("\r\n");
        for (NameValuePair header : headers) {
            sb.append(header.getName()).append(": ").append(header.getValue()).append("\r\n");
        }
        sb.append(body);
        return sb.toString();
    }

    //класс-фабрика
    public static class RequestBuilder {

        private String[] requestLine = null;
        private List<String> headersList = List.of(EMPTY_HEADER);
        private String body = EMPTY_BODY;

        public RequestBuilder setRequestLine(String[] requestLine) {
            this.requestLine = requestLine;
            return this;
        }

        public RequestBuilder setHeaders(List<String> headers) {
            this.headersList = headers;
            return this;
        }

        public RequestBuilder setBody(String body) {
            this.body = body;
            return this;
        }

        private List<NameValuePair> parseHeaders(){
            List<NameValuePair> result = new ArrayList<>();
            for (String header : headersList) {
                String[] headerPair = header.split(": ");
                result.add(new KeyValuePair(headerPair[0], headerPair[1]));
            }
            return result;
        }

        public Request build() {

            return new Request(requestLine[0], requestLine[1], requestLine[2], parseHeaders(), body);
        }

    }
}
