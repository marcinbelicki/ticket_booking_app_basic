# Ticket booking app
### The goal is to build a seat reservation system for a multiplex.
## Business scenario (use case)
### 1. The user selects the day and the time when he/she would like to see the movie.
For this functionality the GET request is used. User selects two dates with time of a day between (regardless of their order) he/she would like to see the movies in following format:\
/timeinterval/firstdate/YYYY/MM/DD/hh/mm/lastdate/YYYY/MM/DD/hh/mm\
The GET request is linked to a proper [controller](/app/controllers/HomeController.scala) in the [routes](/conf/routes) file.
### 2. The system lists movies available in the given time interval - title and screening times.
The controller mentioned in the previous section runs the function "timeInterval
### 3. The user chooses a particular screening.
### 4. The system gives information regarding screening room and available seats.
### 5. The user chooses seats, and gives the name of the person doing the reservation (name and surname).
### 6. The system gives back the total amount to pay and reservation expiration time.
## Assumptions
### 1. The system covers a single cinema with multiple rooms (multiplex).
### 2. Seats can be booked at latest 15 minutes before the screening begins.
### 3. Screenings given in point 2. of the scenario should be sorted by title and screening time.
### 4. There are three ticket types: adult (25 PLN), student (18 PLN), child (12.50 PLN).
## Business requirements
### 1. The data in the system should be valid, in particular:
#### a. name and surname should each be at least three characters long, starting with a capital letter. The surname could consist of two parts separated with a single dash, in this case the second part should also start with a capital letter.
#### b. reservation applies to at least one seat.
### 2. There cannot be a single place left over in a row between two already reserved places.
### 3. The system should properly handle Polish characters.
## Technical requirements
### 1. Application must be written in JVM language (Java, Scala, Kotlin etc.)
### 2. Operations must be exposed as REST services
### 3. No need to stick to any particular database - relational, NoSQL or in-memory database is fine
### 4. No need to build frontend
## Demo
### 1. Include shell script that will build and run your app.
### 2. The system should be automatically initialized with test data (at least three screening rooms, three movies and two screenings per room).
### 3. Include shell script that would run whole use case calling respective endpoints (using e.g. curl), we want to see requests and responses in action.
## Before submitting…
### 1. Make sure your solution contains a README file, which explains how to build and run your project and demo.
### 2. If there are some additional assumptions you’ve made, put them in README as well.
### 3. Prepare a single pull request containing whole source code (so that we can easily do a code review for you).
