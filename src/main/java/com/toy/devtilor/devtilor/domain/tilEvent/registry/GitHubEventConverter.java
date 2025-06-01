package com.toy.devtilor.devtilor.domain.tilEvent.registry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toy.devtilor.devtilor.domain.tilEvent.dto.ActorDto;
import com.toy.devtilor.devtilor.domain.tilEvent.dto.GitHubEvent;
import com.toy.devtilor.devtilor.domain.tilEvent.dto.RepoDto;
import com.toy.devtilor.devtilor.domain.tilEvent.dto.type.EventPayload;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GitHubEventConverter {
    private final ObjectMapper objectMapper;

    public GitHubEvent convert(JsonNode node) {
        try {
            String type = node.get("type").asText();

            Class<? extends EventPayload> payloadClass = EventPayloadRegistry.getPayloadClass(type);

            return GitHubEvent.builder()
                    .id(node.get("id").asText())
                    .type(type)
                    .actor(objectMapper.treeToValue(node.get("actor"), ActorDto.class))
                    .repo(objectMapper.treeToValue(node.get("repo"), RepoDto.class))
                    .createdAt(node.get("created_at").asText())
                    .payload(objectMapper.treeToValue(node.get("payload"), payloadClass))
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("GitHub 이벤트 파싱 실패", e);
        }
    }
}
