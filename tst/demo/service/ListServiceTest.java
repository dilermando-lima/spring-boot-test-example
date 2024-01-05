package demo.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.server.ResponseStatusException;

import demo.constant.ErrMessage;
import demo.model.AnyEntity;
import demo.repository.AnyRepository;
import demo.service.ListService.Request;
import demo.service.ListService.ResponseItem;

/**
 * <p>Tests of {@link ListService}</p>
 * <pre>
 *{@link when_list} {
 *  {@link when_list#GIVEN_request_sucessfully_WHEN_list_SHOULD_run_all_steps_on_right_order(Page)}
 *  {@link when_list#GIVEN_request_sucessfully_WHEN_list_SHOULD_run_sucessfully(Page)}
 *}
 *{@link when_validateRequest} {
 *  {@link when_validateRequest#GIVEN_request_null_WHEN_validateRequest_SHOULD_throw_internal_server_error()}
 *  {@link when_validateRequest#GIVEN_request_sucessfully_WHEN_validateRequest_SHOULD_not_throw_any_exception()}
 *  {@link when_validateRequest#GIVEN_request_with_all_attrs_null_WHEN_validateRequest_SHOULD_not_throw_any_exception()}
 *  {@link when_validateRequest#GIVEN_request_with_numPage_less_than_zero_WHEN_validateRequest_SHOULD_throw_bad_request()}
 *  {@link when_validateRequest#GIVEN_request_with_sizePage_less_than_zero_WHEN_validateRequest_SHOULD_throw_bad_request()}
 *  {@link when_validateRequest#GIVEN_request_with_sizePage_more_than_max_allowed_WHEN_validateRequest_SHOULD_throw_bad_request()}
 *}
 * </pre>
 */
@ExtendWith(MockitoExtension.class)
final class ListServiceTest extends ListServiceFixture {

    @InjectMocks 
    @Spy
    ListService listService;
    
    @Mock
    AnyRepository anyRepositoryMock;

    /**
     *  testing all scenarios from {@link ListService#list(demo.service.ListService.Request) }
     */
    @Nested
    class when_list {

        @Test
        void GIVEN_request_sucessfully_WHEN_list_SHOULD_run_sucessfully(@Mock Page<AnyEntity> pageMock){

            final List<AnyEntity> listEntityGotFromDatabase = LIST_ANY_ENTITY_SUCCESSFULLY.get();
            final Request requestIdInput = REQUEST_SUCCESSFULLY.get();

            
            when(pageMock.getContent()).thenReturn(listEntityGotFromDatabase);

            when(anyRepositoryMock.findAll(ArgumentMatchers.<Specification<AnyEntity>>any(), ArgumentMatchers.<Pageable>any()))
                .thenReturn(pageMock);

            final List<ResponseItem> expectedResponse = listService.list(requestIdInput);

            assertNotNull(expectedResponse);
            assertEquals(2, expectedResponse.size());
            assertEquals(listEntityGotFromDatabase.get(0).getId(), expectedResponse.get(0).id());
            assertEquals(listEntityGotFromDatabase.get(0).getName(), expectedResponse.get(0).name());
            assertEquals(listEntityGotFromDatabase.get(1).getId(), expectedResponse.get(1).id());
            assertEquals(listEntityGotFromDatabase.get(1).getName(), expectedResponse.get(1).name());
 
        }

        @Test
        void GIVEN_request_sucessfully_WHEN_list_SHOULD_run_all_steps_on_right_order(@Mock Page<AnyEntity> pageMock){

            final List<AnyEntity> listEntityGotFromDatabase = LIST_ANY_ENTITY_SUCCESSFULLY.get();
            final ListService.Request requestIdInput = REQUEST_SUCCESSFULLY.get();

            
            when(pageMock.getContent()).thenReturn(listEntityGotFromDatabase);

            when(anyRepositoryMock.findAll(ArgumentMatchers.<Specification<AnyEntity>>any(), ArgumentMatchers.<Pageable>any()))
                .thenReturn(pageMock);
    
            InOrder inOrder = inOrder(listService);
            
            listService.list(requestIdInput);

            inOrder.verify(listService).validateRequest(any(Request.class));
            inOrder.verify(listService).buildPageable(any(Request.class));
            inOrder.verify(listService).buildSpecification(any(Request.class));
            inOrder.verify(listService).listEntity(ArgumentMatchers.<Specification<AnyEntity>>any(), ArgumentMatchers.<Pageable>any());
            inOrder.verify(listService).convertListEntityToListResponse(ArgumentMatchers.<List<AnyEntity>>any());
            
        }

      
    }

