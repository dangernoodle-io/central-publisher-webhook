package io.dangernoodle.cpw.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;


@ExtendWith(SystemStubsExtension.class)
public class LambdaEnviromentTest
{
    private LambdaEnvironment environment;

    @SystemStub
    private EnvironmentVariables variables = setupEnvironmentVariables();

    @BeforeEach
    public void beforeEach()
    {
        environment = new LambdaEnvironment();
    }

    @Test
    public void testCentralPassword()
    {
        assertEquals("password", environment.centralPassword());
    }

    @Test
    public void testCentralUsername()
    {
        assertEquals("username", environment.centralUsername());
    }

    @Test
    public void testSessionToken()
    {
        assertEquals("aws_session_token", environment.sessionToken());
    }

    @Test
    public void testSlackAppToken()
    {
        assertEquals("slack_app_token", environment.slackAppToken());
    }

    @Test
    public void testSlackChannel()
    {
        assertEquals("channel", environment.slackChannel());
    }

    private static EnvironmentVariables setupEnvironmentVariables()
    {
        return new EnvironmentVariables(Map.of(
            "AWS_SESSION_TOKEN", "aws_session_token",
            "SSM_CENTRAL_PASSWORD", "password",
            "SSM_CENTRAL_USERNAME", "username",
            "SSM_SLACK_APP_TOKEN", "slack_app_token",
            "SSM_SLACK_CHANNEL", "channel"
        ));
    }
}
