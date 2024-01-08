package demo.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

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
import demo.service.UpdateByIdService.Request;

/**
 * <p>Tests of {@link UpdateByIdService}</p>
 * <pre>
 *{@link when_updateById} {
 *  {@link when_updateById#GIVEN_request_sucessfully_WHEN_updateById_SHOULD_run_all_steps_on_right_order()}
 *  {@link when_updateById#GIVEN_request_sucessfully_WHEN_updateById_SHOULD_run_sucessfully()}
 *}
 *{@link when_validateRequest} {
 *  {@link when_validateRequest#GIVEN_request_null_WHEN_validateRequest_SHOULD_throw_bad_request()}
 *  {@link when_validateRequest#GIVEN_request_sucessfully_WHEN_validateRequest_SHOULD_not_throw_any_exception()}
 *  {@link when_validateRequest#GIVEN_request_with_all_attrs_null_WHEN_validateRequest_SHOULD_throw_bad_request()}
 *  {@link when_validateRequest#GIVEN_request_with_name_empty_WHEN_validateRequest_SHOULD_throw_bad_request()}
 *  {@link when_validateRequest#GIVEN_request_with_name_more_than_50_WHEN_validateRequest_SHOULD_throw_bad_request()}
 *  {@link when_validateRequest#GIVEN_request_with_name_empty_WHEN_validateRequest_SHOULD_throw_bad_request()}
 *  {@link when_validateRequest#GIVEN_request_with_name_more_than_50_WHEN_validateRequest_SHOULD_throw_bad_request()}
 *}
 *{@link when_prepareEntityBeforeUpdating} {
 *  {@link when_prepareEntityBeforeUpdating#GIVEN_entity_sucessfully_WHEN_prepareEntityBeforeUpdating_SHOULD_return_entity_sucessfully()}
 *}
 *{@link when_getEntityById} {
 *  {@link when_getEntityById#GIVEN_id_request_not_found_WHEN_getEntityById_SHOULD_throw_not_found_request()}
 *}
 * </pre>
 */
@ExtendWith(MockitoExtension.class)
final class UpdateByIdServiceTest extends UpdateByIdServiceTestFixture{

    @InjectMocks 
    @Spy 
    UpdateByIdService updateByIdService;
    
    @Mock 
    AnyRepository anyRepositoryMock;

    /**
     *  testing all scenarios from {@link UpdateByIdService#updateById(String, demo.service.UpdateByIdService.Request)}
     */
    @Nested
    class when_updateById{

        @Test
        void GIVEN_request_sucessfully_WHEN_updateById_SHOULD_run_sucessfully(){

            final AnyEntity anyEntityGotById = ANY_ENTITY_SUCCESSFULLY.get();
            final String idToUpdate = anyEntityGotById.getId();
            final UpdateByIdService.Request requestInput = REQUEST_SUCCESSFULLY.get();

            when(anyRepositoryMock.findById(anyString())).thenReturn(Optional.of(anyEntityGotById));
            
            updateByIdService.updateById(idToUpdate, requestInput);

            ArgumentCaptor<AnyEntity> anyEntityCaptorBeforeBeUpdated = ArgumentCaptor.forClass(AnyEntity.class);
            verify(updateByIdService).updateEntity(anyEntityCaptorBeforeBeUpdated.capture());
    
            assertNotNull(anyEntityCaptorBeforeBeUpdated);
            assertEquals(requestInput.name(), anyEntityCaptorBeforeBeUpdated.getValue().getName());

        }

        @Test
        void GIVEN_request_sucessfully_WHEN_updateById_SHOULD_run_all_steps_on_right_order(){

            final AnyEntity anyEntityGotById = ANY_ENTITY_SUCCESSFULLY.get();
            final String idToUpdate = anyEntityGotById.getId();
            final UpdateByIdService.Request requestInput = REQUEST_SUCCESSFULLY.get();

            when(anyRepositoryMock.findById(anyString())).thenReturn(Optional.of(anyEntityGotById));

            InOrder inOrder = inOrder(updateByIdService);

            updateByIdService.updateById(idToUpdate, requestInput);

            inOrder.verify(updateByIdService).validateRequest(anyString(), any(UpdateByIdService.Request.class));
            inOrder.verify(updateByIdService).getEntityById(anyString());
            inOrder.verify(updateByIdService).prepareEntityBeforeUpdating(any(AnyEntity.class), any(UpdateByIdService.Request.class));
            inOrder.verify(updateByIdService).updateEntity(any(AnyEntity.class));
        
        }
    }

    /**
     *  testing all scenarios from {@link UpdateByIdService#validateRequest(String, Request)}
     */
    @Nested
    class when_validateRequest {

        @Test
        void GIVEN_request_sucessfully_WHEN_validateRequest_SHOULD_not_throw_any_exception(){
            final UpdateByIdService.Request requestInput = REQUEST_SUCCESSFULLY.get();
            final String idToUpdate = ID_REQUEST_SUCCESSFULLY.get();
            assertDoesNotThrow(() -> updateByIdService.validateRequest(idToUpdate, requestInput));
        }

        @Test
        void GIVEN_request_null_WHEN_validateRequest_SHOULD_throw_bad_request(){
            final UpdateByIdService.Request requestNullInput = REQUEST_NULL.get();
            final String idToUpdate = ID_REQUEST_SUCCESSFULLY.get();

            final var expectedException = assertThrows(ResponseStatusException.class, () -> updateByIdService.validateRequest(idToUpdate, requestNullInput));

            final int expectedHttpStatus = 400;
            assertEquals(expectedHttpStatus , expectedException.getStatusCode().value());
            assertEquals(ErrMessage.BODY_NOT_FOUND.get(), expectedException.getBody().getDetail());
        }

