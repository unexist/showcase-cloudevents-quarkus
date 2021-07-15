/**
 * @package Quarkus-Messaging-Showcase
 *
 * @file Todo producer
 * @copyright 2020 Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.todo.adapter.smallrye;

import io.smallrye.mutiny.Multi;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TodoSmallryeProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TodoSmallryeConsumer.class);

    @Outgoing("todo-rye-out")
    public Multi<String> send() {
        return Multi.createFrom().item("Todo");
    }
}
