package com.example.failures;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 05/06/2019
 */
@Path("/application-state")
public class StatusEndpoint {
   @Inject
   FailureInflictor triggerer;

   @GET
   @Produces("application/json")
   public Status getStatus() {
      return triggerer.getStatus();
   }

   @POST
   public void setState(Status status) {
      triggerer.setStatus(status);
   }
}
