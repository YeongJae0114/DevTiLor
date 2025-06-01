package com.toy.devtilor.devtilor.domain.tilEvent.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toy.devtilor.devtilor.domain.tilEvent.dto.GitHubEvent;
import com.toy.devtilor.devtilor.domain.tilEvent.registry.GitHubEventConverter;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GitHubEventService {
    private final WebClient githubWebClient;

    public Mono<List<GitHubEvent>> getRecentEvents(int perPage) {
        return githubWebClient.get()                      // GET 요청
                .uri(uriBuilder ->
                        uriBuilder
                                .queryParam("per_page", perPage)
                                .build())
                .retrieve()
                .bodyToMono(String.class)
                .map(this::convertToEvents);                // 문자열로 변환
    }

    public int countTodayEventsBlocking() {
        String response = githubWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("per_page", 30)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block(); // 블로킹

        List<GitHubEvent> events = convertToEvents(response);

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        return (int) events.stream()
                .takeWhile(event -> isToday(event.getCreatedAt(), today))
                .count();
    }

    private boolean isToday(String createdAtUtc, LocalDate today) {
        Instant instant = Instant.parse(createdAtUtc);
        ZonedDateTime kstTime = instant.atZone(ZoneId.of("Asia/Seoul"));

        return kstTime.toLocalDate().isEqual(today);
    }

    private List<GitHubEvent> convertToEvents(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            GitHubEventConverter converter = new GitHubEventConverter(mapper);

            JsonNode root = mapper.readTree(json);
            List<GitHubEvent> results = new ArrayList<>();

            for (JsonNode node : root) {
                results.add(converter.convert(node)); // 개별 이벤트 파싱
            }

            return results;
        } catch (Exception e) {
            throw new RuntimeException("이벤트 파싱 중 오류 발생", e);
        }
    }
}
