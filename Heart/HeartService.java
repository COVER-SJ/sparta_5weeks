package com.example.intermediate.service;

import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.domain.*;
import com.example.intermediate.jwt.TokenProvider;
import com.example.intermediate.repository.HeartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HeartService {

    private final HeartRepository heartRepository;
    private final PostService postService;
    private final CommentService commentService;
    private final ReCommentService reCommentService;
    private final TokenProvider tokenProvider;

    @Transactional
    public ResponseDto<?> postHeart(Long postId, HttpServletRequest request) {
        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        Member member = validateMember(request);
        //validateMember가 null이라서 member에 담긴 값이 null이면 토큰이 유효하지 않을음 보여준다.
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        Post post = postService.isPresentPost(postId);
        if (null == post) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        }

        if (heartRepository.findByMemberAndPost(member, post) == null) {
            Heart heart = Heart.builder()
                    .post(post)
                    .member(member)
                    .build();
            heartRepository.save(heart);
            return ResponseDto.success("게시글 좋아요 완료");
        } else {
            return ResponseDto.fail("BAD_REQUEST", "좋아요가 이미 존재합니다.");
        }
    }

    @Transactional
    public ResponseDto<?> deletePostHeart (Long postId, HttpServletRequest request) {

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

        Post post = postService.isPresentPost(postId);
        if (null == post) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        }

        Heart heart = isPresentHeartByPostId(postId);
        if (null == heart) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 좋아요 id 입니다.");
        }

        if (heart.validateMember(member)) {
            return ResponseDto.fail("BAD_REQUEST", "작성자만 취소할 수 있습니다.");
        }

        Heart deleteHeart = heartRepository.findByMemberAndPost(member, post);
        heartRepository.delete(deleteHeart);

        return ResponseDto.success("게시글 좋아요 취소 완료");
    }
    //댓글 좋아요
    @Transactional
    public ResponseDto<?> commentHeart(Long commentId, HttpServletRequest request) {
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

        Comment comment = commentService.isPresentComment(commentId);
        if (null == comment) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        }

        if (heartRepository.findByMemberAndComment(member, comment) == null) {
            Heart heart = Heart.builder()
                    .comment(comment)
                    .member(member)
                    .build();
            heartRepository.save(heart);
            return ResponseDto.success("댓글 좋아요 성공");
        } else {
            return ResponseDto.fail("BAD_REQUEST", "좋아요가 이미 존재합니다.");
        }
    }

    @Transactional
    public ResponseDto<?> deleteCommentHeart(Long commentId, HttpServletRequest request) {

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

        Comment comment = commentService.isPresentComment(commentId);
        if (null == comment) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        }

        Heart heart = isPresentHeartByCommentId(commentId);
        if (null == heart) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 좋아요 id 입니다.");
        }

        if (heart.validateMember(member)) {
            return ResponseDto.fail("BAD_REQUEST", "작성자만 취소할 수 있습니다.");
        }

        Heart deleteHeart = heartRepository.findByMemberAndComment(member, comment);
        heartRepository.delete(deleteHeart);

        return ResponseDto.success("댓글 좋아요 취소 완료");
    }

    @Transactional
    public ResponseDto<?> reCommentHeart(Long reCommentId, HttpServletRequest request) {
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

        ReComment reComment = reCommentService.isPresentReComment(reCommentId);
        if (null == reComment) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        }

        if (heartRepository.findByMemberAndReComment(member, reComment) == null) {
            Heart heart = Heart.builder()
                    .reComment(reComment)
                    .member(member)
                    .build();
            heartRepository.save(heart);
            return ResponseDto.success("좋아요 완료");
        } else {
            return ResponseDto.fail("BAD_REQUEST", "좋아요가 이미 존재합니다.");
        }
    }

    @Transactional
    public ResponseDto<?> deleteReCommentHeart(Long reCommentId, HttpServletRequest request) {

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

        ReComment reComment = reCommentService.isPresentReComment(reCommentId);
        if (null == reComment) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        }

        Heart heart = isPresentHeartByReCommentId(reCommentId);
        if (null == heart) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 좋아요 id 입니다.");
        }

        if (heart.validateMember(member)) {
            return ResponseDto.fail("BAD_REQUEST", "작성자만 취소할 수 있습니다.");
        }

        Heart deleteHeart = heartRepository.findByMemberAndReComment(member, reComment);
        heartRepository.delete(deleteHeart);
        return ResponseDto.success("댓글 좋아요 취소 완료");
    }

    @Transactional(readOnly = true)
    public Heart isPresentHeartByPostId(Long postId) {
        Optional<Heart> optionalHeart = heartRepository.findByPostId(postId);
        return optionalHeart.orElse(null);
    }


    @Transactional(readOnly = true)
    public Heart isPresentHeartByCommentId(Long commentId) {
        Optional<Heart> optionalHeart = heartRepository.findByCommentId(commentId);
        return optionalHeart.orElse(null);
    }

    @Transactional(readOnly = true)
    public Heart isPresentHeartByReCommentId(Long reCommentId) {
        Optional<Heart> optionalHeart = heartRepository.findByReCommentId(reCommentId);
        return optionalHeart.orElse(null);
    }

    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }
}