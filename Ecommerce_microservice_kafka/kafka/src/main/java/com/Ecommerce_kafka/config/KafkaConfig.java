package com.Ecommerce_kafka.config;

import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;

@Configurable
@EnableKafka
@EnableKafkaStreams
public class KafkaConfig {

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(KafkaProperties properties) {
        Map<String, Object> config = new HashMap<>(properties.buildProducerProperties());
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(config));
    }

    @Bean
    public KafkaStreamsConfiguration kafkaStreamsConfiguration(KafkaProperties properties) {
        Map<String, Object> config = new HashMap<>(properties.buildStreamsProperties());
        return new KafkaStreamsConfiguration(config);
    }
}
