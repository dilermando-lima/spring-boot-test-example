package demo.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import demo.service.CreateService;
import demo.service.GetByIdService;
import demo.service.ListService.ResponseItem;
import demo.service.UpdateByIdService;

abstract sealed class AnyControllerTestFixture extends FixtureWebMvcBase permits AnyControllerTest  {

    private final String BASE_PATH = "/any-controller";

    protected final Supplier<UpdateByIdService.Request> OK_BODY_REQUEST_UPDATE_BY_ID = () -> {
        return new UpdateByIdService.Request(RANDON_SHORT_STRING.get());
    };

    protected final Supplier<AnyController.RequestList> OK_BODY_REQUEST_LIST_WITH_FILTER_AND_PAGINATION = () -> {
        return new AnyController.RequestList(RANDON_NUMBER_TO_1_TO_10.get(), RANDON_NUMBER_TO_1_TO_10.get(), RANDON_SHORT_STRING.get());
    };

    protected final Supplier<AnyController.RequestList> OK_BODY_REQUEST_LIST_WITH_NO_FILTER_OR_PAGINATION = () -> {
        return new AnyController.RequestList(null, null, null);
    };

    protected final Supplier<CreateService.Request> OK_BODY_REQUEST_CREATE = () -> {
        return new CreateService.Request(RANDON_SHORT_STRING.get());
    };
   
    protected final Supplier<CreateService.Response> OK_REPONSE_CREATE_SERVICE = () -> {
        return new CreateService.Response(RANDON_UUID_STRING.get());
    };


    protected final Supplier<GetByIdService.Response> OK_REPONSE_GET_BY_ID_SERVICE = () -> {
        return new GetByIdService.Response(RANDON_UUID_STRING.get(), RANDON_SHORT_STRING.get(), RANDON_DATE_BEFORE.apply(LocalDateTime.now()), LocalDateTime.now());
    };

    protected final Supplier<List<ResponseItem>> OK_REPONSE_LIST_SERVICE_WITH_ONLY_ONE_ITEM = () -> {
        return List.of(new ResponseItem(RANDON_UUID_STRING.get(), RANDON_SHORT_STRING.get()));
    };

    protected final Supplier<List<ResponseItem>> OK_REPONSE_LIST_SERVICE_WITH_EMPTY_LIST = () -> {
        return Collections.emptyList();
    };

    protected final BuildRequest MOCK_REQUEST_PATH_NOT_FOUND  = mock -> {
        return mock.perform(get(joinPathWithBAR(BASE_PATH + "ANY-WRONG-PATH")).contentType(APPLICATION_JSON_VALUE));
    };

    protected final BuildRequestByID MOCK_REQUEST_GET_BY_ID  = (mock, id) -> {
        return mock.perform(get(joinPathWithBAR(BASE_PATH, id)).contentType(APPLICATION_JSON_VALUE));
    };

    protected final BuildRequestByIDAndBodyRequest<UpdateByIdService.Request> MOCK_REQUEST_UPDATE_BY_ID  =(mock, id, request) -> {
        return mock.perform(put(joinPathWithBAR(BASE_PATH, id)).content(toJson(request)).contentType(APPLICATION_JSON_VALUE));
    };

    protected final BuildRequestByBodyRequest<CreateService.Request> MOCK_REQUEST_CREATE  =(mock, request) -> {
        return mock.perform(post(BASE_PATH).content(toJson(request)).contentType(APPLICATION_JSON_VALUE));
    };
    
    protected final BuildRequestByID MOCK_REQUEST_REMOVE_BY_ID  = (mock, id) -> {
        return mock.perform(delete(joinPathWithBAR(BASE_PATH, id)).contentType(APPLICATION_JSON_VALUE));
    };
    
    protected final BuildRequestByBodyRequest<AnyController.RequestList>  MOCK_REQUEST_LIST  = (mock, request) -> {
        return mock.perform(get(joinPathWithBAR(BASE_PATH)).content(toJson(request)).contentType(APPLICATION_JSON_VALUE));
    };
}

