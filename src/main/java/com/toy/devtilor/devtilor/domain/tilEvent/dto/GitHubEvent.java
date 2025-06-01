package com.toy.devtilor.devtilor.domain.tilEvent.dto;

import com.toy.devtilor.devtilor.domain.tilEvent.dto.type.EventPayload;

public class GitHubEvent {
    private String id;
    private String type;
    private ActorDto actor;
    private RepoDto repo;
    private String createdAt;
    private EventPayload payload;
}
