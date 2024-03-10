package com.lodny.rwcommon.util.MapToDto;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Map;

@Slf4j
public class IntegerToLongDtoField extends DtoField {
    public IntegerToLongDtoField(final Field field) {
        super(field);
    }

    @Override
    public void set(Object dto, Map<String, Object> map) {
        try {
            Object value = map.get(field.getName());
            field.set(dto, ((Integer) value).longValue());
        } catch (IllegalAccessException e) {
            log.info("set() : IllegalAccessException: dto={}", dto);
        }
    }
}
