package com.example.tdw_backend.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatWebCustomConfig implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {


    // Invalid character found in the request target
    // [/api/login?loginRequest[email]=test%40test.com1&loginRequest[pw]=12345678!! ].
    // The valid characters are defined in RFC 7230 and RFC 3986

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        factory.addConnectorCustomizers(connector -> {
            // 쿼리 파라미터와 URL 경로에서 허용되는 문자를 확장
            connector.setProperty("relaxedQueryChars", "<>[\\]^`{|}");
            connector.setProperty("relaxedPathChars", "<>[\\]^`{|}");
        });
    }
}

