package com.example;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;

/**
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@example.com
 * <br>
 * Date: 10/06/2019
 */
public class Feeder {
   private static final Random random = new Random();

   private static final String kidsUri = System.getProperty("kids.uri", "http://localhost:8081");
   private static final String absenceUri = System.getProperty("absence.uri", "http://localhost:8082");

   private static final KidsClient kidsClient = buildClient(KidsClient.class, kidsUri);
   private static final AbsenceClient absenceClient = buildClient(AbsenceClient.class, absenceUri);

   public static void main(String[] args) {
      Collection<Integer> kidIds = createKids();
      registerAbsences(kidIds);
      System.out.println("Bootstrapping finished.");
   }

   private static <T> T buildClient(Class<T> restInterface, String uri) {
      ResteasyClientBuilder clientBuilder = (ResteasyClientBuilder) ResteasyClientBuilder.newBuilder();
      return clientBuilder.build()
            .target(uri)
            .proxyBuilder(restInterface)
            .build();
   }

   private static void registerAbsence(int year, int month, int day, Integer kidId) {
      AbsenceClient.Absence absence = new AbsenceClient.Absence();
      absence.date = String.format("%d-%02d-%02d", year, month, day);
      absence.kidId = kidId;
      absenceClient.addAbsence(absence);
   }

   private static List<Integer> createKids() {
      List<KidsClient.Kid> allKids = kidsClient.getAllKids();

      List<Integer> kidIds = allKids.stream().map(k -> k.id).collect(Collectors.toList());

      addKid(allKids,
            kidIds,
            "Janek", "Kowalski",
            "Jan", "Kowalski", "jkowalski@example.com",
            "Janina", "Kowalska", "jkowalska@example.com");
      addKid(allKids,
            kidIds,
            "Karol", "Nowak",
            "Piotr", "Nowak", "pnowak@example.com",
            "Karolina", "Nowak", "knowak@example.com");
      addKid(allKids,
            kidIds,
            "Maciek", "Malinowski",
            "Marek", "Malinowski", "mmalinowski@example.com",
            "Magdalena", "Malinowska", "mmalinowska@example.com");

      return kidIds;
   }

   private static void addKid(List<KidsClient.Kid> allKids,
                              List<Integer> kidIds,
                              String name,
                              String lastName,
                              String dadsName,
                              String dadsLastName,
                              String dadsEmail,
                              String momsName,
                              String momsLastName,
                              String momsEmail) {
      if (allKids.stream().anyMatch(k -> name.equals(k.firstname) && lastName.equals(k.lastname))) {
         System.out.println(String.format("Skipping adding %s %s. Already exists.", name, lastName));
         return;
      }
      KidsClient.Kid kid = new KidsClient.Kid();
      kid.firstname = name;
      kid.lastname = lastName;

      KidsClient.Parent mom = new KidsClient.Parent();
      mom.firstname = momsName;
      mom.lastname = momsLastName;
      mom.email = momsEmail;
      KidsClient.Parent dad = new KidsClient.Parent();
      dad.firstname = dadsName;
      dad.lastname = dadsLastName;
      dad.email = dadsEmail;

      kid.parents = asList(mom, dad);

      KidsClient.Kid result = kidsClient.addKid(kid);
      kidIds.add(result.id);
   }

   private static void registerAbsences(Collection<Integer> kidIds) {
      kidIds.forEach(Feeder::registerAbsences);
   }

   private static void registerAbsences(Integer kidId) {
      List<AbsenceClient.Absence> kidsAbsences = absenceClient.getAbsences(kidId);
      if (kidsAbsences.isEmpty()) {
         IntStream.of(2018, 2019, 2020)
               .forEach(
                     year -> IntStream.range(1, 13)
                           .forEach(
                                 month -> registerAbsences(year, month, kidId)
                           )
               );
      } else {
         System.out.println("found absences for : " + kidId + ", skipping generating them.");
      }
   }

   private static void registerAbsences(int year, int month, Integer kidId) {
      IntStream.range(1, 29)
            .filter(n -> random.nextDouble() < 0.2)
            .forEach(day -> registerAbsence(year, month, day, kidId));
   }
}
