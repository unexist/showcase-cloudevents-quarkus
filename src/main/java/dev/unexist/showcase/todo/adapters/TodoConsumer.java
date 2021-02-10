/**
 * @package Quarkus-Messaging-Showcase
 *
 * @file Todo consumer
 * @copyright 2020-2021 Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the GNU GPLv2.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.todo.adapters;

import io.cloudevents.CloudEvent;
import io.cloudevents.kafka.CloudEventDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import javax.enterprise.context.ApplicationScoped;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@ApplicationScoped
public class TodoConsumer {
    private KafkaConsumer<String, CloudEvent> consumer;

    TodoConsumer() {
        Properties props = new Properties();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "todo-cloudevents-consumer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, CloudEventDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");

        this.consumer = new KafkaConsumer<>(props);

        consumer.subscribe(Collections.singletonList("todos"));

        System.out.println("Consumer started");
    }

    public List<Map<String, String>> receive() {
        java.util.List<Map<String, String>> list = Collections.emptyList();

        ConsumerRecords<String, CloudEvent> consumerRecords = consumer.poll(Duration.ofMillis(100));
        consumerRecords.forEach(record -> {
            Map<String, String> recordEntry = new HashMap<>();

            recordEntry.put("Record Key", record.key());
            recordEntry.put("Record value", record.value().toString());
            recordEntry.put("Record partition",
                    String.valueOf(record.partition()));
            recordEntry.put("Record offset",
                    String.valueOf(record.offset()));

            list.add(recordEntry);
        });

        return list;
    }
}