        @Test
        void GIVEN_request_with_all_attrs_null_WHEN_validateRequest_SHOULD_throw_bad_request(){
            final UpdateByIdService.Request requestAllAttrNull = REQUEST_WITH_ALL_ATTRS_NULL.get();
            final String idToUpdate = ID_REQUEST_SUCCESSFULLY.get();

            final var expectedException = assertThrows(ResponseStatusException.class, () -> updateByIdService.validateRequest(idToUpdate, requestAllAttrNull));

            final int expectedHttpStatus = 400;
            assertEquals(expectedHttpStatus , expectedException.getStatusCode().value());
            assertEquals(ErrMessage.NAME_IS_REQUIRED.get(), expectedException.getBody().getDetail());
        }

        @Test
        void GIVEN_request_with_name_empty_WHEN_validateRequest_SHOULD_throw_bad_request(){
            final UpdateByIdService.Request requestWithEmptyName = REQUEST_WITH_EMPTY_NAME.get();
            final String idToUpdate = ID_REQUEST_SUCCESSFULLY.get();

            final var expectedException = assertThrows(ResponseStatusException.class, () -> updateByIdService.validateRequest(idToUpdate, requestWithEmptyName));

            final int expectedHttpStatus = 400;
            assertEquals(expectedHttpStatus , expectedException.getStatusCode().value());
            assertEquals(ErrMessage.NAME_IS_REQUIRED.get(), expectedException.getBody().getDetail());
        }

        @Test
        void GIVEN_request_with_name_more_than_50_WHEN_validateRequest_SHOULD_throw_bad_request(){
            
            final UpdateByIdService.Request requestWithNameMoreThan50 = REQUEST_WITH_NAME_MORE_THAN_50.get();
             final String idToUpdate = ID_REQUEST_SUCCESSFULLY.get();

            final var expectedException = assertThrows(ResponseStatusException.class, () -> updateByIdService.validateRequest(idToUpdate, requestWithNameMoreThan50));

            final int expectedHttpStatus = 400;
            assertEquals(expectedHttpStatus , expectedException.getStatusCode().value());
            assertEquals(ErrMessage.NAME_MUST_BE_LESS_THAN_50_CARACT.get(), expectedException.getBody().getDetail());
        }

        @Test
        void GIVEN_id_request_null_WHEN_validateRequest_SHOULD_throw_bad_request(){
            
            final UpdateByIdService.Request requestInput = REQUEST_SUCCESSFULLY.get();
            final String idNullToUpdate = ID_REQUEST_NULL.get();

            final var expectedException = assertThrows(ResponseStatusException.class, () -> updateByIdService.validateRequest(idNullToUpdate, requestInput));

            final int expectedHttpStatus = 400;
            assertEquals(expectedHttpStatus , expectedException.getStatusCode().value());
            assertEquals(ErrMessage.ID_IS_REQUIRED.get(), expectedException.getBody().getDetail());
        }

        @Test
        void GIVEN_id_request_empty_WHEN_validateRequest_SHOULD_throw_bad_request(){
            
            final UpdateByIdService.Request requestInput = REQUEST_SUCCESSFULLY.get();
            final String idEmptyToUpdate = ID_REQUEST_EMPTY.get();

            final var expectedException = assertThrows(ResponseStatusException.class, () -> updateByIdService.validateRequest(idEmptyToUpdate, requestInput));

            final int expectedHttpStatus = 400;
            assertEquals(expectedHttpStatus , expectedException.getStatusCode().value());
            assertEquals(ErrMessage.ID_IS_REQUIRED.get(), expectedException.getBody().getDetail());
        }
    }

    /**
     *  testing all scenarios from {@link UpdateByIdService#prepareEntityBeforeUpdating(AnyEntity, Request)}
     */
    @Nested
    class when_prepareEntityBeforeUpdating {

        @Test
        void GIVEN_entity_sucessfully_WHEN_prepareEntityBeforeUpdating_SHOULD_return_entity_sucessfully(){
            
            final AnyEntity anyEntityToUpdate = ANY_ENTITY_SUCCESSFULLY.get();
            final UpdateByIdService.Request requestInput = REQUEST_SUCCESSFULLY.get();
            final ZonedDateTime expectedDateLastUpdated = ZonedDateTime.now().plusDays(15);

            try(MockedStatic<ZonedDateTime> mockedStatic = Mockito.mockStatic(ZonedDateTime.class)){
                when(ZonedDateTime.now()).thenReturn(expectedDateLastUpdated);
     
                final var expectedAnyEntity = updateByIdService.prepareEntityBeforeUpdating(anyEntityToUpdate, requestInput);

                assertEquals(anyEntityToUpdate.getCreated() , expectedAnyEntity.getCreated());
                assertEquals(expectedDateLastUpdated, expectedAnyEntity.getLastUpdated());
                assertEquals(anyEntityToUpdate.getName(), expectedAnyEntity.getName());

            }
        }
    }
    
    /**
     *  testing all scenarios from {@link UpdateByIdService#getEntityById(String)}
     */
    @Nested
    class when_getEntityById {

        @Test
        void GIVEN_id_request_not_found_WHEN_getEntityById_SHOULD_throw_not_found_request(){
 
            final String anyInvalidId = UUID.randomUUID().toString();
 
            when(anyRepositoryMock.findById(anyString())).thenReturn(Optional.empty());

            final var expectedException = assertThrows(ResponseStatusException.class, () -> updateByIdService.getEntityById(anyInvalidId));

            final int expectedHttpStatus = 404;
            assertEquals(expectedHttpStatus , expectedException.getStatusCode().value());
            assertEquals(ErrMessage.ID_NOT_FOUND.get(anyInvalidId) , expectedException.getBody().getDetail());
        }
        
    }

}
