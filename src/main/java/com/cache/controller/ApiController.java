package com.cache.controller;

import com.cache.exeption.NotFoundHttpException;
import com.cache.model.CachValue;
import com.cache.model.entity.ObjectKeyValue;
import com.cache.repository.ApiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.cache.model.CachValue.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/")
public class ApiController {
    @Autowired
    ApiRepository repository;


    @GetMapping("/{id}")
    public ResponseEntity getByKey(@PathVariable(value = "id") String key) {
        ObjectKeyValue ob = cash.get(key);
        if (ob.getValue() == null) {
            ob = repository.findById(key).orElseThrow(NotFoundHttpException::new);
        }

        return ResponseEntity.status(HttpStatus.OK).allow(HttpMethod.GET).contentType(MediaType.APPLICATION_JSON_UTF8).body(ob);

    }

    @PostMapping
    public ResponseEntity postOrPut(@Valid @RequestBody ObjectKeyValue ob) {
        ob = repository.save(ob);
        cash.put(ob);
        return ResponseEntity.status(HttpStatus.OK).allow(HttpMethod.PUT).contentType(MediaType.APPLICATION_JSON_UTF8).body(ob);
    }

}
