package com.lodny.rwproxy.service;

import com.lodny.rwcommon.grpc.tag.TagGrpc;
import com.lodny.rwcommon.grpc.tag.GrpcTopTagStringsRequest;
import com.lodny.rwcommon.grpc.tag.GrpcTopTagStringsResponse;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FollowGrpcClient {

    @GrpcClient("tag-grpc")
    private TagGrpc.TagBlockingStub tagBlockingStub;

    public List<String> getTopTagStrings(int count) {
        GrpcTopTagStringsResponse topTagStrings = tagBlockingStub.getTopTagStrings(GrpcTopTagStringsRequest.newBuilder().setCount(count).build());
        log.info("getTopTagStrings() : topTagStrings={}", topTagStrings);

        return topTagStrings.getTagsList();
    }
}
