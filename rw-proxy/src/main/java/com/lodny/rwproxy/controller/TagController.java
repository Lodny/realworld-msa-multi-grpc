package com.lodny.rwproxy.controller;

import com.lodny.rwproxy.entity.wrapper.WrapTag10Response;
import com.lodny.rwproxy.service.TagGrpcClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TagController {

    private final TagGrpcClient tagGrpcClient;

    @GetMapping("/tags")
    public ResponseEntity<?> getTopTagStrings() {
        log.info("getTopTagStrings() : 1={}", 1);

        List<String> top10TagStrings = tagGrpcClient.getTopTagStrings(10);
        log.info("getTopTagStrings() : top10TagStrings={}", top10TagStrings);

        return ResponseEntity.ok(new WrapTag10Response(top10TagStrings));
    }
}
