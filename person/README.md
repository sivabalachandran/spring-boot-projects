## Assumptions and Notes:
1. I have not added test case to all components, but have done samples to give a glimpse of unit testing style and 
   Integration testing style.
2. Since the requirements were not clear on GET, POST, PUT and DELETE, I have assumed the entities will be saved and retrieved based 
   on an ID (primary key in the database).
3. Authentication and Authorization is set up as service account since there is clear statement as who will be the 
   consumer of the service is.
4. If the token has expired after 5 mins, please hit the token endpoint again to refresh it.

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
I have leveraged the docker infrastructure for standing up the service which works in conjunction with keycloak, 
Hazelcast and Mysql to service the CRUD operation of person entities.

 1. docker-compose up
 2. Hit http://localhost:9000/actuator/health to see if the application came up successfully.   
 3. Use the post man collection(person-svc.postman_collection.json) to test the service.
 4. Use the token endpoint to create Auth token before using any other API, else APIs will throw 401.

## Security
1. The service is guarded by Spring security and Keycloak.
2. Credentials are not encrypted, but could be done by standing up a config server.
3. keycloak vs local user is achieved by using spring profiles.
    Docker profile - keycloak

## Scalability
1. Hazelcast IMDG which is a distributed database which is capable of discovering other nodes on the network is 
   leveraged for caching. In production environment, this can be standalone nodes with redundancy.
2. GRPC or @AsyncController can be leveraged in production to further add scalability.

## Documentation
1. Swagger provides documentation support

## Production Ready Features
1. Enabled Actuator
2. Added Hawtio to expose JMX statistics which comes handy.





    
