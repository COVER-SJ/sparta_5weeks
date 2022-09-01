package com.example.login.like;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private boolean like;

}



//
//    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Comment> comments;
//
//    @JoinColumn(name = "member_id", nullable = false)
//    @ManyToOne(fetch = FetchType.LAZY)
//    private Member member;
//
//    public void update(com.example.intermediate.controller.request.PostRequestDto postRequestDto) {
//        this.title = postRequestDto.getTitle();
//        this.content = postRequestDto.getContent();
//    }
//
//    public boolean validateMember(Member member) {
//        return !this.member.equals(member);
//    }
//
//}
