package io.dangernoodle.cpw.model;

import static io.dangernoodle.cpw.util.JsonDelegate.serialize;
import static net.javacrumbs.jsonunit.JsonAssert.assertJsonEquals;
import static net.javacrumbs.jsonunit.JsonAssert.whenIgnoringPaths;

import java.util.TimeZone;
import java.util.UUID;

import io.hosuaby.inject.resources.junit.jupiter.GivenTextResource;
import io.hosuaby.inject.resources.junit.jupiter.TestWithResources;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.properties.SystemProperties;


@TestWithResources
@ExtendWith(SystemStubsExtension.class)
public class DeploymentTest
{
    static
    {
        // force a consistent timezone across all envs
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @GivenTextResource("failed-slack-blocks.json")
    String failedBlocks;

    @GivenTextResource("published-slack-blocks.json")
    String publishedBlocks;

    @GivenTextResource("publishing-slack-blocks.json")
    String publishingBlocks;

    @GivenTextResource("unknown-slack-blocks.json")
    String unknownBlocks;

    @GivenTextResource("validated-slack-blocks.json")
    String validatedBlocks;

    private String blocks;

    private Deployment deployment;

    @Test
    public void testFailedBlocks()
    {
        givenAFailedDeployment();
        whenConvertToSlackBlocks();
        thenFailedJSONMatches();
    }

    @Test
    public void testPublishedBlocks()
    {
        givenAPublishedDeployment();
        whenConvertToSlackBlocks();
        thenPublishedJSONMatches();
    }

    @Test
    public void testPublishingBlocks()
    {
        givenAPublishingDeployment();
        whenConvertToSlackBlocks();
        thenPublishingJSONMatches();
    }

    @Test
    public void testUnknownBlocks()
    {
        givenAnUnknownDeployment();
        whenConvertToSlackBlocks();
        thenUnknownJSONMatches();
    }

    @Test
    public void testValidatedBlocks()
    {
        givenAValidatedDeployment();
        whenConvertToSlackBlocks();
        thenValidatedJSONMatches();
    }

    private Deployment createDeployment(String status)
    {
        return new Deployment(UUID.fromString("bd0aa1fe-96ea-4176-97e4-65404b5d584c"), 1710000000000L,
            status, new String[]{"packageUrl"}, new String[]{"centralPaths"});
    }

    private void givenAFailedDeployment()
    {
        deployment = createDeployment("FAILED");
    }

    private void givenAPublishedDeployment()
    {
        deployment = createDeployment("PUBLISHED");
    }

    private void givenAPublishingDeployment()
    {
        deployment = createDeployment("PUBLISHING");
    }

    private void givenAValidatedDeployment()
    {
        deployment = createDeployment("VALIDATED");
    }

    private void givenAnUnknownDeployment()
    {
        deployment = createDeployment("UNKNOWN");
    }

    private void thenFailedJSONMatches()
    {
        assertJsonEquals(failedBlocks, blocks, whenIgnoringPaths());
        //JSONAssert.assertEquals(failedBlocks, blocks, true);
    }

    private void thenPublishedJSONMatches()
    {
        JSONAssert.assertEquals(publishedBlocks, blocks, true);
    }

    private void thenPublishingJSONMatches()
    {
        JSONAssert.assertEquals(publishingBlocks, blocks, true);
    }

    private void thenUnknownJSONMatches()
    {
        JSONAssert.assertEquals(unknownBlocks, blocks, true);
    }

    private void thenValidatedJSONMatches()
    {
        JSONAssert.assertEquals(validatedBlocks, blocks, true);
    }

    private void whenConvertToSlackBlocks()
    {
        blocks = serialize(deployment.asSlackBlocks());
    }
}
