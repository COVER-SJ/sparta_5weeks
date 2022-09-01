package com.example.login.like;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class LikeService {

//    강제로 path 가 들어감.
    private final com.example.intermediate.jwt.TokenProvider tokenProvider;

//    좋아요 등록 API
    @Transactional
    public com.example.intermediate.controller.response.ResponseDto<?> createComment(com.example.intermediate.controller.request.CommentRequestDto requestDto, HttpServletRequest request) {

//        AccessToken이 있고, 유효한 Token일 때만 요청 가능하도록 하기
        if (null == request.getHeader("Refresh-Token")) {
            return com.example.intermediate.controller.response.ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return com.example.intermediate.controller.response.ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

//        게시글, 댓글, 대댓글 reponse에 좋아요 개수 함께 나타내기
        Like like = validateMember(request);
        if (null == Like) {
            return com.example.intermediate.controller.response.ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }


        com.example.intermediate.domain.Comment comment = com.example.intermediate.domain.Comment.builder()
                .member(member)
                .post(post)
                .content(requestDto.getContent())
                .build();
        commentRepository.save(comment);
        return com.example.intermediate.controller.response.ResponseDto.success(
                com.example.intermediate.controller.response.CommentResponseDto.builder()
                        .id(comment.getId())
                        .author(comment.getMember().getNickname())
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt())
                        .modifiedAt(comment.getModifiedAt())
                        .build()
        );
    }


//    좋아요 취소  API
    @Transactional
    public com.example.intermediate.controller.response.ResponseDto<?> deleteComment(Long id, HttpServletRequest request) {
//        AccessToken이 있고, 유효한 Token일 때만 요청 가능하도록 하기
        if (null == request.getHeader("Refresh-Token")) {
            return com.example.intermediate.controller.response.ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return com.example.intermediate.controller.response.ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }


//        게시글, 댓글, 대댓글 reponse에 좋아요 개수 함께 나타내기
        com.example.intermediate.domain.Member member = validateMember(request);
        if (null == member) {
            return com.example.intermediate.controller.response.ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        com.example.intermediate.domain.Comment comment = isPresentComment(id);
        if (null == comment) {
            return com.example.intermediate.controller.response.ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 id 입니다.");
        }

        if (comment.validateMember(member)) {
            return com.example.intermediate.controller.response.ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
        }

        commentRepository.delete(comment);
        return com.example.intermediate.controller.response.ResponseDto.success("success");
    }

    @Transactional
    public Like validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }
}
