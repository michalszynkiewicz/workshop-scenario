# Kindergarten

# TODO:
- prettier formatting for API
- get rid of api prefixes


## Kids service
A CRUD application that stores children data.
Data stored in memory.

### API

#### `GET /kids`

Response: status code `200`
```
[
{
  "id": 1,
  "firstname": "Janek",
  "lastname": "Kowalski"
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
  ...

},
...
]
```


#### `POST /kids`

Request:
```
{
  "firstname": "Janek",
  "lastname": "Kowalski"
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
```
{
  "id": 1,
  "firstname": "Janek",
  "lastname": "Kowalski"
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

## Presence/absence service

A service that stores info on whether a child was present or absent on a given day.

### API

#### `POST /absences`
Request:
```
{
  "childId": 1,
  "date": "2019-01-31"
}
```
Response: status code `201`

#### `POST /absences/kid/{kidId}`
Response:
```
[{
  "childId": 1,
  "date": "2019-01-31"
},{
  "childId": 1,
  "date": "2019-01-11"
},
]
```
Response: status code `200`

#### `GET /api/report/{childId}?month=2019-05`

Response: status code `200`
```
{
  "childId": 1,
  "daysAbsent": 5
}
```


## Payments service
A service that calculates the amount to pay for the kindergarten and sends an email (via e-mail service) to parents.

See below for the email service.

The algorithm to calculate the tuition:
```
max(200, 500 - daysAbsent * 20)
```

### API:

#### POST /api/trigger

Triggers calculations and sending emails.
Request:
```
{
  "year": 2010,
  "month": 12
}
```

Response: status code `200`

## [Already provided] Email service

TODO: describe how it fails.

### API
`POST /emails`
Request:
```
{
  "address": "jan.kowalski@example.com",
  "subject": "Your kindergarten tuition for Janek",
  "content": "In the previous month, your child was absent for 14 days and your tuition is 220 PLN"
}
```

Response: status code: `201`
