package com.toy.devtilor.devtilor.domain.tilEvent.dto.type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateEventData implements EventPayload{
    @Override
    public String getEventType() {
        return "CreateEvent";
    }
    private String ref;
    @JsonProperty("ref_type")
    private String refType;
    @JsonProperty("master_branch")
    private String masterBranch;
    private String description;
}
