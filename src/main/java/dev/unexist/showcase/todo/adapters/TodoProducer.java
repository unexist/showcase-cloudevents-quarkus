/**
 * @package Quarkus-Messaging-Showcase
 *
 * @file Todo producer
 * @copyright 2020-2021 Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the GNU GPLv2.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.todo.adapters;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.message.Encoding;
import io.cloudevents.core.v1.CloudEventBuilder;
import io.cloudevents.jackson.JsonFormat;
import io.cloudevents.kafka.CloudEventSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import javax.enterprise.context.ApplicationScoped;
import java.net.URI;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@ApplicationScoped
public class TodoProducer {
    private KafkaProducer<String, CloudEvent> producer;
    private CloudEventBuilder eventBuilder;

    TodoProducer() {
        Properties props = new Properties();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "todo-cloudevents-producer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, CloudEventSerializer.class);
        props.put(CloudEventSerializer.ENCODING_CONFIG, Encoding.STRUCTURED);
        props.put(CloudEventSerializer.EVENT_FORMAT_CONFIG, JsonFormat.CONTENT_TYPE);

        this.producer = new KafkaProducer<>(props);

        this.eventBuilder = io.cloudevents.core.builder.CloudEventBuilder.v1()
                .withSource(URI.create("https://unexist.dev"))
                .withType("todo");
    }

    public void send() {
        try {
            String id = UUID.randomUUID().toString();
            String data = "Todo event";

            CloudEvent event = this.eventBuilder.newBuilder()
                    .withId(id)
                    .withData("text/plain", data.getBytes())
                    .build();

            RecordMetadata metadata = this.producer
                    .send(new ProducerRecord<>("todos", id, event))
                    .get();

            System.out.println("Record sent to partition " + metadata.partition() +
                    " with offset " + metadata.offset());
        } catch (InterruptedException|ExecutionException e) {
            System.out.println("Error while trying to send the record");
            e.printStackTrace();
        }

        this.producer.flush();
    }
}
