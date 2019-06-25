package org.ohdsi.gisservice.service.client;

import com.odysseusinc.arachne.execution_engine_common.client.FeignSpringFormEncoder;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.form.spring.converter.SpringManyMultipartFilesReader;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.http.converter.HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

public class CommonFeignClientConfig {

    /*@Value("${executionEngine.url}")
    private String url;

    @Bean
    public ExecutionEngineClient engineClient() {

        return Feign.builder()
                // .client(arachneHttpClientBuilder.build(proxyEnabledForEngine))
                .encoder(new FeignSpringFormEncoder())
                .decoder(getFeignDecoder()) //new JacksonDecoder())
                // .requestInterceptor(rt -> rt.header("Authorization", RestUtils.checkCredentials(executionEngineToken)))
                .logger(new Slf4jLogger(ExecutionEngineClient.class))
                .logLevel(Logger.Level.FULL)
                .target(ExecutionEngineClient.class, url);
    }*/

    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    protected Encoder getEncoder() {

        return new FeignSpringFormEncoder();
    }

    protected Decoder getDecoder() {

        List<HttpMessageConverter<?>> springConverters =
                messageConverters.getObject().getConverters();

        List<HttpMessageConverter<?>> decoderConverters =
                new ArrayList<HttpMessageConverter<?>>(springConverters.size() + 1);

        decoderConverters.addAll(springConverters);
        decoderConverters.add(new SpringManyMultipartFilesReader(4096));

        HttpMessageConverters httpMessageConverters = new HttpMessageConverters(decoderConverters);

        return new SpringDecoder(() -> httpMessageConverters);
    }
}
