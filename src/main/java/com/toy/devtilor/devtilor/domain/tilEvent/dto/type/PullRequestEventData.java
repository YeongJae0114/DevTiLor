package com.toy.devtilor.devtilor.domain.tilEvent.dto.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class PullRequestEventData implements EventPayload {
    @Override
    public String getEventType() {
        return "PullRequestEvent";
    }

    private String ref;
    private List<Commit> commits;
    private PullRequest pullRequest;

    @JsonProperty("review_comments")
    private int reviewComments;

    @JsonProperty("review_comments")
    private int reviewComment;

    @JsonProperty("changed_files")
    private int changedFiles;

    private int additions;
}
