package io.dangernoodle.cpw.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Base64;
import java.util.Collections;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class AuthorizerTest
{
    @Mock
    private APIGatewayV2HTTPEvent mockEvent;

    private Authorizer authorizer;

    private APIGatewayV2HTTPResponse event;

    @BeforeEach
    public void setup()
    {
        authorizer = new Authorizer("username", "password");
    }

    @Test
    public void testAuthorized()
    {
        givenAnAuthorizedEvent();
        whenAuthorize();
        thenEventIsAuthorized();
    }

    @Test
    public void testRequiresAuthorization()
    {
        givenARequiresAuthEvent();
        whenAuthorize();
        thenEventRequiresAuth();
    }

    @Test
    public void testUnauthorized()
    {
        givenAnUnauthorizedEvent();
        whenAuthorize();
        thenEventIsUnauthorized();
    }

    private String encode(String username, String password)
    {
        return Base64.getEncoder()
                     .encodeToString((username + ":" + password).getBytes());
    }

    private void givenARequiresAuthEvent()
    {
        when(mockEvent.getHeaders()).thenReturn(Collections.emptyMap());
    }

    private void givenAnAuthorizedEvent()
    {
        when(mockEvent.getHeaders())
            .thenReturn(Map.of("authorization", "Basic " + encode("username", "password")));
    }

    private void givenAnUnauthorizedEvent()
    {
        when(mockEvent.getHeaders())
            .thenReturn(Map.of("authorization", "Basic: " + encode("invalid", "credentials")));

    }

    private void thenEventIsAuthorized()
    {
        assertEquals(200, event.getStatusCode());
    }

    private void thenEventIsUnauthorized()
    {
        assertEquals(403, event.getStatusCode());
    }

    private void thenEventRequiresAuth()
    {
        assertEquals(401, event.getStatusCode());
    }

    private void whenAuthorize()
    {
        event = authorizer.authorize(mockEvent);
    }
}
