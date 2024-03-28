package demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import demo.service.CreateService;
import demo.service.GetByIdService;
import demo.service.ListService;
import demo.service.RemoveByIdService;
import demo.service.UpdateByIdService;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/any-controller")
public class AnyController {

    @Autowired CreateService createService;
    @Autowired ListService listService;
    @Autowired GetByIdService getByIdService;
    @Autowired RemoveByIdService removeByIdService;
    @Autowired UpdateByIdService updateByIdService;

    @Operation(summary = "Create a new entity")
    @PostMapping
    public ResponseEntity<CreateService.Response> create(@RequestBody(required = false) CreateService.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createService.create(request));
    }

    
    public record RequestList(
            @RequestParam(name = "numPage", required = false) Integer numPage,
            @RequestParam(name = "sizePage", required = false) Integer sizePage,
            @RequestParam(name = "filter", required = false) String filter
    ) {}
    
    @Operation(summary = "List all entities")
    @GetMapping
    public ResponseEntity<List<ListService.ResponseItem>> list(RequestList request) {
        return ResponseEntity.ok(listService.list(new ListService.Request(request.numPage, request.sizePage, request.filter)));
    }


    @Operation(summary = "Get entity by id")
    @GetMapping("/{id}")
    public ResponseEntity<GetByIdService.Response> getById(@PathVariable(name = "id") String id) {
        return ResponseEntity.ok(getByIdService.getById(id));
    }

    @Operation(summary = "Delete entity by id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeById(@PathVariable(name = "id") String id) {
        removeByIdService.removeById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update entity by id")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateById(@PathVariable(name = "id") String id, @RequestBody(required = false) UpdateByIdService.Request request) {
        updateByIdService.updateById(id, request);
        return ResponseEntity.noContent().build();
    }


    public record ResponseError(int status, String error) {}

    /**
     * @implNote try to avoid this in a real project. Look for {@link ControllerAdvice} as a configuration handler for a better implementation.
     */
    @ExceptionHandler({Throwable.class})
	public ResponseEntity<ResponseError> handleThrowableException(Throwable ex) {

		HttpStatusCode httpStatus;
        String error;

		if (ex instanceof ErrorResponse errorResponse) {
			httpStatus = errorResponse.getStatusCode();
            error = errorResponse.getBody().getDetail();
		} else {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            error = ex.getMessage();
		}
        return ResponseEntity
                .status(httpStatus)
                .body(new ResponseError(httpStatus.value(), error));

	}

}
