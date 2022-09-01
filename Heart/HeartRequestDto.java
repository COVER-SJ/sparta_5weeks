package com.example.intermediate.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeartRequestDto {


    private Long postId;
    private Long commnetId;
    private Long reCommentId;
}
