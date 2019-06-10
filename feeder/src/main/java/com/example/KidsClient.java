package com.example;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

/**
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 10/06/2019
 */
@Produces("application/json")
@Consumes("application/json")
@Path("/kids")
public interface KidsClient {
   @GET
   List<Kid> getAllKids();
   @POST
   Kid addKid(Kid kid);

   class Kid {
      public int id;
      public String firstname;
      public String lastname;
      public List<Parent> parents;
   }
   class Parent {
      public String firstname;
      public String lastname;
      public String email;
   }
}
