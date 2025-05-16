package io.dangernoodle.cpw.util;

/**
 * Retrieve environment variables from the lambda runtime.
 */
public class LambdaEnvironment
{
    public String sessionToken()
    {
        return System.getenv("AWS_SESSION_TOKEN");
    }

    public String slackAppToken()
    {
        return System.getenv("SLACK_APP_TOKEN");
    }

    public String slackChannel()
    {
        return System.getenv("SLACK_CHANNEL");
    }

    public String webhookPassword()
    {
        return System.getenv("WEBHOOK_PASSWORD");
    }

    public String webhookUsername()
    {
        return System.getenv("WEBHOOK_USERNAME");
    }
}
