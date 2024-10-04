package io.dangernoodle.cpw.client;

import static io.dangernoodle.cpw.util.JsonDelegate.parameterStoreValue;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;


/**
 * Client for interacting with the <a href="https://tinyurl.com/c5kxf8xu">Parameter Store Lambda Extension</a>.
 */
public class PStoreClient
{
    private static final String ENDPOINT = "http://localhost:2773/systemsmanager/parameters/get";

    private final HttpClient client;

    private final String token;
    
    public PStoreClient(String token, HttpClient client)
    {
        this.token = token;
        this.client = client;
    }

    public String retrieve(String key, boolean decrypt) throws IOException, InterruptedException
    {
        HttpResponse<String> response = client.send(request(key, decrypt), HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200)
        {
            throw new IOException(response.body());
        }

        return parameterStoreValue(response.body());
    }

    private HttpRequest request(String key, boolean decrypt)
    {
        String encoded = URLEncoder.encode(key, StandardCharsets.US_ASCII);
        URI uri = URI.create(ENDPOINT + "?name=" + encoded + "&withDecryption=" + decrypt);

        return HttpRequest.newBuilder()
                          .GET()
                          .header("X-Aws-Parameters-Secrets-Token", token)
                          .uri(uri)
                          .build();
    }
}
