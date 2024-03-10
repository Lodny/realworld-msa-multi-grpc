package com.lodny.rwcommon.util.MapToDto;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
public class DtoFieldFactory {
    public static <T> DtoField createDtoField(Field field, Map<String, Object> map, T dto) {
        String fieldName = field.getName();

        boolean notFoundKey = !map.containsKey(fieldName);
        if (notFoundKey) return new DtoDtoField(field);

        Object value = map.get(fieldName);
        final Class<?> fieldType = field.getType();

        boolean isIntegerToLong = value.getClass() == Integer.class && fieldType == Long.class;
        if (isIntegerToLong) return new IntegerToLongDtoField(field);

        boolean isTimestampToLocalDateTime = value.getClass() == Timestamp.class && fieldType == LocalDateTime.class;
        if (isTimestampToLocalDateTime) return new TimestampToLocalDateTimeDtoField(field);

        boolean isSameType = fieldType == value.getClass();
        if (isSameType)
            return new DtoField(field);

        try {
            dto.getClass().getDeclaredMethod(fieldName + "ToDto", Object.class);
            return new MethodDtoField(field);
        } catch (NoSuchMethodException e) {
        }

        return new NullDtoField(field);
    }
}
