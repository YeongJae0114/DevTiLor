package com.toy.devtilor.devtilor.domain.tilEvent.dto.type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PullRequestEventData implements EventPayload {
    @Override
    public String getEventType() {
        return "PullRequestEvent";
    }

    private String ref;
    private PullRequest pull_request;
}
