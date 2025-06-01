package com.toy.devtilor.devtilor.domain.tilEvent.dto.type;

import java.util.List;

public class PushEventData implements EventPayload {
    @Override
    public String getEventType() {
        return "PushEvent";
    }
    private String ref;
    private List<Commit> commits;
}
