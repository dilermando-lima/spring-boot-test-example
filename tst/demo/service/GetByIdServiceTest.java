package demo.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import demo.constant.ErrMessage;
import demo.model.AnyEntity;
import demo.repository.AnyRepository;

/**
 * <h3>Tests of {@link CreateService} </h3>
 * <pre>
 *{@link when_getById} {
 *  {@link when_getById#GIVEN_request_sucessfully_WHEN_getById_SHOULD_run_sucessfully()}
 *  {@link when_getById#GIVEN_request_sucessfully_WHEN_getById_SHOULD_run_all_steps_on_right_order()}
 *}
 *{@link when_validateIdRequest} {
 *  {@link when_validateIdRequest#GIVEN_request_is_blank_WHEN_validateIdRequest_SHOULD_throw_bad_request()}
 *  {@link when_validateIdRequest#GIVEN_request_null_WHEN_validateIdRequest_SHOULD_throw_bad_request()}
 *  {@link when_validateIdRequest#GIVEN_request_sucessfully_WHEN_validateIdRequest_SHOULD_not_throw_any_exception()}
 *}
 *{@link when_getEntityById} {
 *  {@link when_getEntityById#GIVEN_id_request_not_found_WHEN_getEntityById_SHOULD_throw_not_found_request()}
 *}
 *{@link when_convertEntityToResponse} {
 *  {@link when_convertEntityToResponse#GIVEN_request_sucessfully_WHEN_convertEntityToResponse_SHOULD_return_reponse_sucessfully()}
 *}
 * </pre>
 */
@ExtendWith(MockitoExtension.class)
final class GetByIdServiceTest extends GetByIdServiceFixture {

    @InjectMocks 
    @Spy
    GetByIdService getByIdService;
    
    @Mock
    AnyRepository anyRepositoryMock;

    /**
     *  testing all scenarios from {@link GetByIdService#getById(String)}
     */
    @Nested
    class when_getById {

        @Test
        void GIVEN_request_sucessfully_WHEN_getById_SHOULD_run_sucessfully(){

            final AnyEntity anyEntityGotById = ANY_ENTITY_SUCCESSFULLY.get();
            final String requestIdInput = REQUEST_ID_SUCCESSFULLY.get();

            when(anyRepositoryMock.findById(anyString())).thenReturn(Optional.of(anyEntityGotById));

            final GetByIdService.Response expectedResponse = getByIdService.getById(requestIdInput);

            assertNotNull(expectedResponse);
            assertEquals(expectedResponse.id(), anyEntityGotById.getId());
            assertEquals(expectedResponse.lastUpdated(), anyEntityGotById.getLastUpdated());
            assertEquals(expectedResponse.created(), anyEntityGotById.getCreated());
            assertEquals(expectedResponse.name(), anyEntityGotById.getName());
        }

        @Test
        void GIVEN_request_sucessfully_WHEN_getById_SHOULD_run_all_steps_on_right_order(){

            final AnyEntity anyEntityGotById = ANY_ENTITY_SUCCESSFULLY.get();
            final String requestIdInput = REQUEST_ID_SUCCESSFULLY.get();

            when(anyRepositoryMock.findById(anyString())).thenReturn(Optional.of(anyEntityGotById));

            InOrder inOrder = inOrder(getByIdService);

            getByIdService.getById(requestIdInput);

            inOrder.verify(getByIdService).validateIdRequest(anyString());
            inOrder.verify(getByIdService).getEntityById(anyString());
            inOrder.verify(getByIdService).convertEntityToResponse(any(AnyEntity.class));
        
        }

    }

    /**
     *  testing all scenarios from {@link GetByIdService#validateIdRequest(String)}
     */
    @Nested
    class when_validateIdRequest {

        @Test
        void GIVEN_request_sucessfully_WHEN_validateIdRequest_SHOULD_not_throw_any_exception(){
            final String requestIdInput = REQUEST_ID_SUCCESSFULLY.get();
            assertDoesNotThrow(() -> getByIdService.validateIdRequest(requestIdInput));
        }

        @Test
        void GIVEN_request_null_WHEN_validateIdRequest_SHOULD_throw_bad_request(){
            final String requestIdNull = REQUEST_ID_NULL.get();

            final var expectedException = assertThrows(ResponseStatusException.class, () -> getByIdService.validateIdRequest(requestIdNull));

            final int expectedHttpStatus = 400;
            assertEquals(expectedHttpStatus , expectedException.getStatusCode().value());
            assertEquals(ErrMessage.ID_IS_REQUIRED.get(), expectedException.getBody().getDetail());
        }

        @Test
        void GIVEN_request_is_blank_WHEN_validateIdRequest_SHOULD_throw_bad_request(){
            final String requestIdBlank = REQUEST_ID_BLANK.get();

            final var expectedException = assertThrows(ResponseStatusException.class, () -> getByIdService.validateIdRequest(requestIdBlank));

            final int expectedHttpStatus = 400;
            assertEquals(expectedHttpStatus , expectedException.getStatusCode().value());
            assertEquals(ErrMessage.ID_IS_REQUIRED.get(), expectedException.getBody().getDetail());
        }

    }

    /**
     *  testing all scenarios from {@link GetByIdService#getEntityById(String)}
     */
    @Nested
    class when_getEntityById {

        @Test
        void GIVEN_id_request_not_found_WHEN_getEntityById_SHOULD_throw_not_found_request(){
 
            final String anyInvalidId = UUID.randomUUID().toString();
 
            when(anyRepositoryMock.findById(anyString())).thenReturn(Optional.empty());

            final var expectedException = assertThrows(ResponseStatusException.class, () -> getByIdService.getEntityById(anyInvalidId));

            final int expectedHttpStatus = 404;
            assertEquals(expectedHttpStatus , expectedException.getStatusCode().value());
            assertEquals(ErrMessage.ID_NOT_FOUND.get(anyInvalidId) , expectedException.getBody().getDetail());
        }
        
    }

    /**
     *  testing all scenarios from {@link GetByIdService#convertEntityToResponse(AnyEntity)}
     */
    @Nested
    class when_convertEntityToResponse {

        @Test
        void GIVEN_request_sucessfully_WHEN_convertEntityToResponse_SHOULD_return_reponse_sucessfully(){
            final AnyEntity anyEntityInput = ANY_ENTITY_SUCCESSFULLY.get();

            final var expectedResponse =  getByIdService.convertEntityToResponse(anyEntityInput);

            assertNotNull(expectedResponse);
            assertEquals(expectedResponse.id(), anyEntityInput.getId());
            assertEquals(expectedResponse.lastUpdated(), anyEntityInput.getLastUpdated());
            assertEquals(expectedResponse.created(), anyEntityInput.getCreated());
            assertEquals(expectedResponse.name(), anyEntityInput.getName());
        }
        
    }
    
}
