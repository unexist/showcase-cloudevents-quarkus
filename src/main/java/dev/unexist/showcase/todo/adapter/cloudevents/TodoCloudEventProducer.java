/**
 * @package Quarkus-Messaging-Showcase
 *
 * @file Todo cloudevent producer
 * @copyright 2020 Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.todo.adapter.cloudevents;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.message.Encoding;
import io.cloudevents.jackson.JsonFormat;
import io.cloudevents.kafka.CloudEventSerializer;
import io.cloudevents.v03.CloudEventBuilder;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.net.URI;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@ApplicationScoped
public class TodoCloudEventProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TodoCloudEventConsumer.class);

    private KafkaProducer<String, CloudEvent> producer;
    private CloudEventBuilder eventBuilder;

    TodoCloudEventProducer() {
        Properties props = new Properties();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "todo-cloudevents-producer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, CloudEventSerializer.class);
        props.put(CloudEventSerializer.ENCODING_CONFIG, Encoding.STRUCTURED);
        props.put(CloudEventSerializer.EVENT_FORMAT_CONFIG, JsonFormat.CONTENT_TYPE);

        this.producer = new KafkaProducer<>(props);

        this.eventBuilder = CloudEventBuilder.builder()
                .withSource(URI.create("https://unexist.dev"))
                .withType("todo");
    }

    public void send() {
        try {
            String id = UUID.randomUUID().toString();
            String data = "Todo event";

            CloudEvent event = this.eventBuilder
                    .withId(id)
                    .withData(data)
                    .build();

            RecordMetadata metadata = this.producer
                    .send(new ProducerRecord<>("todos-ce", id, event))
                    .get();

            LOGGER.info("Record sent to partition {} with offset {}",
                    metadata.partition(), metadata.offset());
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Error while trying to send the record", e);
        }

        this.producer.flush();
    }
}
