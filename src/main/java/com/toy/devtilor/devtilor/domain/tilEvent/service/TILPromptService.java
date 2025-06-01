package com.toy.devtilor.devtilor.domain.tilEvent.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TILPromptService {
    private final ChatClient chatClient;

    @Value("${til.basePrompt}")
    private String basePrompt;

    TILPromptService (ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    public String createTIL(String prompt) {
        String fullPrompt = basePrompt + "\n "+prompt;

        return chatClient
                .prompt()         // prompt 시작
                .user(fullPrompt)     // 유저 메시지 입력
                .call()           // 실제 API 호출
                .content();       // 결과 내용 반환
    }
}