    /**
     *  testing all scenarios from {@link ListService#buildPageable(demo.service.ListService.Request)}
     */
    @Nested
    class when_buildPageable {

      
    }


    /**
     *  testing all scenarios from {@link ListService#buildSpecification(demo.service.ListService.Request)}
     */
    @Nested
    class when_buildSpecification {

      
    }

    /**
     *  testing all scenarios from {@link ListService#validateRequest(demo.service.ListService.Request)}
     */
    @Nested
    class when_validateRequest {

        @Test
        void GIVEN_request_sucessfully_WHEN_validateRequest_SHOULD_not_throw_any_exception(){
            final ListService.Request requestInput = REQUEST_SUCCESSFULLY.get();
            assertDoesNotThrow(() -> listService.validateRequest(requestInput));
        }

        @Test
        void GIVEN_request_with_all_attrs_null_WHEN_validateRequest_SHOULD_not_throw_any_exception(){
            final ListService.Request requestWithAllAttrNull = REQUEST_WITH_ALL_ATTRS_NULL.get();
            assertDoesNotThrow(() -> listService.validateRequest(requestWithAllAttrNull));
        }

        @Test
        void GIVEN_request_null_WHEN_validateRequest_SHOULD_throw_internal_server_error(){
            final ListService.Request requestNullInput = REQUEST_NULL.get();

            final var expectedException = assertThrows(ResponseStatusException.class, () -> listService.validateRequest(requestNullInput));

            final int expectedHttpStatus = 500;
            assertEquals(expectedHttpStatus , expectedException.getStatusCode().value());
            assertEquals(ErrMessage.REQUEST_CANNOT_BE_NULL.get(), expectedException.getBody().getDetail());
        }

        @Test
        void GIVEN_request_with_numPage_less_than_zero_WHEN_validateRequest_SHOULD_throw_bad_request(){
            final ListService.Request requestWithNumPageLessThanZero = REQUEST_WITH_NUM_PAGE_LESS_THAN_ZERO.get();

            final var expectedException = assertThrows(ResponseStatusException.class, () -> listService.validateRequest(requestWithNumPageLessThanZero));

            final int expectedHttpStatus = 400;
            assertEquals(expectedHttpStatus , expectedException.getStatusCode().value());
            assertEquals(ErrMessage.NUMPAGE_MUST_BE_MORE_THAN_OR_EQUALS_ZERO.get(), expectedException.getBody().getDetail());
        }

        @Test
        void GIVEN_request_with_sizePage_less_than_zero_WHEN_validateRequest_SHOULD_throw_bad_request(){
            final ListService.Request requestWithsizePageLessThanZero = REQUEST_WITH_SIZE_PAGE_LESS_THAN_ZERO.get();

            final var expectedException = assertThrows(ResponseStatusException.class, () -> listService.validateRequest(requestWithsizePageLessThanZero));

            final int expectedHttpStatus = 400;
            assertEquals(expectedHttpStatus , expectedException.getStatusCode().value());
            assertEquals(ErrMessage.SIZEPAGE_MUST_BE_MORE_THAN_OR_EQUALS_ZERO.get(), expectedException.getBody().getDetail());
        }


        @Test
        void GIVEN_request_with_sizePage_more_than_max_allowed_WHEN_validateRequest_SHOULD_throw_bad_request(){

            final int maxSizePageAllowed = 5;
            
            when(listService.maxSizePageAllowed()).thenReturn(maxSizePageAllowed);

            final ListService.Request requestWithsizePageLessThanZero = REQUEST_WITH_SIZE_PAGE_MORE_THAN_X.apply(maxSizePageAllowed);

            final var expectedException = assertThrows(ResponseStatusException.class, () -> listService.validateRequest(requestWithsizePageLessThanZero));

            final int expectedHttpStatus = 400;
            assertEquals(expectedHttpStatus , expectedException.getStatusCode().value());
            assertEquals(ErrMessage.SIZEPAGE_MUST_BE_LESS_THAN_X.get(maxSizePageAllowed), expectedException.getBody().getDetail());
        }

    }

    /**
     *  testing all scenarios from {@link ListService#convertListEntityToListResponse(java.util.List)}
     */
    @Nested
    class when_convertListEntityToListResponse {

      
    }


   
    
}
