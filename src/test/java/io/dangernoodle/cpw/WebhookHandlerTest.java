package io.dangernoodle.cpw;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import io.dangernoodle.cpw.auth.Authorizer;
import io.dangernoodle.cpw.client.PStoreClient;
import io.dangernoodle.cpw.client.SlackClient;
import io.dangernoodle.cpw.model.Deployment;
import io.dangernoodle.cpw.util.LambdaEnvironment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class WebhookHandlerTest
{
    private static final String DEPLOYMENT = """
        {
          "deploymentId": "48d72417-6541-4e8b-affe-50096fdd0c58",
          "timestamp": 1710000000000,
          "status": "VALIDATED",
          "packageUrls": [
            "pkg:maven/org.sonatype.central.test/exampl@1.0.0"
          ],
          "centralPaths": [
            "https://repo1.maven.org/maven2/org/sonatype/central/test/example/1.0.0/example-1.0.0.pom"
          ]
        }
        """;

    @Captor
    private ArgumentCaptor<Deployment> captor;

    private WebhookHandler handler;

    @Mock
    private Authorizer mockAuthorizer;

    @Mock
    private Context mockContext;

    @Mock
    private LambdaEnvironment mockEnvironment;

    @Mock
    private APIGatewayV2HTTPEvent mockEvent;

    @Mock
    private LambdaLogger mockLogger;

    @Mock
    private PStoreClient mockPStoreClient;

    @Mock
    private APIGatewayV2HTTPResponse mockResponse;

    @Mock
    private SlackClient mockSlackClient;

    private APIGatewayV2HTTPResponse response;

    @BeforeEach
    public void beforeEach() throws Exception
    {
        when(mockEvent.getBody()).thenReturn(DEPLOYMENT);
        when(mockContext.getLogger()).thenReturn(mockLogger);
        when(mockAuthorizer.authorize(mockEvent)).thenReturn(mockResponse);

        handler = new WebhookHandler()
        {
            @Override
            Authorizer authorizer()
            {
                return mockAuthorizer;
            }

            @Override
            LambdaEnvironment lambdaEnvironment()
            {
                return mockEnvironment;
            }

            @Override
            PStoreClient pStoreClient()
            {
                return mockPStoreClient;
            }

            @Override
            SlackClient slackClient() throws IOException, InterruptedException
            {
                return mockSlackClient;
            }
        };
    }

    @Test
    public void testIOException() throws Exception
    {
        givenAnIOException();
        givenAnAuthorizedRequest();
        whenHandleReqeust();
        thenResponseIs500();
    }

    @Test
    public void testInteruptedException() throws Exception
    {
        givenAnInteruptedException();
        givenAnAuthorizedRequest();
        whenHandleReqeust();
        thenResponseIs500();
        thenThreadInterupted();
    }

    @Test
    public void testRequestHandled() throws Exception
    {
        givenAnAuthorizedRequest();
        whenHandleReqeust();
        thenResponseIs200();
        thenSlackClientInvoked();
    }

    @Test
    public void testRequestUnauthorized() throws Exception
    {
        givenAnUnauthorizedRequest();
        whenHandleReqeust();
        thenResponseIs403();
        thenSlackClientNotInvoked();
    }

    private void givenAnAuthorizedRequest() throws IOException, InterruptedException
    {
        when(mockResponse.getStatusCode()).thenReturn(200);
        when(mockEnvironment.slackChannel()).thenReturn("SSM_SLACK_CHANNEL");
        when(mockPStoreClient.retrieve("SSM_SLACK_CHANNEL", false)).thenReturn("channel");
    }

    private void givenAnIOException() throws IOException, InterruptedException
    {
        doThrow(IOException.class).when(mockSlackClient).send(anyString(), any());
    }

    private void givenAnInteruptedException() throws IOException, InterruptedException
    {
        doThrow(InterruptedException.class).when(mockSlackClient).send(anyString(), any());
    }

    private void givenAnUnauthorizedRequest()
    {
        when(mockResponse.getStatusCode()).thenReturn(403);
    }

    private void thenResponseIs200()
    {
        assertEquals(200, response.getStatusCode());
    }

    private void thenResponseIs403()
    {
        assertEquals(403, response.getStatusCode());
    }

    private void thenResponseIs500()
    {
        assertEquals(500, response.getStatusCode());
    }

    private void thenSlackClientInvoked() throws Exception
    {
        verify(mockSlackClient).send(eq("channel"), captor.capture());

        Deployment deployment = captor.getValue();
        assertEquals("48d72417-6541-4e8b-affe-50096fdd0c58", deployment.deploymentId().toString());
    }

    private void thenSlackClientNotInvoked()
    {
        verifyNoInteractions(mockSlackClient);
    }

    private void thenThreadInterupted()
    {
        assertTrue(Thread.interrupted());
    }

    private void whenHandleReqeust()
    {
        response = handler.handleRequest(mockEvent, mockContext);
    }
}
