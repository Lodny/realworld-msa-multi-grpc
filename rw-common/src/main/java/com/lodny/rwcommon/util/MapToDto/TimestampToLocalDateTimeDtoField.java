package com.lodny.rwcommon.util.MapToDto;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.Map;

@Slf4j
public class TimestampToLocalDateTimeDtoField extends DtoField{
    public TimestampToLocalDateTimeDtoField(final Field field) {
        super(field);
    }

    @Override
    public void set(Object dto, Map<String, Object> map) {
        try {
            Object value = map.get(field.getName());
            field.set(dto, ((Timestamp) value).toLocalDateTime());
        } catch (IllegalAccessException e) {
            log.info("set() : IllegalAccessException: dto={}", dto);
        }
    }
}
