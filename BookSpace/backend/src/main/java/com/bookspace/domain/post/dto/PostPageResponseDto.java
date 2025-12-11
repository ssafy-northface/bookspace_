package com.bookspace.domain.post.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostPageResponseDto {

    private Content content;
    private boolean hasNext;
    private Integer nextPage;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Content {
        private List<PostResponseDto> posts;
        private int page;
        private int size;
        private int totalElements;
    }
}
