package com.example.login.reple;

import com.example.login.like.Like;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RepleService {

    private final com.example.intermediate.repository.CommentRepository commentRepository;
    private final com.example.intermediate.jwt.TokenProvider tokenProvider;
    private final com.example.intermediate.service.PostService postService;

    @Transactional
    public com.example.intermediate.controller.response.ResponseDto<?> createComment(com.example.intermediate.controller.request.CommentRequestDto requestDto, HttpServletRequest request) {
        if (null == request.getHeader("Refresh-Token")) {
            return com.example.intermediate.controller.response.ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return com.example.intermediate.controller.response.ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        com.example.intermediate.domain.Member member = validateMember(request);
        if (null == member) {
            return com.example.intermediate.controller.response.ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        com.example.intermediate.domain.Post post = postService.isPresentPost(requestDto.getPostId());
        if (null == post) {
            return com.example.intermediate.controller.response.ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
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

    @Transactional(readOnly = true)
    public com.example.intermediate.controller.response.ResponseDto<?> getAllCommentsByPost(Long postId) {
        com.example.intermediate.domain.Post post = postService.isPresentPost(postId);
        if (null == post) {
            return com.example.intermediate.controller.response.ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        }

        List<com.example.intermediate.domain.Comment> commentList = commentRepository.findAllByPost(post);
        List<com.example.intermediate.controller.response.CommentResponseDto> commentResponseDtoList = new ArrayList<>();

        for (com.example.intermediate.domain.Comment comment : commentList) {
            commentResponseDtoList.add(
                    com.example.intermediate.controller.response.CommentResponseDto.builder()
                            .id(comment.getId())
                            .author(comment.getMember().getNickname())
                            .content(comment.getContent())
                            .createdAt(comment.getCreatedAt())
                            .modifiedAt(comment.getModifiedAt())
                            .build()
            );
        }
        return com.example.intermediate.controller.response.ResponseDto.success(commentResponseDtoList);
    }

    @Transactional
    public com.example.intermediate.controller.response.ResponseDto<?> updateComment(Long id, com.example.intermediate.controller.request.CommentRequestDto requestDto, HttpServletRequest request) {
        if (null == request.getHeader("Refresh-Token")) {
            return com.example.intermediate.controller.response.ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return com.example.intermediate.controller.response.ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        com.example.intermediate.domain.Member member = validateMember(request);
        if (null == member) {
            return com.example.intermediate.controller.response.ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        com.example.intermediate.domain.Post post = postService.isPresentPost(requestDto.getPostId());
        if (null == post) {
            return com.example.intermediate.controller.response.ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        }

        com.example.intermediate.domain.Comment comment = isPresentComment(id);
        if (null == comment) {
            return com.example.intermediate.controller.response.ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 id 입니다.");
        }

        if (comment.validateMember(member)) {
            return com.example.intermediate.controller.response.ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
        }

        comment.update(requestDto);
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

    @Transactional
    public com.example.intermediate.controller.response.ResponseDto<?> deleteComment(Long id, HttpServletRequest request) {
        if (null == request.getHeader("Refresh-Token")) {
            return com.example.intermediate.controller.response.ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return com.example.intermediate.controller.response.ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

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

    @Transactional(readOnly = true)
    public com.example.intermediate.domain.Comment isPresentComment(Long id) {
        Optional<com.example.intermediate.domain.Comment> optionalComment = commentRepository.findById(id);
        return optionalComment.orElse(null);
    }

    @Transactional
    public com.example.intermediate.domain.Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }
}
