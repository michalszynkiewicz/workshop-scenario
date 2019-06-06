package com.example;

import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonWriter;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import java.io.Writer;
import java.util.List;

/**
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 05/06/2019
 */
@RegisterForReflection
public class JsonEncoder implements Encoder.TextStream<List<Email>> {
   @Override
   public void init(EndpointConfig config) {
   }

   @Override
   public void destroy() {
   }

   @Override
   public void encode(List<Email> emails, Writer writer) {
      try (JsonWriter jsonWriter = Json.createWriter(writer)) {
         JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

         emails.forEach(
               email -> arrayBuilder.add(email.toJsonObject())
         );

         jsonWriter.writeArray(arrayBuilder.build());
      }
   }
}
