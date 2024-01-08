package demo.service;

import java.util.UUID;
import java.util.function.Supplier;

abstract sealed class RemoveByIdServiceFixture permits RemoveByIdServiceTest {

    protected final Supplier<String> REQUEST_ID_NULL = () -> null;
    protected final Supplier<String> REQUEST_ID_BLANK = () -> "             ";
    protected final Supplier<String> REQUEST_ID_SUCCESSFULLY = UUID.randomUUID()::toString;
}
