package demo.controller;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class FixtureWebMvcBase {

    private final Random random = new Random();
    private final ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);

    public final Supplier<String> RANDON_UUID_STRING = UUID.randomUUID()::toString;
    public final Supplier<String> RANDON_SHORT_STRING = () -> Long.toString(1000+ random.nextInt(10000),36);
    public final Supplier<Integer> RANDON_NUMBER_TO_1_TO_10 = () -> random.nextInt(10) + 1;

    public final Function<LocalDateTime, LocalDateTime> RANDON_DATE_AFTER = (localDate) -> {
        return localDate.plusMonths(new Random().nextInt(15)).plusDays(new Random().nextInt(360));
    };
    
    public Function<LocalDateTime, LocalDateTime> RANDON_DATE_BEFORE = (localDate) -> {
        return localDate.minusMonths(new Random().nextInt(15)).minusDays(new Random().nextInt(360));
    };

    protected String joinPathWithBAR(String... path){
        return Stream.of(path).collect(Collectors.joining("/"));
    }

    protected String toJson(Object object) throws JsonProcessingException{
            return mapper.writeValueAsString(object);
    }

    @FunctionalInterface
    public static interface BuildRequest {
        public ResultActions setMock(MockMvc mock) throws Exception;
    }

    @FunctionalInterface
    public static interface BuildRequestByID {
        public ResultActions setMock(MockMvc mock, String id) throws Exception;
    }

    @FunctionalInterface
    public static interface BuildRequestByIDAndBodyRequest<T> {
        public ResultActions setMock(MockMvc mock, String id, T request) throws Exception;
    }

    @FunctionalInterface
    public static interface BuildRequestByBodyRequest<T> {
        public ResultActions setMock(MockMvc mock, T request) throws Exception;
    }

    @FunctionalInterface
    public static interface BuildRequestWithPathAdding {
        public ResultActions setMock(MockMvc mock, String pathAdding) throws Exception;
    }

    
}
