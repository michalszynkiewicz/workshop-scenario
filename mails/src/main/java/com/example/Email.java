package com.example;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 05/06/2019
 */
public class Email {
   public String address;
   public String subject;
   public String content;

   public JsonObject toJsonObject() {
      return Json.createObjectBuilder()
            .add("address", address)
            .add("subject", subject)
            .add("content", content)
            .build();
   }
}
