package io.dangernoodle.cpw.util;

/**
 * Retrieve environment variables from the lambda runtime.
 */
public class LambdaEnvironment
{
    public String centralPassword()
    {
        return System.getenv("SSM_CENTRAL_PASSWORD");
    }

    public String centralUsername()
    {
        return System.getenv("SSM_CENTRAL_USERNAME");
    }

    public String sessionToken()
    {
        return System.getenv("AWS_SESSION_TOKEN");
    }

    public String slackAppToken()
    {
        return System.getenv("SSM_SLACK_APP_TOKEN");
    }

    public String slackChannel()
    {
        return System.getenv("SSM_SLACK_CHANNEL");
    }
}
