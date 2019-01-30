package uz.paymo.notifier.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by rafatdin on 1/14/19.
 */
public class ApiHelper {
    public static ResponseEntity<String> sendRequest(String url, String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new ApiHelper.RestErrorHandler());
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        return response;
    }

    public static ResponseEntity<String> sendGetRequest(String url) {
        HttpHeaders headers = new HttpHeaders();

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new ApiHelper.RestErrorHandler());

        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        return response;
    }

    static class RestErrorHandler implements ResponseErrorHandler {
        @Override
        public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
            return clientHttpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR ||
                    clientHttpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR;
        }

        @Override
        public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
            Logger logger = LogManager.getLogger(ApiHelper.class);
            logger.error("Response error: {} {}", clientHttpResponse.getStatusCode(), clientHttpResponse.getStatusText());
        }
    }
}
