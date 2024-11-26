package com.example.tdw_backend.controller;

import com.example.tdw_backend.security.JwtTokenProvider;
import com.example.tdw_backend.service.WeatherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping("/weather")
    public ResponseEntity<String> getWeather(@RequestParam("city") String city,
                             @RequestHeader("Authorization") String token) {

        if (!jwtTokenProvider.isTokenExpired(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }

        String weatherData = weatherService.getWeather(city);
        return ResponseEntity.ok(weatherData);
    }
}
