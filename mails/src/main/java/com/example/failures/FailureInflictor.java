package com.example.failures;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 06/06/2019
 */
@ApplicationScoped
public class FailureInflictor {
   private static final Logger log = LoggerFactory.getLogger(FailureInflictor.class);
   private AtomicInteger failureOpportunities = new AtomicInteger(0);
   private Status status = Status.FIFTY_FIFTY;

   public Status getStatus() {
      return status;
   }

   public void setStatus(Status status) {
      log.info("Setting status to: {}", status);
      failureOpportunities.set(0);
      this.status = status;
   }

   public boolean shouldFail() {
      switch (status) {
         case WORKING:
            return false;
         case FIFTY_FIFTY:
            if (failureOpportunities.incrementAndGet() % 2 == 0) {
               return false;
            }
      }
      return true;
   }
}
