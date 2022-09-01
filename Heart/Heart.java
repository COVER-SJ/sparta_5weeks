package com.example.intermediate.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Heart extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "member_id")
    @ManyToOne
    private Member member;

    @JoinColumn(name = "post_id")
    @ManyToOne
    private Post post;

    @JoinColumn(name = "comment_id")
    @ManyToOne
    private Comment comment;

    @JoinColumn(name = "reComment_id")
    @ManyToOne
    private ReComment reComment;

    public Heart(Member member, Post post, Comment comment, ReComment reComment) {
        this. member = member;
        this. post = post;
        this.comment = comment;
        this.reComment = reComment;
    }

    public boolean validateMember(Member member) {
        return !this.member.equals(member);
    }
}

