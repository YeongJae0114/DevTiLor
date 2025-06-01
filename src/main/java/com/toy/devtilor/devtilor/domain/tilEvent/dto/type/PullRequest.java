package com.toy.devtilor.devtilor.domain.tilEvent.dto.type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PullRequest {
    private String title;
    private String url;
    private String body;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("review_comments")
    private int reviewComments;

    @JsonProperty("changed_files")
    private int changedFiles;

    private int additions;
}
