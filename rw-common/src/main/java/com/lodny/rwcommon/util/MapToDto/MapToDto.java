package com.lodny.rwcommon.util.MapToDto;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
public class MapToDto {
    final static List<String> exceptFieldNames = List.of("log");

    public static <T> T convert(Map<String, Object> map, Class<T> clazz) {
        T dto = null;
        try {
            dto = clazz.getDeclaredConstructor().newInstance();

            final T finalDto = dto;
            Arrays.stream(clazz.getDeclaredFields())
                    .filter(field -> !exceptFieldNames.contains(field.getName()))
                    .map(field ->DtoFieldFactory.createDtoField(field, map, finalDto))
                    .forEach(dtoField -> dtoField.set(finalDto, map));
        } catch (Exception ignored) {
        }

//        for (Field field : clazz.getDeclaredFields()) {
//            field.setAccessible(true);
//
//            String fieldName = field.getName();
//            if (fieldName.equals("log")) continue;
//
//            try {
//                if (map.containsKey(fieldName)) {
//                    final Class<?> fieldType = field.getType();
//                    Object value = map.get(fieldName);
////                    if (field.getType().isAssignableFrom(value.getClass()))
//                    if (fieldType == Long.class && value.getClass() == Integer.class) {
//                        field.set(dto, ((Integer) value).longValue());
//                        continue;
//                    }
//
//                    if (fieldType == LocalDateTime.class && value.getClass() == Timestamp.class) {
//                        field.set(dto, ((Timestamp) value).toLocalDateTime());
//                        continue;
//                    }
//
//                    field.set(dto, value);
//                } else {
//                    final Class<?> fieldType = field.getType();
//                    if (fieldType.isPrimitive() || fieldType.equals(String.class))
//                        continue;
//
//                    field.set(dto, convert(map, fieldType));
//                }
//            } catch (IllegalAccessException e) {
//                log.info("convert() : IllegalAccessException");
//            }
//        }

        return dto;
    }
}
