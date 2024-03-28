package demo.controller;



import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import demo.repository.AnyRepository;
import demo.service.CreateService;
import demo.service.GetByIdService;
import demo.service.ListService;
import demo.service.ListService.Request;
import demo.service.RemoveByIdService;
import demo.service.UpdateByIdService;


/**
 * <p>Tests of {@link AnyController}</p>
 * <pre>
 *{@link when_create} {
 *  {@link when_create#GIVEN_valid_request_WHEN_create_SHOULD_run_sucessfully()}
 *  {@link when_create#GIVEN_thow_ResponseStatusException_with_badrequest_WHEN_create_SHOULD_return_error_400()}
 *  {@link when_create#GIVEN_thow_RuntimeException_WHEN_create_SHOULD_return_error_500()}
 *}
 *{@link when_updateById} {
 *  {@link when_updateById#GIVEN_valid_request_WHEN_updateById_SHOULD_run_sucessfully()}
 *  {@link when_updateById#GIVEN_thow_ResponseStatusException_with_badrequest_WHEN_updateById_SHOULD_return_error_400()}
 *  {@link when_updateById#GIVEN_thow_RuntimeException_WHEN_updateById_SHOULD_return_error_500()}
 *}
 *{@link on_specific_scenarios} {
 *  {@link on_specific_scenarios#GIVEN_invalid_path_SHOULD_return_not_found()}
 *}
 *{@link when_removeById} {
 *  {@link when_removeById#GIVEN_valid_request_WHEN_removeById_SHOULD_run_sucessfully()}
 *  {@link when_removeById#GIVEN_thow_ResponseStatusException_with_badrequest_WHEN_removeById_SHOULD_return_error_400()}
 *  {@link when_removeById#GIVEN_thow_RuntimeException_WHEN_removeById_SHOULD_return_error_500()}
 *}
 *{@link when_list} {
 *  {@link when_list#GIVEN_request_without_filter_or_pagination_WHEN_list_SHOULD_run_sucessfully()}
 *  {@link when_list#GIVEN_valid_request_and_empty_reponse_list_WHEN_list_SHOULD_run_sucessfully()}
 *  {@link when_list#GIVEN_thow_ResponseStatusException_with_badrequest_WHEN_list_SHOULD_return_error_400()}
 *  {@link when_list#GIVEN_thow_RuntimeException_WHEN_list_SHOULD_return_error_500()}
 *}
  *{@link when_getById} {
 *  {@link when_getById#GIVEN_valid_request_WHEN_getById_SHOULD_run_sucessfully()}
 *  {@link when_getById#GIVEN_thow_ResponseStatusException_with_badrequest_WHEN_getById_SHOULD_return_error_400()}
 *  {@link when_getById#GIVEN_thow_RuntimeException_WHEN_getById_SHOULD_return_error_500()}
 *}
 * </pre>
 */
@WebMvcTest(controllers = AnyController.class, properties = {"--app.default-size-page=10"})
@ComponentScan(
    basePackageClasses = {
        CreateService.class,
        ListService.class,
        GetByIdService.class,
        RemoveByIdService.class,
        UpdateByIdService.class
    }
)
final class AnyControllerTest extends AnyControllerTestFixture {

    @Autowired private MockMvc mockMvc;
    @MockBean private CreateService createService;
    @MockBean private UpdateByIdService updateByIdService;
    @MockBean private RemoveByIdService removeByIdService;
    @MockBean private GetByIdService getByIdService;
    @MockBean private ListService listService;
    @MockBean private AnyRepository anyRepository;

    /**
     *  testing all scenarios from {@link AnyController#create(demo.service.CreateService.Request)}
     */
    @Nested
    class when_create{

        @Test
        void GIVEN_valid_request_WHEN_create_SHOULD_run_sucessfully() throws Exception{
            final var request = OK_BODY_REQUEST_CREATE.get();
            final var responseExpected = OK_REPONSE_CREATE_SERVICE.get();
    
            when(createService.create(request)).thenReturn(responseExpected);
            
            MOCK_REQUEST_CREATE
                .setMock(mockMvc, request)      
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", equalTo(responseExpected.id())));
        }
    
