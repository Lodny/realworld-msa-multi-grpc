package com.lodny.rwcommon.util.MapToDto;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Map;

@Slf4j
public class NullDtoField extends DtoField {

    public NullDtoField(final Field field) {
        super(field);
    }

    @Override
    public void set(Object dto, Map<String, Object> map) {
    }
}
