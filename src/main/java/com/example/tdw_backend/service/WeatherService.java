package com.example.tdw_backend.service;

import org.springframework.stereotype.Service;

@Service
public interface WeatherService {

    String getWeather(String city);
}
