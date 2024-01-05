package demo.service;

import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.function.Supplier;

import demo.model.AnyEntity;

abstract sealed class CreateServiceTestFixture permits CreateServiceTest {

    protected final Supplier<CreateService.Request> REQUEST_SUCCESSFULLY = () -> new CreateService.Request("name 1");
    protected final Supplier<CreateService.Request> REQUEST_NULL = () -> null;
    protected final Supplier<CreateService.Request> REQUEST_WITH_ALL_ATTRS_NULL = () -> new CreateService.Request(null);
    protected final Supplier<CreateService.Request> REQUEST_WITH_EMPTY_NAME = () -> new CreateService.Request("                  ");
    protected final Supplier<CreateService.Request> REQUEST_WITH_NAME_MORE_THAN_50 = () -> new CreateService.Request("A".repeat(51));
    protected final Supplier<AnyEntity> ANY_ENTITY_WITH_NO_DATES = () -> {
        var anyEntity = new AnyEntity();
        anyEntity.setId(null);
        anyEntity.setCreated(null);
        anyEntity.setLastUpdated(null);
        anyEntity.setName("name 1");
        return anyEntity;
    };
    protected final Supplier<AnyEntity> ANY_ENTITY_SUCCESSFULLY = () -> {
        var anyEntity = new AnyEntity();
        anyEntity.setId(UUID.randomUUID().toString());
        anyEntity.setCreated(ZonedDateTime.now());
        anyEntity.setLastUpdated(ZonedDateTime.now().plusDays(3));
        anyEntity.setName("name 1");
        return anyEntity;
    };

}
