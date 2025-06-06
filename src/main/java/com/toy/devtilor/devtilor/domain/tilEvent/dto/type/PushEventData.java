package com.toy.devtilor.devtilor.domain.tilEvent.dto.type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PushEventData implements EventPayload {
    @Override
    public String getEventType() {
        return "PushEvent";
    }
    private String ref;
    private List<Commit> commits;
}
