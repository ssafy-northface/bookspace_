package com.bookspace.domain.aiRecommendation.service;

import com.bookspace.domain.aiRecommendation.dto.AiRecommendationResponseDto;
import com.bookspace.domain.aiRecommendation.dto.ApiRequestDto;
import com.bookspace.domain.aiRecommendation.dto.ApiResponseDto;

public interface AiRecommendationService {

    AiRecommendationResponseDto getRecommendedBooks(ApiRequestDto requestDto);

}
