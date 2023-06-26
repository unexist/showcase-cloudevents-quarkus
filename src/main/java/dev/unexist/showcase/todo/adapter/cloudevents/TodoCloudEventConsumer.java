/**
 * @package Quarkus-Messaging-Showcase
 *
 * @file Todo cloudevent consumer
 * @copyright 2020-present Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.todo.adapter.cloudevents;

import io.cloudevents.CloudEvent;
import io.cloudevents.kafka.CloudEventDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

@ApplicationScoped
public class TodoCloudEventConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TodoCloudEventConsumer.class);
    private static final int POLL_IN_MILLIS = 100;

    KafkaConsumer<String, CloudEvent> consumer;

    TodoCloudEventConsumer() {
        Properties props = new Properties();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "todo-ce-consumer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, CloudEventDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");

        this.consumer = new KafkaConsumer<>(props);

        consumer.subscribe(Collections.singletonList("todos-ce"));

        LOGGER.info("Cloudevent consumer started");
    }

    public void receive() {
        ConsumerRecords<String, CloudEvent> consumerRecords = consumer.poll(
                Duration.ofMillis(POLL_IN_MILLIS));
        consumerRecords.forEach(record -> {
            LOGGER.info("Record key: {}", record.key());
            LOGGER.info("Record value: {}", record.value().toString());
            LOGGER.info("Record partition: {}", record.partition());
            LOGGER.info("Record offset: {}", record.offset());
        });
    }
}
