package com.example.login.reple;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RepleDto {

    private Long id;
    private Member member;
    private com.example.intermediate.domain.Post post;
    private String content;
}
