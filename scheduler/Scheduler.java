package com.example.login.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor // final 멤버 변수를 자동으로 생성합니다.
@Component // 스프링이 필요 시 자동으로 생성하는 클래스 목록에 추가합니다.
public class Scheduler {

    private final com.example.intermediate.repository.PostRepository postRepository;
    private final com.example.intermediate.service.PostService postService;

    // 초, 분, 시, 일, 월, 주 순서
    @Scheduled(cron = "0 0 1 * * *")
    public void autoDeleteZeroCommentPost() throws Exception {

        // 저장된 모든 글들을 조회합니다.
        List<com.example.intermediate.domain.Post> productList = postRepository.findAll();
        for (int i=0; i<productList.size(); i++) {
            // i 번째 글을 꺼냅니다.
            com.example.intermediate.domain.Post p = productList.get(i);
            if (p.getCommentCnt() == 0){
//                @todo request 값을 넣기
                postService.deletePost(i, request);
            }
        }
    }
}