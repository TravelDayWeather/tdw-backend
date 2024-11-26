package com.example.tdw_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherServiceImpl implements WeatherService {

    @Value("${weather.apiKey}")
    String API_KEY;

    @Value("${weather.baseUrl}")
    String BASE_URL;

    @Override
    public String getWeather(String city) {
        String url = String.format("%s?q=%s&appid=%s&units=metric", BASE_URL, city, API_KEY);

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, String.class);
    }
}
