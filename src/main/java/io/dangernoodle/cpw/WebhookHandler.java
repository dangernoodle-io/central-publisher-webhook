package io.dangernoodle.cpw;

import static io.dangernoodle.cpw.util.JsonDelegate.deserialize;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.Duration;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.amazonaws.services.lambda.runtime.logging.LogLevel;
import io.dangernoodle.cpw.auth.Authorizer;
import io.dangernoodle.cpw.client.PStoreClient;
import io.dangernoodle.cpw.client.SlackClient;
import io.dangernoodle.cpw.model.Deployment;
import io.dangernoodle.cpw.util.LambdaEnvironment;


/**
 * Lambda request handler implementation for the webhook.
 */
public class WebhookHandler implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse>
{
    private final LambdaEnvironment environment;

    private final HttpClient httpClient;

    private final PStoreClient pStoreClient;

    public WebhookHandler()
    {
        this.httpClient = httpClient();
        this.environment = lambdaEnvironment();
        this.pStoreClient = pStoreClient();
    }

    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context)
    {
        LambdaLogger logger = context.getLogger();
        logger.log("event: " + event, LogLevel.DEBUG);

        Deployment deployment = deserialize(event.getBody(), Deployment.class);
        logger.log("deployment: " + deployment, LogLevel.DEBUG);

        try
        {
            APIGatewayV2HTTPResponse response = authorizer().authorize(event);
            if (response.getStatusCode() == 200)
            {
                String channel = pStoreClient.retrieve(environment.slackChannel(), false);
                slackClient().send(channel, deployment);

                return response(200, "Success");
            }

            return response;
        }
        catch (Exception e)
        {
            if (e instanceof InterruptedException)
            {
                Thread.currentThread().interrupt();
            }

            logger.log(e.getMessage(), LogLevel.ERROR);
            return response(500, "Internal server error");
        }
        finally
        {
            httpClient.close();
        }
    }

    // visible for testing
    Authorizer authorizer() throws IOException, InterruptedException
    {
        return new Authorizer(
            pStoreClient.retrieve(environment.centralUsername(), false),
            pStoreClient.retrieve(environment.centralPassword(), true)
        );
    }

    // visible for testing
    LambdaEnvironment lambdaEnvironment()
    {
        return new LambdaEnvironment();
    }

    // visible for testing
    PStoreClient pStoreClient()
    {
        return new PStoreClient(environment.sessionToken(), httpClient);
    }

    // visible for testing
    SlackClient slackClient() throws IOException, InterruptedException
    {
        String token = environment.slackAppToken();
        return new SlackClient(pStoreClient.retrieve(token, true), httpClient);
    }

    private HttpClient httpClient()
    {
        return HttpClient.newBuilder()
                         .connectTimeout(Duration.ofSeconds(20))
                         .build();
    }

    private APIGatewayV2HTTPResponse response(int statusCode, String body)
    {
        return APIGatewayV2HTTPResponse.builder()
                                       .withBody(body)
                                       .withStatusCode(statusCode)
                                       .build();
    }
}
