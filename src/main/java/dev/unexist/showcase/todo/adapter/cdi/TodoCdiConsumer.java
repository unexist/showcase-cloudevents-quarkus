/**
 * @package Quarkus-Messaging-Showcase
 *
 * @file Todo cdi consumer
 * @copyright 2020 Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the GNU GPLv3.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.todo.adapter.cdi;

import org.aerogear.kafka.cdi.annotation.Consumer;
import org.aerogear.kafka.cdi.annotation.KafkaConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@KafkaConfig(bootstrapServers = "localhost:9092")
public class TodoCdiConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TodoCdiConsumer.class);

    @Consumer(topics = "topic-cdi", groupId = "todo-cdi-consumer")
    public void onMessage(final String key, final String value) {
        LOGGER.info("Key: {}", key);
        LOGGER.info("Value: {}", value);
    }
}