package com.ebi.assessment.person;

import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        return new HazelcastCacheManager(hazelcastInstance());
    }

    private HazelcastInstance hazelcastInstance() {
        return Hazelcast.newHazelcastInstance(config());
    }

    private Config config() {
        final MapConfig mapConfig = new MapConfig("person");
        mapConfig.setMaxIdleSeconds(300);
        final EvictionConfig evictionConfig = new EvictionConfig();
        evictionConfig.setEvictionPolicy(EvictionPolicy.LRU);
        evictionConfig.setMaxSizePolicy(MaxSizePolicy.USED_HEAP_SIZE);
        evictionConfig.setSize(100);
        mapConfig.setEvictionConfig(evictionConfig);

        final Config config = new Config("person");
        config.addMapConfig(mapConfig);
        config.setProperty("hazelcast.heartbeat.interval.seconds", "5");
        config.setProperty("hazelcast.max.no.heartbeat.seconds", "120");

        return config;
    }
}
