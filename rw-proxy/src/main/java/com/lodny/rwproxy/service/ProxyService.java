package com.lodny.rwproxy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProxyService {

//    private final TagRepository tagRepository;
//
//    public int registerTags(final Long articleId, final Set<String> tags) {
//        int count = 1;
//        tagRepository.saveAll(tags.stream()
//                .map(tag -> new Tag(articleId, tag))
//                .toList());
//        log.info("registerTags() : count={}", count);
//
//        return count;
//    }
//
    public List<String> getTop10Tags() {
        return List.of("JAVA", "REACT");
//        return tagRepository.getTop10Tags().stream().map(tag -> tag[0]).toList();
    }
//
//    public Set<Tag> getTags(final Long articleId) {
//        return tagRepository.findAllByArticleId(articleId);
//    }
//
//    public List<Long> getArticleIdsByTag(final String tag) {
//        return tagRepository
//                .findAllByTag(tag).stream()
//                .map(Tag::getArticleId)
//                .toList();
//    }
//
//    @Transactional
//    public List<Tag> deleteTagsByArticleId(final Long articleId) {
//        List<Tag> tags = this.tagRepository.deleteAllByArticleId(articleId);
//        log.info("deleteTagsByArticleId() : tags={}", tags);
//
//        return tags;
//    }
}
