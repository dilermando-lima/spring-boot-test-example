package demo.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

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
import demo.repository.AnyRepository;

/**
 * <p>Tests of {@link RemoveByIdService}</p>
 * <pre>
 *{@link when_removeById} {
 *  {@link when_removeById#GIVEN_request_sucessfully_WHEN_getById_SHOULD_run_sucessfully()}
 *  {@link when_removeById#GIVEN_request_sucessfully_WHEN_getById_SHOULD_run_all_steps_on_right_order()}
 *}
 *{@link when_validateIdRequest} {
 *  {@link when_validateIdRequest#GIVEN_request_is_blank_WHEN_validateIdRequest_SHOULD_throw_bad_request()}
 *  {@link when_validateIdRequest#GIVEN_request_null_WHEN_validateIdRequest_SHOULD_throw_bad_request()}
 *  {@link when_validateIdRequest#GIVEN_request_sucessfully_WHEN_validateIdRequest_SHOULD_not_throw_any_exception()}
 *}
 *{@link when_validateIdExists} {
 *  {@link when_validateIdExists#GIVEN_id_request_not_found_WHEN_validateIdExists_SHOULD_throw_not_found_request()}
 *}
 * </pre>
 */
@ExtendWith(MockitoExtension.class)
final class RemoveByIdServiceTest extends RemoveByIdServiceFixture {

    @InjectMocks 
    @Spy
    RemoveByIdService removeByIdService;
    
    @Mock
    AnyRepository anyRepositoryMock;

    /**
     *  testing all scenarios from {@link RemoveByIdService#removeById(String)}
     */
    @Nested
    class when_removeById {

        @Test
        void GIVEN_request_sucessfully_WHEN_getById_SHOULD_run_sucessfully(){

            final String requestIdInput = REQUEST_ID_SUCCESSFULLY.get();

            when(anyRepositoryMock.existsById(anyString())).thenReturn(true);

            assertDoesNotThrow(() -> removeByIdService.removeById(requestIdInput));
        }

        @Test
        void GIVEN_request_sucessfully_WHEN_getById_SHOULD_run_all_steps_on_right_order(){

             final String requestIdInput = REQUEST_ID_SUCCESSFULLY.get();

            when(anyRepositoryMock.existsById(anyString())).thenReturn(true);

            InOrder inOrder = inOrder(removeByIdService);

            removeByIdService.removeById(requestIdInput);

            inOrder.verify(removeByIdService).validateIdRequest(anyString());
            inOrder.verify(removeByIdService).validateIdExists(anyString());
            inOrder.verify(removeByIdService).remove(anyString());
        
        }

    }

    /**
     *  testing all scenarios from {@link RemoveByIdService#validateIdRequest(String)}
     */
    @Nested
    class when_validateIdRequest {

        @Test
        void GIVEN_request_sucessfully_WHEN_validateIdRequest_SHOULD_not_throw_any_exception(){
            final String requestIdInput = REQUEST_ID_SUCCESSFULLY.get();
            assertDoesNotThrow(() -> removeByIdService.validateIdRequest(requestIdInput));
        }

        @Test
        void GIVEN_request_null_WHEN_validateIdRequest_SHOULD_throw_bad_request(){
            final String requestIdNull = REQUEST_ID_NULL.get();

            final var expectedException = assertThrows(ResponseStatusException.class, () -> removeByIdService.validateIdRequest(requestIdNull));

            final int expectedHttpStatus = 400;
            assertEquals(expectedHttpStatus , expectedException.getStatusCode().value());
            assertEquals(ErrMessage.ID_IS_REQUIRED.get(), expectedException.getBody().getDetail());
        }

        @Test
        void GIVEN_request_is_blank_WHEN_validateIdRequest_SHOULD_throw_bad_request(){
            final String requestIdBlank = REQUEST_ID_BLANK.get();

            final var expectedException = assertThrows(ResponseStatusException.class, () -> removeByIdService.validateIdRequest(requestIdBlank));

            final int expectedHttpStatus = 400;
            assertEquals(expectedHttpStatus , expectedException.getStatusCode().value());
            assertEquals(ErrMessage.ID_IS_REQUIRED.get(), expectedException.getBody().getDetail());
        }

    }

    /**
     *  testing all scenarios from {@link RemoveByIdService#validateIdExists(String)}
     */
    @Nested
    class when_validateIdExists {

        @Test
        void GIVEN_id_request_not_found_WHEN_validateIdExists_SHOULD_throw_not_found_request(){
 
            final String anyInvalidId = UUID.randomUUID().toString();
 
            when(anyRepositoryMock.existsById(anyString())).thenReturn(false);

            final var expectedException = assertThrows(ResponseStatusException.class, () -> removeByIdService.validateIdExists(anyInvalidId));

            final int expectedHttpStatus = 404;
            assertEquals(expectedHttpStatus , expectedException.getStatusCode().value());
            assertEquals(ErrMessage.ID_NOT_FOUND.get(anyInvalidId) , expectedException.getBody().getDetail());
        }
        
    }
    
}
