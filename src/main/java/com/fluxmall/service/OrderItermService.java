package com.fluxmall.service;

import com.fluxmall.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderItermService {
    private final OrderItemRepository orderItemRepository;
}