        @Test
        void GIVEN_thow_ResponseStatusException_with_badrequest_WHEN_create_SHOULD_return_error_400() throws Exception{
    
            final var e = new  ResponseStatusException(HttpStatus.BAD_REQUEST, RANDON_SHORT_STRING.get());
    
            when(createService.create(any(CreateService.Request.class))).thenThrow(e);
            
            MOCK_REQUEST_CREATE
                .setMock(mockMvc, OK_BODY_REQUEST_CREATE.get()) 
                .andExpect(status().is(e.getBody().getStatus()))
                .andExpect(jsonPath("$.status", equalTo(e.getBody().getStatus())))
                .andExpect(jsonPath("$.error", equalTo(e.getBody().getDetail())));
        }
    
    
        @Test
        void GIVEN_thow_RuntimeException_WHEN_create_SHOULD_return_error_500() throws Exception{
    
            final var e = new  RuntimeException(RANDON_SHORT_STRING.get());
    
            when(createService.create(any(CreateService.Request.class))).thenThrow(e);
    
            final var expectedResponseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            
            MOCK_REQUEST_CREATE
                .setMock(mockMvc, OK_BODY_REQUEST_CREATE.get()) 
                .andExpect(status().is(expectedResponseStatus.value()))
                .andExpect(jsonPath("$.status", equalTo(expectedResponseStatus.value())))
                .andExpect(jsonPath("$.error", equalTo(e.getMessage())));
        }


    }

    /**
     *  testing all scenarios from {@link AnyController#updateById(String, demo.service.UpdateByIdService.Request)}
     */
    @Nested
    class when_updateById{

        @Test
        void GIVEN_valid_request_WHEN_updateById_SHOULD_run_sucessfully() throws Exception{
            final var request = OK_BODY_REQUEST_UPDATE_BY_ID.get();
            final var id = RANDON_UUID_STRING.get();

            doNothing().when(updateByIdService).updateById(id, request);

            MOCK_REQUEST_UPDATE_BY_ID
                .setMock(mockMvc, id, request)      
                .andExpect(status().isNoContent());

        }
    
        @Test
        void GIVEN_thow_ResponseStatusException_with_badrequest_WHEN_updateById_SHOULD_return_error_400() throws Exception{
    
            final var e = new  ResponseStatusException(HttpStatus.BAD_REQUEST, RANDON_SHORT_STRING.get());
    
            doThrow(e).when(updateByIdService).updateById(any(String.class), any(UpdateByIdService.Request.class));
            
            MOCK_REQUEST_UPDATE_BY_ID
                .setMock(mockMvc, RANDON_UUID_STRING.get(), OK_BODY_REQUEST_UPDATE_BY_ID.get()) 
                .andExpect(status().is(e.getBody().getStatus()))
                .andExpect(jsonPath("$.status", equalTo(e.getBody().getStatus())))
                .andExpect(jsonPath("$.error", equalTo(e.getBody().getDetail())));
        }
    
    
        @Test
        void GIVEN_thow_RuntimeException_WHEN_updateById_SHOULD_return_error_500() throws Exception{
    
            final var e = new  RuntimeException(RANDON_SHORT_STRING.get());
    
            doThrow(e).when(updateByIdService).updateById(any(String.class), any(UpdateByIdService.Request.class));
    
            final var expectedResponseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            
            MOCK_REQUEST_UPDATE_BY_ID
                .setMock(mockMvc, RANDON_UUID_STRING.get(), OK_BODY_REQUEST_UPDATE_BY_ID.get()) 
                .andExpect(status().is(expectedResponseStatus.value()))
                .andExpect(jsonPath("$.status", equalTo(expectedResponseStatus.value())))
                .andExpect(jsonPath("$.error", equalTo(e.getMessage())));
    
        }

    }


