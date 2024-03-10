package com.lodny.rwcommon.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RealException extends RuntimeException {
    public RealException(String message) {
        super(message);
    }
}
