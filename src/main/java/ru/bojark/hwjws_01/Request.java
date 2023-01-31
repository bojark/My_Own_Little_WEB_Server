package ru.bojark.hwjws_01;

import org.apache.commons.fileupload.FileUpload;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import ru.bojark.hwjws_01.misc.Colors;
import ru.bojark.hwjws_01.misc.KeyValuePair;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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
        System.out.println(Colors.GREEN + "Новый Request:\n" + Colors.WHITE + this + "\nEND" + Colors.RESET);
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
        return URLEncodedUtils.parse(URI.create(query), CHARSET_NAME);
    }

    public List<NameValuePair> getQueryParam(String name) throws URISyntaxException {
        //todo тут может быть несколько параметров с одним и тем же именем
        List<NameValuePair> nameValuePairs = getQueryParams();
        List<NameValuePair> result = new ArrayList<>();
        for (NameValuePair pair : nameValuePairs) {
            if(pair.getName().equals(name)){
                result.add(pair);
            }
        }
        return result;
    }

    public List<NameValuePair> getPostParams(){
        if (!body.equals(EMPTY_BODY)){
            List<String> params = List.of(body.split("&"));
            System.out.println(params);
            List<NameValuePair> keyValues = new ArrayList<>();
            for (String param : params) {
                String[] kv = param.split("=");
                keyValues.add(new KeyValuePair(kv[0], kv[1]));
            }
            return keyValues;
        } else {
            System.out.println("!! Body of this Request is empty !!");
            return null;
        }

    }

    public List<NameValuePair> getPostParam(String name){
        List<NameValuePair> nvps = getPostParams();
        List<NameValuePair> result = new ArrayList<>();
        for (NameValuePair nvp : nvps) {
            if(nvp.getName().equals(name)){
                result.add(nvp);
            }
        }
        return result;
    }

    public void getParts(){
        //todo
    }
    public void getPart(String name){
        //todo
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
