package chapter6.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

import chapter5.dto.Instance;

public class CustomHystrixCommand extends HystrixCommand<Instance> {
    private static Logger logger = LoggerFactory.getLogger(CustomHystrixCommand.class);
    private RestTemplate restTemplate;
    private String serviceId;

    protected CustomHystrixCommand(RestTemplate restTemplate, String serviceId) {
        super(HystrixCommandGroupKey.Factory.asKey("CustomServiceGroup"));

        this.restTemplate = restTemplate;
        this.serviceId = serviceId;
    }


    @Override
    protected Instance run() throws Exception {
        Instance instance = restTemplate
                .getForEntity("http://FEIGN-SERVICE/feign-service/instance/{serviceId}", Instance.class, serviceId)
                .getBody();
        return instance;
    }

    @Override
    protected Instance getFallback() {
        logger.info("Can not get Instance by serviceId {}", serviceId);
        return new Instance("error", "error", 0);
    }
}
