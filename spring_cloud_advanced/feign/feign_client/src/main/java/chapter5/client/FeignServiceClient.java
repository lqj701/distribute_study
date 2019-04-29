package chapter5.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import chapter5.dto.Instance;

@FeignClient("feign-service")
public interface FeignServiceClient {
    @RequestMapping(value = "/instance/{serviceId}", method = RequestMethod.GET)
    Instance getInstanceByServiceid(@PathVariable("serviceId") String serviceId);

    @RequestMapping(value = "/intance/{serviceId}", method = RequestMethod.DELETE)
    String deleteIntanceByServiceId(@PathVariable("serviceId") String serviceId);

    @RequestMapping(value = "/instance", method = RequestMethod.POST)
    String createInstance(@RequestBody Instance instance);

    @RequestMapping(value = "/instance/{serviceId}", method = RequestMethod.PUT)
    String updateInstanceByServiceId(@RequestBody Instance instance, @PathVariable("serviceId") String serviceId);

}
