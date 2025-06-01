package com.toy.devtilor.devtilor.domain.tilEvent.dto.type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WatchEventData implements EventPayload {
    @Override
    public String getEventType() {
        return "WatchEvent";
    }
}
