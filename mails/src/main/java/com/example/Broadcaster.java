package com.example;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.SendResult;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;

/**
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 05/06/2019
 */
@ServerEndpoint(value = "/emails", encoders = JsonEncoder.class)
@ApplicationScoped
public class Broadcaster {

   private final List<Email> emails = Collections.synchronizedList(new ArrayList<>());
   private final List<Session> sessions = Collections.synchronizedList(new ArrayList<>());

   @OnOpen
   public void onOpen(Session session) {
      sessions.add(session);
      session.getAsyncRemote().sendObject(emails);
      System.out.println("client connected, the number of clients: " + sessions.size());
   }

   @OnClose
   public void onClose(Session session) {
      sessions.remove(session);
      System.out.println("client closed, the number of clients left: " + sessions.size());
   }

   @OnError
   public void onError(Session session, Throwable ignored) {
      sessions.remove(session);
      System.out.println("client errored, the number of clients left: " + sessions.size());
      ignored.printStackTrace();
   }

   private void handleFailure(SendResult result) {
      if (result.getException() != null) {
         System.out.println("Unable to send message: " + result.getException());
      }
   }

   public void addEmail(Email email) {
      this.emails.add(email);
      sessions.forEach(s -> {
         s.getAsyncRemote().sendObject(singletonList(email), this::handleFailure);
      });
   }
}
