package com.bookspace.domain.aiRecommendation.controller;

import com.bookspace.domain.aiRecommendation.dto.ApiRequestDto;
import com.bookspace.domain.aiRecommendation.dto.AiRecommendationResponseDto;
import com.bookspace.domain.aiRecommendation.service.AiRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommend")
public class AiRecommendationController {

    @Autowired
    private AiRecommendationService recommendationService;

    // 사용자 요청을 받아 도서 추천을 반환하는 API
    @PostMapping
    public AiRecommendationResponseDto getRecommendation(@RequestBody ApiRequestDto requestDto) {
        return recommendationService.getRecommendedBooks(requestDto);
    }
}
