package io.dangernoodle.cpw.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class PStoreClientTest
{
    private static String JSON = """
        {
          "Parameter": {
            "Value": "central-publisher"
          }
        }
        """;

    @Captor
    private ArgumentCaptor<HttpRequest> captor;

    @Mock
    private HttpClient mockClient;

    @Mock
    private HttpResponse<String> mockResponse;

    private PStoreClient pStoreClient;

    private String value;

    @BeforeEach
    public void beforeEach()
    {
        pStoreClient = new PStoreClient("TOKEN", mockClient);
    }

    @Test
    public void testRetrieve() throws Exception
    {
        givenAnHttpRequest();
        givenA200StatusCode();
        whenRetrieve();
        thenParameterRetrieved();
    }

    @Test
    public void testRetrieveFailure() throws Exception
    {
        givenAnHttpRequest();
        givenANon200StatusCode();
        thenIOExceptionIsThrown();
        thenHttpRequestIsValid();
    }

    private void givenA200StatusCode()
    {
        when(mockResponse.body()).thenReturn(JSON);
        when(mockResponse.statusCode()).thenReturn(200);
    }

    private void givenANon200StatusCode()
    {
        when(mockResponse.statusCode()).thenReturn(500);
    }

    private void givenAnHttpRequest() throws IOException, InterruptedException
    {
        when(mockClient.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(mockResponse);
    }

    private void thenHttpRequestIsValid() throws IOException, InterruptedException
    {
        verify(mockClient).send(captor.capture(), eq(HttpResponse.BodyHandlers.ofString()));
        HttpRequest request = captor.getValue();

        assertEquals("GET", request.method());
        assertEquals("TOKEN", request.headers().map().get("X-Aws-Parameters-Secrets-Token").getFirst());
        assertEquals(URI.create("http://localhost:2773/systemsmanager/parameters/get?name=key&withDecryption=false"),
            request.uri());
    }

    private void thenIOExceptionIsThrown()
    {
        assertThrows(IOException.class, this::whenRetrieve);
    }

    private void thenParameterRetrieved()
    {
        assertEquals("central-publisher", value);
    }

    private void whenRetrieve() throws IOException, InterruptedException
    {
        value = pStoreClient.retrieve("key", false);
    }
}