    /**
     *  testing specific scenarios in {@link AnyController}
     */
    @Nested
    class on_specific_scenarios{

        @Test
        void GIVEN_invalid_path_SHOULD_return_not_found() throws Exception{
            MOCK_REQUEST_PATH_NOT_FOUND
                .setMock(mockMvc)
                .andExpect(status().isNotFound());
        }

    }


    /**
     *  testing all scenarios from {@link AnyController#removeById(String)}
     */
    @Nested
    class when_removeById{

        @Test
        void GIVEN_valid_request_WHEN_removeById_SHOULD_run_sucessfully() throws Exception{
    
            final var id = RANDON_UUID_STRING.get();
    
            doNothing().when(removeByIdService).removeById(id);
    
            MOCK_REQUEST_REMOVE_BY_ID
                .setMock(mockMvc, id)
                .andExpect(status().isNoContent());

        }
    
        @Test
        void GIVEN_thow_ResponseStatusException_with_badrequest_WHEN_removeById_SHOULD_return_error_400() throws Exception{
    
            final var e = new  ResponseStatusException(HttpStatus.BAD_REQUEST, RANDON_SHORT_STRING.get());
    
            doThrow(e).when(removeByIdService).removeById(any(String.class));
            
            MOCK_REQUEST_REMOVE_BY_ID
                .setMock(mockMvc, RANDON_UUID_STRING.get())
                .andExpect(status().is(e.getBody().getStatus()))
                .andExpect(jsonPath("$.status", equalTo(e.getBody().getStatus())))
                .andExpect(jsonPath("$.error", equalTo(e.getBody().getDetail())));
        }
    
        @Test
        void GIVEN_thow_RuntimeException_WHEN_removeById_SHOULD_return_error_500() throws Exception{
    
            final var e = new  RuntimeException(RANDON_SHORT_STRING.get());
    
            doThrow(e).when(removeByIdService).removeById(any(String.class));
    
            final var expectedResponseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            
            MOCK_REQUEST_REMOVE_BY_ID
                .setMock(mockMvc, RANDON_UUID_STRING.get())
                .andExpect(status().is(expectedResponseStatus.value()))
                .andExpect(jsonPath("$.status", equalTo(expectedResponseStatus.value())))
                .andExpect(jsonPath("$.error", equalTo(e.getMessage())));
    
        }

    }


    /**
     *  testing all scenarios from {@link AnyController#list(demo.controller.AnyController.RequestList)}
     */
    @Nested
    class when_list{

        @Test
        void GIVEN_request_without_filter_or_pagination_WHEN_list_SHOULD_run_sucessfully() throws Exception{
    
            final var request = OK_BODY_REQUEST_LIST_WITH_NO_FILTER_OR_PAGINATION.get();
            final var responseExpected = OK_REPONSE_LIST_SERVICE_WITH_ONLY_ONE_ITEM.get();

            when(listService.list(new Request(request.numPage(), request.sizePage(), request.filter()))).thenReturn(responseExpected);
    
            MOCK_REQUEST_LIST
                .setMock(mockMvc, request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(responseExpected.size())))
                .andExpect(jsonPath("$[0].id", equalTo(responseExpected.get(0).id())))
                .andExpect(jsonPath("$[0].name", equalTo(responseExpected.get(0).name())));
    
        }
    
