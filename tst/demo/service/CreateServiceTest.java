package demo.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import demo.constant.ErrMessage;
import demo.model.AnyEntity;
import demo.repository.AnyRepository;

/**
 * <p>Tests of {@link CreateService}</p>
 * <pre>
 *{@link when_create} {
 *  {@link when_create#GIVEN_request_sucessfully_WHEN_create_SHOULD_run_sucessfully_and_return_new_id()}
 *  {@link when_create#GIVEN_request_sucessfully_WHEN_create_SHOULD_run_all_steps_on_right_order()}
 *}
 *{@link when_validateRequest} {
 *  {@link when_validateRequest#GIVEN_request_null_WHEN_validateRequest_SHOULD_throw_bad_request()}
 *  {@link when_validateRequest#GIVEN_request_sucessfully_WHEN_validateRequest_SHOULD_not_throw_any_exception()}
 *  {@link when_validateRequest#GIVEN_request_with_all_attrs_null_WHEN_validateRequest_SHOULD_throw_bad_request()}
 *  {@link when_validateRequest#GIVEN_request_with_name_empty_WHEN_validateRequest_SHOULD_throw_bad_request()}
 *  {@link when_validateRequest#GIVEN_request_with_name_more_than_50_WHEN_validateRequest_SHOULD_throw_bad_request()}
 *}
 *{@link when_prepareEntityBeforeCreating} {
 *  {@link when_prepareEntityBeforeCreating#GIVEN_entity_without_dates_WHEN_prepareEntityBeforeCreating_SHOULD_return_entity_with_dates()}
 *}
 *{@link when_convertRequestToEntity} {
 *  {@link when_convertRequestToEntity#GIVEN_request_with_name_WHEN_convertRequestToEntity_SHOULD_return_entity_with_only_name()}
 *}
 * </pre>
 */
@ExtendWith(MockitoExtension.class)
final class CreateServiceTest extends CreateServiceTestFixture{

    @InjectMocks 
    @Spy 
    CreateService createService;
    
    @Mock 
    AnyRepository anyRepositoryMock;

    /**
     *  testing all scenarios from {@link CreateService#create(demo.service.CreateService.Request)}
     */
    @Nested
    class when_create{

        @Test
        void GIVEN_request_sucessfully_WHEN_create_SHOULD_run_sucessfully_and_return_new_id(){

            final AnyEntity anyEntityCreatedInDatabase = ANY_ENTITY_SUCCESSFULLY.get();
            final CreateService.Request requestInput = REQUEST_SUCCESSFULLY.get();

            when(anyRepositoryMock.save(any(AnyEntity.class))).thenReturn(anyEntityCreatedInDatabase);

            final CreateService.Response expectedResponse = createService.create(requestInput);

            ArgumentCaptor<AnyEntity> anyEntityCaptorBeforeBeSaved = ArgumentCaptor.forClass(AnyEntity.class);
            verify(createService).createEntity(anyEntityCaptorBeforeBeSaved.capture());
            final String idAnyEntityBeforeBeSaved = anyEntityCaptorBeforeBeSaved.getValue().getId();
            final String nameAnyEntityBeforeBeSaved = anyEntityCaptorBeforeBeSaved.getValue().getName();
            assertNull(idAnyEntityBeforeBeSaved);
            assertEquals(requestInput.name(), nameAnyEntityBeforeBeSaved);

            assertNotNull(expectedResponse);
            assertEquals(expectedResponse.id(), anyEntityCreatedInDatabase.getId());
        }

        @Test
        void GIVEN_request_sucessfully_WHEN_create_SHOULD_run_all_steps_on_right_order(){

            final AnyEntity anyEntityCreatedInDatabase = ANY_ENTITY_SUCCESSFULLY.get();
            final CreateService.Request requestInput = REQUEST_SUCCESSFULLY.get();

            when(anyRepositoryMock.save(any(AnyEntity.class))).thenReturn(anyEntityCreatedInDatabase);

            InOrder inOrder = inOrder(createService);

            createService.create(requestInput);

            inOrder.verify(createService).validateRequest(any(CreateService.Request.class));
            inOrder.verify(createService).convertRequestToEntity(any(CreateService.Request.class));
            inOrder.verify(createService).prepareEntityBeforeCreating(any(AnyEntity.class));
            inOrder.verify(createService).createEntity(any(AnyEntity.class));
            inOrder.verify(createService).convertEntityToResponse(any(AnyEntity.class));
        
        }
    }

    /**
     *  testing all scenarios from {@link CreateService#validateRequest(demo.service.CreateService.Request)}
     */
    @Nested
    class when_validateRequest {

