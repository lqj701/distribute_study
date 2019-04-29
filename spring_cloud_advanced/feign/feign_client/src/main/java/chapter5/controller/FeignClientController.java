package chapter5.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import chapter5.client.FeignServiceClient;
import chapter5.dto.Instance;

@RestController
@RequestMapping("/feign-client")
public class FeignClientController {
    @Autowired
    FeignServiceClient feignServiceClient;

    @RequestMapping(value = "/instance/{serviceId}", method = RequestMethod.GET)
    public Instance getInstanceByServiceId(@PathVariable("serviceId") String serviceId) {
        return feignServiceClient.getInstanceByServiceid(serviceId);
    }

    @RequestMapping(value = "/instance/{serviceId}", method = RequestMethod.DELETE)
    public String deleteInstanceByServiceId(@PathVariable("serviceId") String serviceId) {
        return feignServiceClient.deleteIntanceByServiceId(serviceId);
    }

    @RequestMapping(value = "/instance", method = RequestMethod.POST)
    public String createInstance(@RequestBody Instance instance) {
        return feignServiceClient.createInstance(instance);
    }

    public String updateInstanceByServiceId(@RequestBody Instance instance,
            @PathVariable("serviceId") String serviceId) {
        return feignServiceClient.updateInstanceByServiceId(instance, serviceId);
    }

}
