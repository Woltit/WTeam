package com.wteam.backend;

import com.wteam.backend.booking.dto.BookingResponse;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class RedisTest {
    @Test
    public void test() {
        try {
            PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                    .allowIfSubType(Object.class)
                    .build();

            tools.jackson.databind.ObjectMapper mapper = tools.jackson.databind.json.JsonMapper.builder()
                    .findAndAddModules()
                    .activateDefaultTypingAsProperty(ptv, tools.jackson.databind.DefaultTyping.NON_FINAL_AND_RECORDS, "@class")
                    .build();

            GenericJacksonJsonRedisSerializer serializer = new GenericJacksonJsonRedisSerializer(mapper);

            Object emptyList = new java.util.ArrayList<>();
            System.out.println("Serializing EmptyList...");
            byte[] bytes = serializer.serialize(emptyList);
            System.out.println("Serialized: " + new String(bytes));

            System.out.println("Deserializing EmptyList...");
            Object obj = serializer.deserialize(bytes);
            System.out.println("Deserialized EmptyList: " + obj.getClass());
            
            Object listOfRecords = new java.util.ArrayList<>(java.util.List.of(new BookingResponse(
                    1L, 1L, 1L, LocalDate.now(), LocalDate.now().plusDays(2),
                    java.math.BigDecimal.TEN, java.math.BigDecimal.ONE,
                    java.math.BigDecimal.ZERO,
                    com.wteam.backend.common.enums.BookingStatus.PENDING,
                    "No reason"
            )));
            System.out.println("Serializing List of Records...");
            byte[] bytes2 = serializer.serialize(listOfRecords);
            System.out.println("Serialized: " + new String(bytes2));

            System.out.println("Deserializing List of Records...");
            Object obj2 = serializer.deserialize(bytes2);
            System.out.println("Deserialized List of Records: " + obj2.getClass());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
