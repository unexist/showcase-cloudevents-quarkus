/**
 * @package Quarkus-Messaging-Showcase
 *
 * @file Todo resource
 * @copyright 2020-2021 Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the GNU GPLv2.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.todo.adapters;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/message")
public class MessageResource {

    @Inject
    TodoProducer todoProducer;

    @Inject
    TodoConsumer todoConsumer;

    @POST
    @Operation(summary = "Send cloudevent via kafka")
    @Tag(name = "Message")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Nothing found"),
            @APIResponse(responseCode = "500", description = "Server error")
    })
    public Response sendCloudEvent() {
        this.todoProducer.send();

        return Response.noContent().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Receive cloudevent via kafka")
    @Tag(name = "Message")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Nothing found"),
            @APIResponse(responseCode = "500", description = "Server error")
    })
    public Response receiveCloudEvent() {
        Response.ResponseBuilder builder = Response.noContent();

        List<Map<String, String>> list = this.todoConsumer.receive();

        if (!list.isEmpty()) {
            builder = Response.ok(list);
        }

        return builder.build();
    }
}
