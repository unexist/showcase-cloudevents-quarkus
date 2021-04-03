/**
 * @package Quarkus-Messaging-Showcase
 *
 * @file Todo resource
 * @copyright 2020 Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the GNU GPLv3.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.todo.adapters;

import dev.unexist.showcase.todo.adapters.cdi.TodoCdiConsumer;
import dev.unexist.showcase.todo.adapters.cloudevents.TodoCloudEventConsumer;
import dev.unexist.showcase.todo.adapters.cloudevents.TodoCloudEventProducer;
import dev.unexist.showcase.todo.adapters.smallrye.TodoSmallryeConsumer;
import dev.unexist.showcase.todo.adapters.smallrye.TodoSmallryeProducer;
import org.aerogear.kafka.SimpleKafkaProducer;
import org.aerogear.kafka.cdi.annotation.Producer;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/message")
public class MessageResource {

    /* Cdi */
    @Producer
    SimpleKafkaProducer<String, String> todoCdiProducer;

    @Inject
    TodoCdiConsumer todoCdiConsumer;

    /* Cloudevents */
    @Inject
    TodoCloudEventProducer todoCloudEventProducer;

    @Inject
    TodoCloudEventConsumer todoCloudEventConsumer;

    /* Smallrye */
    @Inject
    TodoSmallryeProducer todoSmallryeProducer;

    @Inject
    TodoSmallryeConsumer todoSmallryeConsumer;

    @POST
    @Operation(summary = "Send cdi event via kafka")
    @Tag(name = "Message")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Nothing found"),
            @APIResponse(responseCode = "500", description = "Server error")
    })
    public Response sendCdiEvent() {
        this.todoCdiProducer.send("todos-cdi", "test", "test");

        return Response.noContent().build();
    }

    @POST
    @Operation(summary = "Send cloudevent via kafka")
    @Tag(name = "Message")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Nothing found"),
            @APIResponse(responseCode = "500", description = "Server error")
    })
    public Response sendCloudEvent() {
        this.todoCloudEventProducer.send();

        return Response.noContent().build();
    }

    @POST
    @Operation(summary = "Send smallrye via kafka")
    @Tag(name = "Message")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Nothing found"),
            @APIResponse(responseCode = "500", description = "Server error")
    })
    public Response sendSmallryeEvent() {
        this.todoSmallryeProducer.send();

        return Response.noContent().build();
    }
}
