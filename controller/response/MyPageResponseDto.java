package com.example.intermediate.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MyPageResponseDto {
    private Long id;
    private List<PostResponseDto> postResponseDtoList;
    private List<CommentResponseDto> commentResponseDtoList;
    private List<ReCommentResponseDto> subCommentResponseDtoList;
    private List<PostResponseDto> heartPostResponseDtoList;
    private List<CommentResponseDto> heartCommentResponseDto;

}