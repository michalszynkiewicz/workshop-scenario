package com.example.clients;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.List;

/**
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 10/06/2019
 */
@Path("/absences")
@Produces("application/json")
@Consumes("application/json")
public interface AbsenceClient {

   @GET
   @Path("/{kidId}")
   List<Absence> getAbsences(@PathParam("kidId") Integer kidId);

   @POST
   void addAbsence(Absence absence);

   class Absence {
      public int kidId;
      public String date;
   }
}
