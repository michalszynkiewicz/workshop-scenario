# Kindergarten system

The task is to implement a system for kindergarten tuition calculation.

The system will comprise of:
- kids service for storing children data
- absence service for storing when a child was absent
- tuition service for calculating the tuition for a given month
- email service for sending email notifications.

The tuition service will get a list of kids from kids service.
For each kid, it will get a list of their absences from absence service, 
prepare a message with the tuition amount and send it to the kid's parents
using the email service.

The email service is already provided, you can find it in the `mails` directory in this repository.

To query the rest services, you can use `curl` or a browser extension, such as Advanced REST client for Chrome.


![Kindergarten system](kindergarten-system.png)

## 1. Implement the system
All the services store their data in memory.

Use:
- JAX-RS to expose REST services, 
- [MicroProfile Rest Client](https://download.eclipse.org/microprofile/microprofile-rest-client-1.3/microprofile-rest-client-1.3.html#_sample_builder_usage) to access REST resources,
- `application.properties` and `@ConfigProperty` for configuration
- `quarkus.http.port` property (in `application.properties`) to specify a non-default port for a service
    - helpful to run the services together locally
    
Important commands:
- generating a project 
```
mvn io.quarkus:quarkus-maven-plugin:999-SNAPSHOT:create
```
- listing available extensions: 
```
mvn quarkus:list-extensions
```
- adding extensions: 
```
mvn quarkus:add-extension -Dextensions=<comma-separated list of extensions>
```
- starting in the dev mode:
```
mvn clean compile quarkus:dev
```
- building native binary:
```
mvn clean package -Pnative
```
 

### Kids service
A CRUD application that stores children data.

For the local run, expose it on port `8081`.

#### API

##### `GET /kids`

Response: status code `200`
```JSON
[
{
  "id": 1,
  "firstname": "Janek",
  "lastname": "Kowalski",
  "parents": [
    {
      "firstname": "Jan",
      "lastname": "Kowalski",
      "email": "jan.kowalski@example.com"
    },
    {
      "firstname": "Janina",
      "lastname": "Kowalska",
      "email": "janina.kowalska@example.com"
    }
  ]
},
{
  "id": 2,
  "firstname": "..."
}
]
```


##### `POST /kids`

Request:
```JSON
{
  "firstname": "Janek",
  "lastname": "Kowalski",
  "parents": [
    {
      "firstname": "Jan",
      "lastname": "Kowalski",
      "email": "jan.kowalski@example.com"
    },
    {
      "firstname": "Janina",
      "lastname": "Kowalska",
      "email": "janina.kowalska@example.com"
    }
  ]
}
```
Response: status code 201
```JSON
{
  "id": 1,
  "firstname": "Janek",
  "lastname": "Kowalski",
  "parents": [
    {
      "firstname": "Jan",
      "lastname": "Kowalski",
      "email": "jan.kowalski@example.com"
    },
    {
      "firstname": "Janina",
      "lastname": "Kowalska",
      "email": "janina.kowalska@example.com"
    }
  ]
}
```

### Presence/absence service

A service that stores kids' absences.
It stores and exposes single absences but also provides a monthly report of absences for a child. 

For the local run, expose it on port `8082`

#### API

##### `POST /absences`
Request:
```JSON
{
  "kidId": 1,
  "date": "2019-01-31"
}
```
Response: status code `201`

##### `GET /absences/{kidId}` 
Response:
```JSON
[{
  "kidId": 1,
  "date": "2019-01-31"
},{
  "kidId": 1,
  "date": "2019-01-11"
}
]
```
Response: status code `200`

##### `GET /report/{kidId}?month=2019-05`

Response: status code `200`
```JSON
{
  "kidId": 1,
  "daysAbsent": 5
}
```


### Tuition service
For each child, calculates the amount of money to pay for the kindergarten.
Then sends an email (via e-mail service) to parents.

Please note that the email service is not reliable.
Use [MicroProfile Fault Tolerance's `@Retry`](https://download.eclipse.org/microprofile/microprofile-fault-tolerance-2.0/microprofile-fault-tolerance-spec.html#_retry_usage)
to make sure the message is sent. 

The formula to calculate the tuition:
```
max(200, 500 - daysAbsent * 20)
```

#### API:

##### POST /trigger

Triggers calculations and sending emails.
Request:
```JSON
{
  "year": 2019,
  "month": 12
}
```

Response: status code `200`

### [Already provided] Email service

To run the email service, enter the `mails` directory, build the project
and run:
```
java -jar target/mails-1.0-SNAPSHOT.jar
```

It will expose a web page listing all the messages sent and an API described 
below at http://localhost:8084

#### API
`POST /emails`
Request:
```JSON
{
  "address": "jan.kowalski@example.com",
  "subject": "Your kindergarten tuition for Janek",
  "content": "In the previous month, your child was absent for 14 days and your tuition is 220 PLN"
}
```

Response: status code: `201`

## 2. Populate the system
This repository contains a `feeder` application that lets you easily feed the system with some data.

To run it, either run the `Feeder` class in an IDE or build the app with `mvn clean package` and run with:
```
java -jar target/feeder.jar
```
You can also run the `com.example.Feeder` app from the `feeder` directory
to add some data to the kids and absence services.

`feeder` assumes the applications are running locally on the suggested ports.
If you used different ports or want to feed applications running in minikube, 
make sure to set `kids.uri` and `absence.uri` system properties to 
appropriate URLs, e.g.:
```
java -Dkids.uri=http://localhost:8181 -Dabsence.uri=http://localhost:8080 -jar target.jar
``` 

## 3. Trigger the calculations and watch the results
Open your browser on the email service. By default it is exposed at http://localhost:8084

Trigger the calculations, e.g.:
```
curl localhost:8080/trigger -d '{"year": 2019, "month": 12}'
```

If the calculations finished successfully, the mail service page 
should show the emails with tuition amounts sent to parents.

## 4. Deploy the services to minikube
[Instructions for installing minikube, building Docker images and using kubectl](https://github.com/michalszynkiewicz/simple-kubernetes-cheat-sheet)

Install minikube and start it.

Following the instructions linked above, build docker images for the four applications
and push them to minikube's docker registry.

Create deployments for each of the applications.

Create kubernetes services for each of the applications. 
Use `NodePort` to be able to access the from outside of the cluster. 

Assuming your services are called `kids`, `absence` and `mails` and are exposing 
the suggested ports, create a `ConfigMap` as follows:

```yaml
kind: ConfigMap
apiVersion: v1
metadata:
  name: kindergarten-config
data:
  kids.url: 'http://kids:8081'
  absence.url: 'http://absence:8082'
  mails.url: 'http://mails:8084'
```

And add propagate the tuition's environment from it:
```
kubectl set env deployment tuition --from=configmap/kindergarten-config
```

Execute `Feeder` with urls of the services.

Trigger the calculations again, this time in the Kubernetes version, and observe 
the web page of the kubernetes version of mail service.


