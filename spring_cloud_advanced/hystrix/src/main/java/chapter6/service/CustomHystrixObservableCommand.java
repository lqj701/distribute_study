package chapter6.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixObservableCommand;

import chapter5.dto.Instance;
import rx.Observable;

public class CustomHystrixObservableCommand extends HystrixObservableCommand<Instance> {
    private static Logger logger = LoggerFactory.getLogger(CustomHystrixObservableCommand.class);
    private RestTemplate restTemplate;
    private String serviceId;

    protected CustomHystrixObservableCommand(RestTemplate restTemplate, String serviceId) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("CustomServiceGroup")));

        this.restTemplate = restTemplate;
        this.serviceId = serviceId;
    }

    @Override
    protected Observable<Instance> construct() {
        return Observable.create(subscriber -> {
            if (!subscriber.isUnsubscribed()) {
                subscriber.onNext(restTemplate.getForEntity("http://FEIGN-SERVICE/feign-service/instance/{serviceId}",
                        Instance.class, serviceId).getBody());
                subscriber.onCompleted();
            }
        });
    }

    @Override
    protected Observable<Instance> resumeWithFallback() {
        logger.info("Can not get Instance by serviceId {}", serviceId);
        return Observable.create(subscriber -> {
            if (!subscriber.isUnsubscribed()) {
                subscriber.onNext(new Instance("error", "error", 0));
                subscriber.onCompleted();
            }
        });
    }
}
