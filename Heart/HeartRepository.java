package com.example.intermediate.repository;
import com.example.intermediate.domain.*;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface HeartRepository extends JpaRepository<Heart, Long> {

    Optional<Heart> findByPostId(Long postId);
    Optional<Heart> findByCommentId(Long commentId);
    Optional<Heart> findByReCommentId(Long reCommentId);

    Heart findByMemberAndPost(Member member, Post post);
    Heart findByMemberAndComment(Member member, Comment comment);
    Heart findByMemberAndReComment(Member member, ReComment reComment);
}
