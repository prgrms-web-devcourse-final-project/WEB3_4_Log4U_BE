package com.example.log4u.domain.supports.service;

import com.example.log4u.domain.supports.repository.SupportRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SupportService {
    private final SupportRepository supportRepository;
}
