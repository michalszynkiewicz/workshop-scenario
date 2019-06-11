package com.example;

import com.example.failures.FailureInflictor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/emails")
@ApplicationScoped
public class EmailResource {
   @Inject
   Broadcaster broadcaster;

   @Inject
   FailureInflictor inflictor;

   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   public Response sendEmail(Email email) {
      mimicWork();
      if (inflictor.shouldFail()) {
         return Response.serverError().build();
      }
      broadcaster.addEmail(email);
      return Response.status(201).build();
   }

   private void mimicWork() {
      try {
         Thread.sleep(500L);
      } catch (InterruptedException ignored) {
      }
   }
}