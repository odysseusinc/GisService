package org.ohdsi.gisservice.service.client;

import feign.Headers;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@FeignClient(
        name = "execution-engine",
        configuration = ExecutionEngineClient.ClientConfiguration.class
)
public interface ExecutionEngineClient {

    @RequestMapping(method = RequestMethod.POST, value = "/api/v1/analyze/sync")
    @Headers({
            "Content-Type: multipart/form-data"
    })
    MultipartFile[] executeSync(@RequestBody Map<String, ?> body);

    class ClientConfiguration extends CommonFeignClientConfig {

        @Bean
        public Encoder getEncoder() {
            return super.getEncoder();
        }

        @Bean
        public Decoder getDecoder() {
            return super.getDecoder();
        }
    }
}
