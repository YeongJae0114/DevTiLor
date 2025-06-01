package com.toy.devtilor.devtilor.domain.tilEvent.dto.type;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PullRequest {
    private String title;
    private String url;
    private String body;

    @JsonProperty("created_at")
    private String createdAt;
}
