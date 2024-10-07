package com.inn.cafe.service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface UserService {

    public ResponseEntity<String> signUp(Map<String, String> requestMp);

    ResponseEntity<String> login(Map<String, String> requestMap);
}