        @Test
        void GIVEN_request_sucessfully_WHEN_validateRequest_SHOULD_not_throw_any_exception(){
            final CreateService.Request requestInput = REQUEST_SUCCESSFULLY.get();
            assertDoesNotThrow(() -> createService.validateRequest(requestInput));
        }

        @Test
        void GIVEN_request_null_WHEN_validateRequest_SHOULD_throw_bad_request(){
            final CreateService.Request requestNullInput = REQUEST_NULL.get();

            final var expectedException = assertThrows(ResponseStatusException.class, () -> createService.validateRequest(requestNullInput));

            final int expectedHttpStatus = 400;
            assertEquals(expectedHttpStatus , expectedException.getStatusCode().value());
            assertEquals(ErrMessage.BODY_NOT_FOUND.get(), expectedException.getBody().getDetail());
        }

        @Test
        void GIVEN_request_with_all_attrs_null_WHEN_validateRequest_SHOULD_throw_bad_request(){
            final CreateService.Request requestNullInput = REQUEST_WITH_ALL_ATTRS_NULL.get();

            final var expectedException = assertThrows(ResponseStatusException.class, () -> createService.validateRequest(requestNullInput));

            final int expectedHttpStatus = 400;
            assertEquals(expectedHttpStatus , expectedException.getStatusCode().value());
            assertEquals(ErrMessage.NAME_IS_REQUIRED.get(), expectedException.getBody().getDetail());
        }

        @Test
        void GIVEN_request_with_name_empty_WHEN_validateRequest_SHOULD_throw_bad_request(){
            final CreateService.Request requestWithEmptyName = REQUEST_WITH_EMPTY_NAME.get();

            final var expectedException = assertThrows(ResponseStatusException.class, () -> createService.validateRequest(requestWithEmptyName));

            final int expectedHttpStatus = 400;
            assertEquals(expectedHttpStatus , expectedException.getStatusCode().value());
            assertEquals(ErrMessage.NAME_IS_REQUIRED.get(), expectedException.getBody().getDetail());
        }

        @Test
        void GIVEN_request_with_name_more_than_50_WHEN_validateRequest_SHOULD_throw_bad_request(){
            
            final CreateService.Request requestWithNameMoreThan50 = REQUEST_WITH_NAME_MORE_THAN_50.get();

            final var expectedException = assertThrows(ResponseStatusException.class, () -> createService.validateRequest(requestWithNameMoreThan50));

            final int expectedHttpStatus = 400;
            assertEquals(expectedHttpStatus , expectedException.getStatusCode().value());
            assertEquals(ErrMessage.NAME_MUST_BE_LESS_THAN_50_CARACT.get(), expectedException.getBody().getDetail());
        }
    }

    /**
     *  testing all scenarios from {@link CreateService#prepareEntityBeforeCreating(AnyEntity)}
     */
    @Nested
    class when_prepareEntityBeforeCreating {

        @Test
        void GIVEN_entity_without_dates_WHEN_prepareEntityBeforeCreating_SHOULD_return_entity_with_dates(){
            
            final AnyEntity anyEntityWithoutDates = ANY_ENTITY_WITH_NO_DATES.get();
            final ZonedDateTime expectedDateCreatedAndLastUpdated = ZonedDateTime.now().plusDays(15);

            try(MockedStatic<ZonedDateTime> mockedStatic = Mockito.mockStatic(ZonedDateTime.class)){
                when(ZonedDateTime.now()).thenReturn(expectedDateCreatedAndLastUpdated);
     
                final var expectedAnyEntity = createService.prepareEntityBeforeCreating(anyEntityWithoutDates);

                assertEquals(expectedDateCreatedAndLastUpdated , expectedAnyEntity.getCreated());
                assertEquals(expectedDateCreatedAndLastUpdated , expectedAnyEntity.getLastUpdated());

            }
        }
    }
    
    /**
     *  testing all scenarios from {@link CreateService#convertRequestToEntity(demo.service.CreateService.Request)}
     */
    @Nested
    class when_convertRequestToEntity {
        @Test
        void GIVEN_request_with_name_WHEN_convertRequestToEntity_SHOULD_return_entity_with_only_name(){
            final CreateService.Request requestInput = REQUEST_SUCCESSFULLY.get();

            final var responseAnyEntity =  createService.convertRequestToEntity(requestInput);
            assertEquals(requestInput.name(), responseAnyEntity.getName());
            assertNull(responseAnyEntity.getId());
            assertNull(responseAnyEntity.getCreated());
            assertNull(responseAnyEntity.getLastUpdated());
        }
             
    }

}