        @Test
        void GIVEN_valid_request_and_empty_reponse_list_WHEN_list_SHOULD_run_sucessfully() throws Exception{
    
            final var request = OK_BODY_REQUEST_LIST_WITH_NO_FILTER_OR_PAGINATION.get();
            final var responseExpected = OK_REPONSE_LIST_SERVICE_WITH_EMPTY_LIST.get();
    
            when(listService.list(new Request(request.numPage(), request.sizePage(), request.filter()))).thenReturn(responseExpected);
    
            MOCK_REQUEST_LIST
                .setMock(mockMvc, request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(responseExpected.size())))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    
        }

        @Test
        void GIVEN_thow_ResponseStatusException_with_badrequest_WHEN_list_SHOULD_return_error_400() throws Exception{
    
            final var e = new  ResponseStatusException(HttpStatus.BAD_REQUEST, RANDON_SHORT_STRING.get());
    
            when(listService.list(any(ListService.Request.class))).thenThrow(e);
            
            MOCK_REQUEST_LIST
                .setMock(mockMvc, OK_BODY_REQUEST_LIST_WITH_NO_FILTER_OR_PAGINATION.get())
                .andExpect(status().is(e.getBody().getStatus()))
                .andExpect(jsonPath("$.status", equalTo(e.getBody().getStatus())))
                .andExpect(jsonPath("$.error", equalTo(e.getBody().getDetail())));
        }
    
    
        @Test
        void GIVEN_thow_RuntimeException_WHEN_list_SHOULD_return_error_500() throws Exception{
    
            final var e = new  RuntimeException(RANDON_SHORT_STRING.get());
    
            when(listService.list(any(ListService.Request.class))).thenThrow(e);
    
            final var expectedResponseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            
            MOCK_REQUEST_LIST
                .setMock(mockMvc, OK_BODY_REQUEST_LIST_WITH_NO_FILTER_OR_PAGINATION.get())
                .andExpect(status().is(expectedResponseStatus.value()))
                .andExpect(jsonPath("$.status", equalTo(expectedResponseStatus.value())))
                .andExpect(jsonPath("$.error", equalTo(e.getMessage())));
        }

    }


    /**
     *  testing all scenarios from {@link AnyController#getById(String)}
     */
    @Nested
    class when_getById{

        @Test
        void GIVEN_valid_request_WHEN_getById_SHOULD_run_sucessfully() throws Exception{
            final var id = RANDON_UUID_STRING.get();
            final var responseExpected = OK_REPONSE_GET_BY_ID_SERVICE.get();
    
            when(getByIdService.getById(id)).thenReturn(responseExpected);
            
            MOCK_REQUEST_GET_BY_ID
                .setMock(mockMvc, id)      
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(responseExpected.id())))
                .andExpect(jsonPath("$.name", equalTo(responseExpected.name())))
                .andExpect(jsonPath("$.created",startsWith(responseExpected.created().truncatedTo(ChronoUnit.MICROS).toString())))
                .andExpect(jsonPath("$.lastUpdated",startsWith(responseExpected.lastUpdated().truncatedTo(ChronoUnit.MICROS).toString())));
        }
    
        @Test
        void GIVEN_thow_ResponseStatusException_with_badrequest_WHEN_getById_SHOULD_return_error_400() throws Exception{
    
            final var e = new  ResponseStatusException(HttpStatus.BAD_REQUEST, RANDON_SHORT_STRING.get());
    
            when(getByIdService.getById(anyString())).thenThrow(e);
            
            MOCK_REQUEST_GET_BY_ID
                .setMock(mockMvc, RANDON_UUID_STRING.get()) 
                .andExpect(status().is(e.getBody().getStatus()))
                .andExpect(jsonPath("$.status", equalTo(e.getBody().getStatus())))
                .andExpect(jsonPath("$.error", equalTo(e.getBody().getDetail())));
        }
    
    
        @Test
        void GIVEN_thow_RuntimeException_WHEN_getById_SHOULD_return_error_500() throws Exception{
    
            final var e = new  RuntimeException(RANDON_SHORT_STRING.get());
    
            when(getByIdService.getById(anyString())).thenThrow(e);
    
            final var expectedResponseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            
            MOCK_REQUEST_GET_BY_ID
                .setMock(mockMvc, RANDON_UUID_STRING.get()) 
                .andExpect(status().is(expectedResponseStatus.value()))
                .andExpect(jsonPath("$.status", equalTo(expectedResponseStatus.value())))
                .andExpect(jsonPath("$.error", equalTo(e.getMessage())));
        }


    }
   

   



    
}

