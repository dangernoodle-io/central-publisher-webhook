package io.dangernoodle.cpw.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

import io.dangernoodle.cpw.model.Deployment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class SlackClientTest
{
    private static final Deployment deployment = new Deployment(UUID.randomUUID(), 1710000000000L,
        "PUBLISHED", new String[]{}, new String[]{});

    @Captor
    private ArgumentCaptor<HttpRequest> captor;

    @Mock
    private HttpClient mockClient;

    @Mock
    private HttpResponse<String> mockResponse;

    private SlackClient slackClient;

    @BeforeEach
    public void beforeEach()
    {
        slackClient = new SlackClient("token", mockClient);
    }

    @Test
    public void testSend() throws Exception
    {
        givenAnHttpRequest();
        givenA200StatusCode();
        givenATrueOkInBody();
        whenSendMessage();
        thenMessageIsSent();
    }

    @Test
    public void testSendBodyError() throws Exception
    {
        givenAnHttpRequest();
        givenA200StatusCode();
        givenAnFalseOkInBody();
        thenIOExceptionIsThrown();
    }

    @Test
    public void testSendError() throws Exception
    {
        givenAnHttpRequest();
        givenANon200StatusCode();
        thenIOExceptionIsThrown();
    }

    private void givenA200StatusCode()
    {
        when(mockResponse.statusCode()).thenReturn(200);
    }

    private void givenANon200StatusCode()
    {
        when(mockResponse.statusCode()).thenReturn(500);
    }

    private void givenATrueOkInBody()
    {
        when(mockResponse.body()).thenReturn("{ \"ok\":true, \"other\": \"other\" }");
    }

    private void givenAnFalseOkInBody()
    {
        when(mockResponse.body()).thenReturn("{ \"ok\":false, \"other\": \"other\" }");
    }

    private void givenAnHttpRequest() throws IOException, InterruptedException
    {
        when(mockClient.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(mockResponse);
    }

    private void thenIOExceptionIsThrown()
    {
        assertThrows(IOException.class, this::whenSendMessage);
    }

    private void thenMessageIsSent() throws IOException, InterruptedException
    {
        verify(mockClient).send(captor.capture(), eq(HttpResponse.BodyHandlers.ofString()));
        HttpRequest request = captor.getValue();

        assertEquals("POST", request.method());
        // best we can do for the content
        assertTrue(request.bodyPublisher().get().contentLength() > 0);
        assertEquals("Bearer token", request.headers().map().get("Authorization").getFirst());
    }

    private void whenSendMessage() throws IOException, InterruptedException
    {
        slackClient.send("channel", deployment);
    }
}
