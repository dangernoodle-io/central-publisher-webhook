package io.dangernoodle.cpw.auth;

import java.util.Base64;
import java.util.Collections;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;


/**
 * Handles authorization of the incoming webhook request.
 * <p>
 * Possible response codes:
 * <ul>
 *     <li>200 on successful authorization</li>
 *     <li>401 if no authorization header was passed</li>
 *     <li>403 if the request is unauthorized</li>
 * </ul>
 * </p>
 */
public class Authorizer
{
    private static final String AUTHENTICATE = "WWW-Authenticate";

    private static final String AUTHORIZATION = "authorization";

    private static final String AUTH_SCHEME = "Basic realm=central-publisher";

    private final String password;

    private final String username;

    public Authorizer(String username, String password)
    {
        this.username = username;
        this.password = password;
    }

    public APIGatewayV2HTTPResponse authorize(APIGatewayV2HTTPEvent event)
    {
        Map<String, String> headers = event.getHeaders();
        String authorization = headers.get(AUTHORIZATION);

        if (authorization == null)
        {
            return response(401, null, Map.of(AUTHENTICATE, AUTH_SCHEME));
        }

        if (!encode().equals(authorization))
        {
            return response(403, "Unauthorized");
        }

        return response(200, null);
    }

    private String encode()
    {
        return "Basic " + Base64.getEncoder()
                                .encodeToString((username + ":" + password).getBytes());
    }

    private APIGatewayV2HTTPResponse response(int statusCode, String body)
    {
        return response(statusCode, body, Collections.emptyMap());
    }

    private APIGatewayV2HTTPResponse response(int statusCode, String body, Map<String, String> headers)
    {
        return APIGatewayV2HTTPResponse.builder()
                                       .withBody(body)
                                       .withHeaders(headers)
                                       .withStatusCode(statusCode)
                                       .build();
    }
}
