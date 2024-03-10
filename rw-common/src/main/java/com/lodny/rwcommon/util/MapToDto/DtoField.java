package com.lodny.rwcommon.util.MapToDto;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Map;

@Slf4j
@ToString
public class DtoField {
    protected final Field field;
    public DtoField(final Field field) {
        this.field = field;
        this.field.setAccessible(true);
    }

    public void set(Object dto, Map<String, Object> map) {
        try {
            Object value = map.get(field.getName());
            this.field.set(dto, value);
        } catch (IllegalAccessException e) {
            log.info("set() : IllegalAccessException: dto={}", dto);
        }
    }
}
