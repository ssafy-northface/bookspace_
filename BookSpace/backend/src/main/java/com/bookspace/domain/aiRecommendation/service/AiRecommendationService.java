package com.bookspace.domain.aiRecommendation.service;

import com.bookspace.domain.aiRecommendation.dto.AiRecommendationRequestDto;
import com.bookspace.domain.aiRecommendation.dto.AiRecommendationResponseDto;

public interface AiRecommendationService {

    AiRecommendationResponseDto getRecommendedBooks(AiRecommendationRequestDto requestDto);

}
