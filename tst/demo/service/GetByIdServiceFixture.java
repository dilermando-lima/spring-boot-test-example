package demo.service;

import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.function.Supplier;

import demo.model.AnyEntity;

abstract sealed class GetByIdServiceFixture permits GetByIdServiceTest {

    protected final Supplier<String> REQUEST_ID_NULL = () -> null;
    protected final Supplier<String> REQUEST_ID_BLANK = () -> "             ";
    protected final Supplier<String> REQUEST_ID_SUCCESSFULLY = UUID.randomUUID()::toString;
    protected final Supplier<AnyEntity> ANY_ENTITY_SUCCESSFULLY = () -> {
        var anyEntity = new AnyEntity();
        anyEntity.setId(UUID.randomUUID().toString());
        anyEntity.setCreated(ZonedDateTime.now());
        anyEntity.setLastUpdated(ZonedDateTime.now().plusDays(3));
        anyEntity.setName("name 1");
        return anyEntity;
    };
    
}
