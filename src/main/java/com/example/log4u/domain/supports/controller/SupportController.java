package com.example.log4u.domain.supports.controller;

import com.example.log4u.domain.supports.dto.SupportCreateRequestDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SupportController {
    public ResponseEntity<Void> createSupport(
            @RequestBody @Valid SupportCreateRequestDto supportCreateRequestDto
            ){
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
