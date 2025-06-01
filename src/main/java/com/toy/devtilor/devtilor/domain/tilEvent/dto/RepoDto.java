package com.toy.devtilor.devtilor.domain.tilEvent.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RepoDto {
    private String name;
    private String url;
}
