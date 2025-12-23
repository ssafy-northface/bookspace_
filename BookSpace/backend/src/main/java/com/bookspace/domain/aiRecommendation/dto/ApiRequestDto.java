package com.bookspace.domain.aiRecommendation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApiRequestDto {

    // 사용자가 입력한 문장
    private String userRequest;

}