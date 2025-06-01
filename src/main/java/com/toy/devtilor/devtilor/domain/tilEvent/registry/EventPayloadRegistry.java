package com.toy.devtilor.devtilor.domain.tilEvent.registry;

import com.toy.devtilor.devtilor.domain.tilEvent.dto.type.CreateEventData;
import com.toy.devtilor.devtilor.domain.tilEvent.dto.type.EventPayload;
import com.toy.devtilor.devtilor.domain.tilEvent.dto.type.ForkEventData;
import com.toy.devtilor.devtilor.domain.tilEvent.dto.type.IssuesEventData;
import com.toy.devtilor.devtilor.domain.tilEvent.dto.type.PullRequestEventData;
import com.toy.devtilor.devtilor.domain.tilEvent.dto.type.PushEventData;
import com.toy.devtilor.devtilor.domain.tilEvent.dto.type.WatchEventData;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class EventPayloadRegistry {
    private static final Map<String, Class<? extends EventPayload>> typeToClassMap = Map.of(
            "PushEvent", PushEventData.class,
            "IssuesEvent", IssuesEventData.class,
            "PullRequestEvent", PullRequestEventData.class,
            "ForkEvent", ForkEventData.class,
            "WatchEvent", WatchEventData.class,
            "CreateEvent", CreateEventData.class
    );

    public static Class<? extends EventPayload> getPayloadClass(String type) {
        return typeToClassMap.get(type);
    }

    public static boolean supports(String type) {
        return typeToClassMap.containsKey(type);
    }
}
