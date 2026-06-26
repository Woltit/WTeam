import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

public class JacksonTest {
    public static void main(String[] args) throws Exception {
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType(Object.class)
                .build();

        tools.jackson.databind.ObjectMapper mapper = tools.jackson.databind.json.JsonMapper.builder()
                .findAndAddModules()
                .activateDefaultTypingAsProperty(ptv, tools.jackson.databind.DefaultTyping.NON_FINAL_AND_RECORDS, "@class")
                .build();

        GenericJacksonJsonRedisSerializer serializer = new GenericJacksonJsonRedisSerializer(mapper);

        try {
            System.out.println("--- Testing EmptyList ---");
            Object emptyList = java.util.Collections.emptyList();
            byte[] bytes = serializer.serialize(emptyList);
            System.out.println("Serialized: " + new String(bytes));
            Object obj = serializer.deserialize(bytes);
            System.out.println("Deserialized: " + obj.getClass());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            System.out.println("--- Testing SingletonList of Record ---");
            record TestRecord(String name) {}
            Object singletonList = java.util.Collections.singletonList(new TestRecord("test"));
            byte[] bytes2 = serializer.serialize(singletonList);
            System.out.println("Serialized: " + new String(bytes2));
            Object obj2 = serializer.deserialize(bytes2);
            System.out.println("Deserialized: " + obj2.getClass());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
