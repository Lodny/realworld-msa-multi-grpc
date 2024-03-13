package com.lodny.rwcommon.util;

import com.google.protobuf.Timestamp;
import com.lodny.rwcommon.grpc.common.Common;
import io.grpc.stub.StreamObserver;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class CommonGrpcUtil {
    public static LocalDateTime toLocalDateTime(Timestamp timestamp) {
        Instant instant = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public static Timestamp toGrpcTimestamp(LocalDateTime localDateTime) {
        long seconds = localDateTime.toEpochSecond(ZoneOffset.UTC);
        int nanos = localDateTime.getNano();
        return Timestamp.newBuilder().setSeconds(seconds).setNanos(nanos).build();
    }

    public static <T> void completeResponseObserver(final StreamObserver<T> responseObserver, T response) {
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    public static <T> void completeResponseObserver(final StreamObserver<T> responseObserver) {
        CommonGrpcUtil.completeResponseObserver(responseObserver, (T) Common.Empty.getDefaultInstance());
    }
}
