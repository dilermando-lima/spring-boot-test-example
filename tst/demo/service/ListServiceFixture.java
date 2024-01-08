package demo.service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

import demo.model.AnyEntity;

abstract sealed class ListServiceFixture permits ListServiceTest {

    protected final Supplier<ListService.Request> REQUEST_SUCCESSFULLY = () -> new ListService.Request(0, 10, "filter-1");
    protected final Supplier<ListService.Request> REQUEST_WITH_NUM_PAGE_AND_SIZE_PAGE_MORE_THAN_ZERO = () -> new ListService.Request(2, 10, null);
    protected final Supplier<ListService.Request> REQUEST_NULL = () -> null;
    protected final Supplier<ListService.Request> REQUEST_WITH_ALL_ATTRS_NULL = () -> new ListService.Request(null,null,null);
    protected final Supplier<ListService.Request> REQUEST_WITH_NUM_PAGE_LESS_THAN_ZERO = () -> new ListService.Request(-1, 10, "filter-1");
    protected final Supplier<ListService.Request> REQUEST_WITH_SIZE_PAGE_LESS_THAN_ZERO = () -> new ListService.Request(0, -1, "filter-1");
    protected final Function<Integer, ListService.Request> REQUEST_WITH_SIZE_PAGE_MORE_THAN_X = (sizePage) -> new ListService.Request(0, sizePage + 1, "filter-1");

    protected final Supplier<List<AnyEntity>> LIST_ANY_ENTITY_SUCCESSFULLY = () -> {
        var anyEntity1 = new AnyEntity();
        anyEntity1.setId(UUID.randomUUID().toString());
        anyEntity1.setCreated(ZonedDateTime.now().plusDays(1));
        anyEntity1.setLastUpdated(ZonedDateTime.now().plusDays(3));
        anyEntity1.setName("name 1");

        var anyEntity2 = new AnyEntity();
        anyEntity2.setId(UUID.randomUUID().toString());
        anyEntity2.setCreated(ZonedDateTime.now().plusDays(2));
        anyEntity2.setLastUpdated(ZonedDateTime.now().plusDays(5));
        anyEntity2.setName("name 2");
        
        return List.of(anyEntity1, anyEntity2);
    };

    protected final Supplier<List<AnyEntity>> LIST_ANY_ENTITY_NULL = () -> null;
    
    
}
