package com.example.intermediate.service;

import com.example.intermediate.controller.request.CommentRequestDto;
import com.example.intermediate.controller.request.ReCommentRequestDto;
import com.example.intermediate.controller.response.CommentResponseDto;
import com.example.intermediate.controller.response.ReCommentResponseDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.domain.Comment;
import com.example.intermediate.domain.Member;
import com.example.intermediate.domain.Post;
import com.example.intermediate.domain.ReComment;
import com.example.intermediate.jwt.TokenProvider;
import com.example.intermediate.repository.ReCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ReCommentService {

    private final TokenProvider tokenProvider;
    private final PostService postService;
    private final CommentService commentService;
    private final ReCommentRepository reCommentRepository;


    @Transactional
    public ResponseDto<?> createReComment(ReCommentRequestDto reCommentRequestDto, HttpServletRequest request) {
        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        Comment comment = commentService.isPresentComment(reCommentRequestDto.getCommentId());
        if (null == comment) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 id 입니다.");
        }

        Post post = postService.isPresentPost(comment.getPost().getId());

        ReComment reComment = ReComment.builder()
                .member(member)
                .post(post)
                .comment(comment)
                .reContent(reCommentRequestDto.getContent())
                .build();
        reCommentRepository.save(reComment);
        return ResponseDto.success(
                ReCommentResponseDto.builder()
                        .id(reComment.getId())
                        .author(reComment.getMember().getNickname())
                        .reContent(reComment.getReContent())
                        .createdAt(reComment.getCreatedAt())
                        .modifiedAt(reComment.getModifiedAt())
                        .build()
        );
    }

    @Transactional(readOnly = true)
    public ResponseDto<?> getAllReCommentsByComment(Long commentId) {
        Comment comment = commentService.isPresentComment(commentId);
        if (null == comment) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 id 입니다.");
        }

        List<ReComment> reCommentList = reCommentRepository.findAllByComment(comment);  // 여기에서

        List<ReCommentResponseDto> reCommentResponseDtoList = new ArrayList<>();

        for (ReComment reComment : reCommentList) {
            reCommentResponseDtoList.add(
                    ReCommentResponseDto.builder()
                            .id(reComment.getId())
                            .author(reComment.getMember().getNickname())
                            .reContent(reComment.getReContent())
                            .createdAt(reComment.getCreatedAt())
                            .modifiedAt(reComment.getModifiedAt())
                            .build()
            );
        }
        return ResponseDto.success(reCommentResponseDtoList);
    }

    @Transactional
    public ResponseDto<?> updateReComment(Long id, ReCommentRequestDto reCommentRequestDto, HttpServletRequest request) {
        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        Comment comment = commentService.isPresentComment(reCommentRequestDto.getCommentId());
        if (null == comment) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 id 입니다.");
        }

        ReComment reComment = isPresentReComment(id);
        if (null == reComment) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 대댓글 id 입니다.");
        }

        if (reComment.validateMember(member)) {
            return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
        }

        reComment.update(reCommentRequestDto);
        return ResponseDto.success(
                CommentResponseDto.builder()
                        .id(reComment.getId())
                        .author(reComment.getMember().getNickname())
                        .content(reComment.getReContent())
                        .createdAt(reComment.getCreatedAt())
                        .modifiedAt(reComment.getModifiedAt())
                        .build()
        );
    }

    @Transactional
    public ResponseDto<?> deleteReComment(Long id, HttpServletRequest request) {
        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        ReComment reComment = isPresentReComment(id);
        if (null == reComment) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 id 입니다.");
        }

        if (reComment.validateMember(member)) {
            return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
        }

        reCommentRepository.delete(reComment);
        return ResponseDto.success("success");
    }


    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }

    @Transactional(readOnly = true)
    public ReComment isPresentReComment(Long id) {
        Optional<ReComment> optionalReComment = reCommentRepository.findById(id);
        return optionalReComment.orElse(null);
    }

}

