package com.toy.devtilor.devtilor.domain.tilEvent.controller;

import com.toy.devtilor.devtilor.domain.tilEvent.dto.GitHubEvent;
import com.toy.devtilor.devtilor.domain.tilEvent.service.GitHubEventService;
import com.toy.devtilor.devtilor.domain.tilEvent.service.TILPromptService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class TilController {
    private final GitHubEventService gitHubEventService;
    private final TILPromptService tilPromptService;

    @GetMapping("/api")
    public Mono<List<GitHubEvent>> test(){
        int toDayCount = gitHubEventService.countTodayEventsBlocking();

        return gitHubEventService.getRecentEvents(toDayCount);
    }
    @GetMapping("/api2")
    public String test2(){
        int toDayCount = gitHubEventService.countTodayEventsBlocking();
        String str = gitHubEventService.getRecentEvents(toDayCount).map(Object::toString).block();
        if (str==null){
            return "No";
        }
        return tilPromptService.createTIL(str.toString());
    }
}
