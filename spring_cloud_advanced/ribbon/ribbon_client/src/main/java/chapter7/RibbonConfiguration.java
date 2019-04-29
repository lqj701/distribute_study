package chapter7;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;

public class RibbonConfiguration {
    @Autowired
    IClientConfig iClientConfig;

    @Bean
    public IRule ribbonRule() {
        return new RandomRule();
    }
}
