package com.lab2.chat.feign;

import com.lab2.chat.dto.UserReadDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user")
@CircuitBreaker(name = "userServiceBreaker")
public interface UserServiceClient {

    @GetMapping("/users/{userId}")
    ResponseEntity<UserReadDto> findUserById(@PathVariable Long userId);
}
