# airplane_tickets

A microservice system whose functionality is the sale of airline tickets 
are composed of 3 services. Each service has its own function
in this microservices architecture. All services communicate via HTTP.
In this maven projet is used Spring Boot and Apache ActiveMQ. 
Implementation is enriched by Eureka and Zuul API gateway.
Service 1 or Registrator is an user microservice. It is in charge of
users and admins. 
Service 2 or FlightTickets is a microservice that is in charge of
flights and airplanes. It is supported to add and delete them. Also
it is upholed to search the specific flight. Essential functionality
is refunding money which implies use of message broker (all information
are passed on to service 1 and service 2).
Service 3 or BuyingTickets is a microservice required for selling tickets.
User can buy tickets paying by his credit card. The price of ticket depends
of the user's rank. Discount for golden rank is 20% and for silver rank is 
10%. Users in bronze rank doesn't have any discount. 
