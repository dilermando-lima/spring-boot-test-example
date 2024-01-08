package demo.service;

import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.function.Supplier;

import demo.model.AnyEntity;

abstract sealed class UpdateByIdServiceTestFixture permits UpdateByIdServiceTest {

    protected final Supplier<UpdateByIdService.Request> REQUEST_SUCCESSFULLY = () -> new UpdateByIdService.Request("name 1 updated");
    protected final Supplier<UpdateByIdService.Request> REQUEST_NULL = () -> null;
    protected final Supplier<UpdateByIdService.Request> REQUEST_WITH_ALL_ATTRS_NULL = () -> new UpdateByIdService.Request(null);
    protected final Supplier<UpdateByIdService.Request> REQUEST_WITH_EMPTY_NAME = () -> new UpdateByIdService.Request("                  ");
    protected final Supplier<UpdateByIdService.Request> REQUEST_WITH_NAME_MORE_THAN_50 = () -> new UpdateByIdService.Request("A".repeat(51));

    protected final Supplier<String> ID_REQUEST_NULL = () -> null;
    protected final Supplier<String> ID_REQUEST_SUCCESSFULLY  = UUID.randomUUID()::toString;
    protected final Supplier<String> ID_REQUEST_EMPTY = () -> "             ";

    protected final Supplier<AnyEntity> ANY_ENTITY_SUCCESSFULLY = () -> {
        var anyEntity = new AnyEntity();
        anyEntity.setId(UUID.randomUUID().toString());
        anyEntity.setCreated(ZonedDateTime.now());
        anyEntity.setLastUpdated(ZonedDateTime.now().plusDays(3));
        anyEntity.setName("name 1");
        return anyEntity;
    };

}
