package chapter6.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCollapser;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.annotation.ObservableExecutionMode;
import com.netflix.hystrix.contrib.javanica.command.AsyncResult;

import chapter5.dto.Instance;
import chapter6.client.InstanceClient;
import rx.Observable;
import rx.functions.Action1;

@Service
public class InstanceService {
    private static String DEFAULT_SERVICE_ID = "application";
    private static String DEFAULT_HOST = "localhost";
    private static int DEFAULT_PORT = 8080;

    private static Logger logger = LoggerFactory.getLogger(InstanceService.class);

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    InstanceClient instanceClient;

    @HystrixCommand(fallbackMethod = "instanceInfoGetFail", threadPoolProperties = {
            @HystrixProperty(name = "allowMaximunSizeToDivergeFromCoreSize", value = "true"),
            @HystrixProperty(name = "maximumSize", value = "20")})
    public Instance getInstanceByServiceIdWithRestTemplate(String serviceId) {
        Instance instance = restTemplate
                .getForEntity("http://FEIGN-SERVICE/feign-service/instance/{serviceId}", Instance.class, serviceId)
                .getBody();
        return instance;
    }

    @HystrixCommand(fallbackMethod = "instanceInfoGetFailObservable", observableExecutionMode = ObservableExecutionMode.LAZY)
    public Observable<Instance> getInstanceByServiceIdObservable(String serviceId) {
        return Observable.create(subscriber -> {
            if (!subscriber.isUnsubscribed()) {
                subscriber.onNext(restTemplate.getForEntity("http://FEIGN-SERVICE/feign-service/instance/{serviceId}",
                        Instance.class, serviceId).getBody());
                subscriber.onCompleted();
            }
        });
    }

    public Instance getInstanceByServiceIdCustom(String serviceId) {
        CustomHystrixCommand customHystrixCommand = new CustomHystrixCommand(restTemplate, serviceId);
        return customHystrixCommand.execute();
    }

    public Instance getInstanceByServiceIdCustomObservable(String serviceId) {
        CustomHystrixObservableCommand command1 = new CustomHystrixObservableCommand(restTemplate, serviceId);
        Observable<Instance> observable1 = command1.observe();
        Instance instance = observable1.toBlocking().single();

        CustomHystrixObservableCommand command2 = new CustomHystrixObservableCommand(restTemplate, serviceId);
        Observable<Instance> observable2 = command2.toObservable();

        observable2.subscribe(new Action1<Instance>() {
            @Override
            public void call(Instance instance) {
                System.out.println(instance);
            }
        });

        return instance;
    }

    public Observable<Instance> instanceInfoGetFailObservable(String serviceId) {
        logger.info("Can not get Instance by serviceId {}", serviceId);
        return Observable.create(subscriber -> {
            if (!subscriber.isUnsubscribed()) {
                subscriber.onNext(new Instance("error", "error", 0));
                subscriber.onCompleted();
            }
        });
    }


    @HystrixCommand(fallbackMethod = "instanceInfoGetFailAsync")
    public Future<Instance> getInstanceByServiceIdAsync(String serviceId) {
        logger.info("Can not get Instance by serviceId {}", serviceId);
        return new AsyncResult<Instance>() {
            @Override
            public Instance invoke() {
                return restTemplate.getForEntity("http://FEIGN-SERVICE/feign-service/instance/{serviceId}",
                        Instance.class, serviceId).getBody();
            }
        };
    }

    public Instance getInstanceByServiceIdWithFeign(String serviceId) {
        Instance instance = instanceClient.getInstanceByServiceId(serviceId);
        return instance;
    }


    @HystrixCommand
    public Future<Instance> instanceInfoGetFailAsync(String serviceId) {
        logger.info("Can not get Instance by serviceId {}", serviceId);
        return new AsyncResult<Instance>() {
            @Override
            public Instance invoke() {
                return new Instance("error", "error", 0);
            }
        };
    }

    @HystrixCollapser(batchMethod = "getInstanceByServiceIds", collapserProperties = {
            @HystrixProperty(name = "timerDelayInMilliseconds", value = "100")})
    // 如果示范代码没有生效，请缩短请求合并的周期，如缩短到10ms，视运行电脑性能而定
    public Future<Instance> getInstanceByServiceId(String serviceId) {
        return null;
    }

    @HystrixCommand
    public List<Instance> getInstanceByServiceIds(List<String> serviceIds) {
        List<Instance> instances = new ArrayList<>();
        logger.info("start batch!");
        for (String s : serviceIds) {
            instances.add(new Instance(s, DEFAULT_HOST, DEFAULT_PORT));
        }

        return instances;
    }

}
