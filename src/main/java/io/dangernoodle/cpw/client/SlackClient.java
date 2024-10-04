package io.dangernoodle.cpw.client;

import static io.dangernoodle.cpw.util.JsonDelegate.serialize;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.slack.api.webhook.Payload;
import io.dangernoodle.cpw.model.Deployment;


/**
 * Client for interacting with Slack.
 */
public class SlackClient
{
    private static final URI endpoint = URI.create("https://slack.com/api/chat.postMessage");

    private final HttpClient client;

    private final String token;

    public SlackClient(String token, HttpClient client)
    {
        this.client = client;
        this.token = token;
    }

    public void send(String channel, Deployment deployment) throws IOException, InterruptedException
    {
        Payload payload = Payload.builder()
                                 .blocks(deployment.asSlackBlocks())
                                 .channel(channel)
                                 .build();

        HttpRequest request = createRequest(serialize(payload));
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String body = response.body();
        if (response.statusCode() != 200 || body.contains("\"ok\":false"))
        {
            throw new IOException(response.body());
        }
    }

    private HttpRequest createRequest(String message)
    {
        return HttpRequest.newBuilder()
                          .POST(HttpRequest.BodyPublishers.ofString(message))
                          .header("Authorization", "Bearer " + token)
                          .header("Accept", "application/json")
                          .header("Content-Type", "application/json; charset=utf-8")
                          .uri(endpoint)
                          .build();
    }
}
