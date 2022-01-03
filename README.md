# Ticket booking app
### The goal is to build a seat reservation system for a multiplex.
## Business scenario (use case)
### 1. The user selects the day and the time when he/she would like to see the movie.
For this functionality the GET request is used. User selects two dates with time of a day between (regardless of their order) he/she would like to see the movies in following format:\
/timeinterval/firstdate/YYYY/MM/DD/hh/mm/lastdate/YYYY/MM/DD/hh/mm\
The GET request is linked to a proper [controller](/app/controllers/HomeController.scala) in the [routes](/conf/routes) file.
### 2. The system lists movies available in the given time interval - title and screening times.
The [controller](/app/controllers/HomeController.scala) mentioned in the previous section runs the function "timeInterval" which filters all of the screenings (localized in mutable Map in [Memory.scala](/app/memory/Memory.scala) file) and leaves only these that are between two selected dates.\
The response is given in HTML format, and the screenigs are grouped by name of the movie and recursively subgrouped by year, month, day of the month, hour, until they reach minutes parameter (which contains hyperlink to a particular screening). For this functionality [screening.scala.html](/app/views/screenings.scala.html) view is used.
### 3. The user chooses a particular screening.
The user can choose between every screening by clicking the hyperlink in the minutes parameter. (In the response described in the previous section)
### 4. The system gives information regarding screening room and available seats.
This functionality uses GET request (linked in [routes](/conf/routes)) in the following format:
/reserveScreening/id/\
Where id is id of the particular screening. All of the screenings are stored in mutable Map, and each have a special numerical id.\
The view of screening and available seats the system uses [reservescreening.scala.html](/app/views/reservescreening.scala.html). This view contain the matrix of seats - green are available, and red are reserved. As default each room has 10x10 seats matrix. Each seat is identified with 2 characters - first is letter of latin alphabet - which gives the information about the row, and an arabic numeral which gives the information regarding the location of the seat in the row.
### 5. The user chooses seats, and gives the name of the person doing the reservation (name and surname).
The choosing of the seats is realised via GET request (linked in [routes](/conf/routes)) in the following format:\
/reserveSeat/screening/row/seat\
Where screening is id of chosen screening, row is id of chosen row, seat is id of a chosen seat in a row. After the user chooses a seat the system checks if his order exists in the memory, if it doesn't the system creates new order with special id which will be stored in cookie file of the user. If order already exists the system ads the seat to the user's order. This functionality uses 'reserveSeat' method stored in [controller](/app/controllers/HomeController.scala).\
After the user chosen all of the seats he/she had wanted the user can go to 'finalizeOrder' ([screeningFinalization.scala.html](/app/views/screeningFinalization.scala.html) contains the view of this page) page which will give him/her the information about all of the seats he/she chosen and will ask him about what types of tickets does he/she want to buy. The user will also be asked fot his/her name and surname.\
### 6. The system gives back the total amount to pay and reservation expiration time.
This functionality uses POST request (linked in [routes](/conf/routes)) in the following format:\
/ultimateFinalization\
The POST rewquest must contain fields "lname" and "fname" with information about name and surname of a person that finalizes the order and information about type of ticket for each of chosen seats.
## Assumptions
### 1. The system covers a single cinema with multiple rooms (multiplex).
Each room object is stored in 'rooms' mutable Map.
### 2. Seats can be booked at latest 15 minutes before the screening begins.
The user can only see the screenings after 15 minutes from the current time. The seats can be only at least 15 minutes before the screening. However it is possible to finalize the order even after 15 minutes advance.
### 3. Screenings given in point 2. of the scenario should be sorted by title and screening time.
How it was [previously](#2.-The-system-lists-movies-available-in-the-given-time-interval---title-and-screening-times.) describe GET request for screenings in time interval gives response in HTML format where all filtered screeenings not only grouped but also sorted (by the title in the first order and then by each meaningful parameter of the date.)

### 4. There are three ticket types: adult (25 PLN), student (18 PLN), child (12.50 PLN).
Each of ticket prices described above has its own case object of the TicketPrice class. All of described abstractions are stored in [TicketPrice.scala](/app/models/TicketPrice.scala)
## Business requirements
### 1. The data in the system should be valid, in particular:
#### a. name and surname should each be at least three characters long, starting with a capital letter. The surname could consist of two parts separated with a single dash, in this case the second part should also start with a capital letter.
For checking if the name and surname are valid the two Regexes and functions are used in file [Order](/app/models/Order.scala):
```scala
  private val fname: Regex = """\p{Lu}\p{L}{2,}""".r
  private val lname: Regex = """\p{Lu}\p{L}{2,}([-]\p{Lu}\p{L}{2,})?""".r

  private val fnameFunction: String => OperationStatus[String] = {
    case fname@fname() => Success(fname)
    case fname => Failure(fname)
  }
  private val lnameFunction: String => OperationStatus[String] = {
    case lname@lname(_) => Success(lname)
    case lname => Failure(lname)
  }
```
#### b. reservation applies to at least one seat.
In the finalization of the order the system checks if there are any seats chosen. If there are none the order cannot be finalized and the system responds with error message.
### 2. There cannot be a single place left over in a row between two already reserved places.
The system checks this condition for each screening in the order with checkCondition(order: Order) method in [Screening.scala](/app/models/Screening.scala) which then maps checkCondition(order: Order) method for each seat row inside the room of screenig. checkConditio(order: Order) for seatRow class is localized inside [SeatRow.scala](/app/models/SeatRow.scala) file and gives result of class OperationStatus, with list of seats that should be reserved for order to be valid. It is coded as follows:
```scala
 def checkCondition(order: Order): OperationStatus[List[Seat]] = {
    seats
      .map(seat => (seat,seat.getStatus))
      .sliding(3,1)
      .toList
      .collect {
        case Array((_,Taken(_,_) | Reserved(`order`,_)),(seat,Available),(_,Taken(_,_) | Reserved(`order`,_))) => seat
      } match {
      case Nil => Success(Nil)
      case list => Failure(list)
    }
  }
```
### 3. The system should properly handle Polish characters.
The user's name and surname may contain Polish characters, as well as from other alphabets. The validation in the system will work just fine thanks to propper Regexes inside [Order](/app/models/Order.scala) file.
## Technical requirements
### 1. Application must be written in JVM language (Java, Scala, Kotlin etc.)
The application is written in Scala programming language and above that it's using Play Framework for handling the requests.
### 2. Operations must be exposed as REST services
Each of the user case operations are exposed as REST services in the [routes](/conf/routes) file.

### 3. No need to stick to any particular database - relational, NoSQL or in-memory database is fine
The system uses in-memory four main mutable maps for storing data. Mutable maps are defined inside [Memory.scala](/app/memory/Memory.scala) file and all of them have propper adding methods.

### 4. No need to build frontend
The system contains minimal HTML and CSS frontend, mostly for ease of testing (espacially if data is more graphical - for example the matrix of seats).

## Demo
### 1. Include shell script that will build and run your app.
The script is localized in [buildAndRun.sh](/shellScripts/linux/buildAndRun.sh). The number of used port can be set by changing content of [portNumber](/shellScripts/portNumber).

### 2. The system should be automatically initialized with test data (at least three screening rooms, three movies and two screenings per room).
Test data is initialized inside [ApplicationStart.scala](/app/start/ApplicationStart.scala) file.

### 3. Include shell script that would run whole use case calling respective endpoints (using e.g. curl), we want to see requests and responses in action.
The script is localized in [useCase.sh](/shellScripts/linux/useCase.sh). It uses the same [portNumber](/shellScripts/portNumber) file as [buildAndRun.sh](/shellScripts/linux/buildAndRun.sh).

## Before submitting…
### 1. Make sure your solution contains a README file, which explains how to build and run your project and demo.
The project contains [README](README.md) file. In order to run the application execute following command:
```console
cd shellScripts/linux; ./buildAndRun.sh
```
To run use case demo execute following command:
```console
cd shellScripts/linux; ./useCase.sh
```
### 2. If there are some additional assumptions you’ve made, put them in README as well.
The additional assumtion is that a person can finalized her/his order even after the fifteen minutes screening advance, but can reserve seats only before this time.
### 3. Prepare a single pull request containing whole source code (so that we can easily do a code review for you).

