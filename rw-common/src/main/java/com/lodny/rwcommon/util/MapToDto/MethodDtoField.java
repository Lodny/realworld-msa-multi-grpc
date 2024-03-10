package com.lodny.rwcommon.util.MapToDto;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Map;

@Slf4j
public class MethodDtoField extends DtoField {

    public MethodDtoField(final Field field) {
        super(field);
    }

    @Override
    public void set(Object dto, Map<String, Object> map) {
        try {
            dto.getClass()
                    .getDeclaredMethod(field.getName() + "ToDto", Object.class)
                    .invoke(dto, map.get(field.getName()));
        } catch (Exception ignored) {
        }
    }
}
