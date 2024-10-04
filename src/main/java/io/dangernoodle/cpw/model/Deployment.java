package io.dangernoodle.cpw.model;

import static com.slack.api.model.block.Blocks.asBlocks;
import static com.slack.api.model.block.Blocks.divider;
import static com.slack.api.model.block.Blocks.header;
import static com.slack.api.model.block.Blocks.section;
import static com.slack.api.model.block.composition.BlockCompositions.asSectionFields;
import static com.slack.api.model.block.composition.BlockCompositions.markdownText;
import static com.slack.api.model.block.composition.BlockCompositions.plainText;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.slack.api.model.block.LayoutBlock;


/**
 * Represents the payload received by the webhook.
 *
 * @param deploymentId Unique ID of deployment
 * @param timestamp Notification timestamp
 * @param status Deployment state
 * @param packageUrls PURLs of deployed artifacts
 * @param centralPaths Deployed artifact urls
 */
public record Deployment(UUID deploymentId, long timestamp, String status, String[] packageUrls, String[] centralPaths)
{
    private static final String FAILED = "FAILED";

    private static final String PUBLISHED = "PUBLISHED";

    private static final String PUBLISHING = "PUBLISHING";

    private static final String VALIDATED = "VALIDATED";

    public List<LayoutBlock> asSlackBlocks()
    {
        List<LayoutBlock> blocks = asBlocks(
            header(header -> header.text(plainText("Maven Central Deployment"))),
            divider(),
            section(section -> section.text(markdownText("*Deployment Id:*\n" + deploymentId))),
            section(section -> section.fields(asSectionFields(
                markdownText("*Status:*\n" + formatStatus()),
                markdownText("*Timestamp:*\n" + formatTimestamp()))
            )),
            section(section -> section.text(markdownText("*Artifact Package Urls:*\n" +
                String.join("\n", packageUrls))))
        );

        if (PUBLISHED.equals(status))
        {
            blocks = new ArrayList<>(blocks);
            blocks.add(section(section -> section.text(markdownText("*Artifact Links:*\n" +
                String.join("\n", createLinks(centralPaths))))));
        }

        return blocks;
    }

    private String createLinks(String[] urls)
    {
        return Stream.of(urls)
                     .map(url -> String.format("<%s|%s>", url, url.substring(url.lastIndexOf("/") + 1)))
                     .collect(Collectors.joining("\n"));
    }

    private String formatStatus()
    {
        String emoji = switch (status)
        {
            case FAILED -> "x";
            case PUBLISHED -> "tada";
            case PUBLISHING -> "construction_worker";
            case VALIDATED -> "white_check_mark";
            default -> "grey_question";
        };

        return String.format(":%s: %s", emoji,
            status().charAt(0) + status().substring(1).toLowerCase());
    }

    private String formatTimestamp()
    {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
                            .format(DateTimeFormatter.RFC_1123_DATE_TIME);
    }
}
