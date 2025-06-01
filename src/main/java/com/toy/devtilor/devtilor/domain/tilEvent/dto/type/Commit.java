package com.toy.devtilor.devtilor.domain.tilEvent.dto.type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Commit {
    private String sha;
    private String message;
    private String url;
}