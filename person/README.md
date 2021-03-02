## Tech Stack
 - Spring boot
 - Docker
 - Hazelcast IMDG - Caching
 - Keycloak - Authentication & Authorization
 - Mysql
 - H2 In-memory Database
 - Spring Security
 - Spock - Unit Testing

## How To Run
I have leveraged the docker infrastructure for standing up the service which works in conjunction with keycloak, Hazelcast and Mysql to service the CRUD operation of person entities.

 1. docker-compose up
 2. Hit http://localhost:9000/actuator/health to see if the application came up successfully.   
 3. Use the post man collection to test the service.
 4. Use the token endpoint to create Auth token before using any other API, else APIs will throw 401.

## Notes:
 1. I have not added test case to all components, but have done samples to give a glimpse of unit testing style and Integration testing style.
 2. Since the requirements were not clear on get and put, I have assumed the entities will be saved and retrieved based on an ID (primary key in the database).
 3. Authentication and Authorization is setup as service account since there is clear statement as who will be the consumer of the service is.
 4. If the token has expired after 5 mins, please hit the token endpoint again to refresh it.






    