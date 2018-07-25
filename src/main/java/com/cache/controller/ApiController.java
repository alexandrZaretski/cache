package com.cache.controller;

import com.cache.exeption.NotFoundHttpException;
import com.cache.model.CachValue;
import com.cache.model.entity.ObjectKeyValue;
import com.cache.repository.ApiRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/")
public class ApiController {
    private static Logger logger = LogManager.getLogger(ApiController.class);

    @Autowired
    public ApiController(ApiRepository repository, CachValue cash) {
        this.repository = repository;
        this.cash = cash;
    }

    ApiRepository repository;
    CachValue cash;

    @GetMapping("/{id}")
    public ResponseEntity getByKey(@PathVariable(value = "id") String key) throws NullPointerException {
        ObjectKeyValue ob = cash.get(key);

        if (ob.getValue() == null) {
            ob = repository.findById(key).orElseThrow(NotFoundHttpException::new);
        }
        logger.debug("method Get " + ob + "key =" + key);
        return ResponseEntity.status(HttpStatus.OK).allow(HttpMethod.GET).contentType(MediaType.APPLICATION_JSON_UTF8).body(ob);

    }

    @PostMapping
    public ResponseEntity postOrPut(@Valid @RequestBody ObjectKeyValue ob) {
        ob = repository.save(ob);
        cash.put(ob);
        logger.debug("method Put  object=" + ob);
        return ResponseEntity.status(HttpStatus.OK).allow(HttpMethod.PUT).contentType(MediaType.APPLICATION_JSON_UTF8).body(ob);
    }

}
