package com.lodny.rwproxy.service;

import com.lodny.rwcommon.grpc.tag.TagGrpc;
import com.lodny.rwcommon.grpc.tag.TopTagStringsRequest;
import com.lodny.rwcommon.grpc.tag.TopTagStringsResponse;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TagGrpcClient {

    @GrpcClient("tag-grpc")
    private TagGrpc.TagBlockingStub tagBlockingStub;

    public List<String> getTopTagStrings(int count) {
        TopTagStringsResponse topTagStrings = tagBlockingStub.getTopTagStrings(TopTagStringsRequest.newBuilder().setCount(count).build());
        log.info("getTopTagStrings() : topTagStrings={}", topTagStrings);

        return topTagStrings.getTagsList();
    }
}
