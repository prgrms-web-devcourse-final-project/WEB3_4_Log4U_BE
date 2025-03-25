package com.example.log4u.domain.supports.controller;

import com.example.log4u.domain.supports.dto.SupportCreateRequestDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/supports")
public class SupportController {

    @PostMapping
    public ResponseEntity<Void> createSupport(
            @RequestBody @Valid SupportCreateRequestDto supportCreateRequestDto
            ){
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<String>> getSupportPage(){
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{supportId}")
    public ResponseEntity<String> getSupportBySupportId(
            @PathVariable Long supportId){
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
