package chapter6.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import chapter5.dto.Instance;

@FeignClient(value = "feign-service", fallback = InstanceClientFallBack.class)
public interface InstanceClient {
    @RequestMapping(value = "/feign-service/instance/{serviceId}", method = RequestMethod.GET)
    Instance getInstanceByServiceId(@PathVariable("serviceId") String serviceId);
}
