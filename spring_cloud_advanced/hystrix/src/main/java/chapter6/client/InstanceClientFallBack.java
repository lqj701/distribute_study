package chapter6.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import chapter5.dto.Instance;

@Component
public class InstanceClientFallBack implements InstanceClient {
    private static Logger logger = LoggerFactory.getLogger(InstanceClientFallBack.class);


    @Override
    public Instance getInstanceByServiceId(String serviceId) {
        logger.info("Can not get Instance by serviceId {}", serviceId);
        return new Instance("error", "error", 0);
    }
}
