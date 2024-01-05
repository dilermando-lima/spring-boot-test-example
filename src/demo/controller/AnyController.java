package demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

@RestController
@RequestMapping("/any-controller")
public class AnyController {

    @Autowired CreateService createService;
    @Autowired ListService listService;
    @Autowired GetByIdService getByIdService;
    @Autowired RemoveByIdService removeByIdService;
    @Autowired UpdateByIdService updateByIdService;

    @PostMapping
    public ResponseEntity<CreateService.Response> create(@RequestBody(required = false) CreateService.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<ListService.ResponseItem>> list(
            @RequestParam(name = "numPage", required = false) Integer numPage,
            @RequestParam(name = "sizePage", required = false) Integer sizePage,
            @RequestParam(name = "filter", required = false) String filter) {
        return ResponseEntity.ok(listService.list(new ListService.Request(numPage, sizePage, filter)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetByIdService.Response> getById(@PathVariable(name = "id") String id) {
        return ResponseEntity.ok(getByIdService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeById(@PathVariable(name = "id") String id) {
        removeByIdService.removeById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> removeById(@PathVariable(name = "id") String id, @RequestBody(required = false) UpdateByIdService.Request request) {
        updateByIdService.updateById(id, request);
        return ResponseEntity.noContent().build();
    }

}
