package com.lodny.rwcommon.util.MapToDto;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Map;

@Slf4j
public class DtoDtoField extends DtoField {

    public DtoDtoField(final Field field) {
        super(field);
    }

    @Override
    public void set(Object dto, Map<String, Object> map) {
        try {
            final Class<?> fieldType = field.getType();
            if (fieldType.isPrimitive() || fieldType.equals(String.class))
                return;

            field.set(dto, MapToDto.convert(map, fieldType));
        } catch (IllegalAccessException e) {
            log.info("set() : IllegalAccessException: dto={}", dto);
        }
    }
}
