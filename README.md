# MyTasksWebService

*Spring RESTful web service for a multi-user task editor*


## Introduction

This web service project is part of the **MyTasks** application and provides REST access for managing:

* *Users*

* *Tasks*

in a multi-user environment supporting *password-based* authentication.


## Building and running the project

[Gradle](https://gradle.org/) is required to build the project - which is an **executable Spring Boot jar**.

To create it, just go to the project directory and run:

> gradle build

Then, to execute it:

> ./build/libs/webservice-1.0.jar

The service is a standard web service listening on **port 8080** - so it can be accessed via *curl* or browser plugins like *Restlet client*.

Please, note that, by default, the service relies on a **local** PostgreSQL having the following parameters:

* **port: 5432**

* **postgres** user

* **adminPwd** password

* **MyTests** db manually created and then initialized (for example, via the **setup.sql** script)


## Web service details

For details about the supported URIs, verbs and parameters, please refer to the classes annotated with **@RestController**.



## Architectural notes

* **@SpringBootApplication** enables sensible defaults, such as autoconfiguration and component scanning

* **WebConfig** is introduced to support [CORS](https://en.wikipedia.org/wiki/Cross-origin_resource_sharing)

* The chosen underlying storage technology is PostgreSQL via JDBC; **application.properties** can be edited (or another configuration file can be passed) in order to provide different parameters (including a different driver)

* **WebSecurityConfig** deals with authentication and authorization:

    * *Basic* HTTP authentication is employed all over the web service

    * CSRF checks are disabled, as a RESTful web service has no state

    * the **/users** area is only available to the **admin** role

    * OPTIONS preflight requests (e.g., for CORS) are always available out of the admin-only area

* **Task** and **User** are *read-only objects* - functional style introduces more productivity and less errors. For example, in Scala these classes would be *case classes*

* **TaskRepository** and **UserRepository** are DDD concepts hiding how storage is actually performed

* **@Persistent** and **@Caching** are custom **@Qualifier** annotations, enabling the developer to choose between different autowired bean implementations

* **JdbcTaskRepository** and **JdbcUserRepository** realy on:

 * **@Transactional** - on methods writing to the DB

 * **NamedParameterJdbcTemplate** to easily create *prepared statements* with *named parameters*; it also enables mapping from records in the result set to Java objects

 * **ConcurrentHashMapUserRepository** is basd on a *ConcurrentHashMap* to skip DB queries whenever a rest call is performed and user authentication is required. It is just an in-memory, non-distributed cache, but its eviction policies are a consequence of the domain logic - which makes them very straightforward

 * **TaskController** depends on **Principal** (injected by *Spring security*) to retrieve the name of the current user, and to ensure that edited/deleted tasks actually belong to the current user


 * The project features several aspects of **Java 8**:

     * Streams

     * Lambdas

     * Optional

     * Map::computeIfAbsent

* Automated tests verify the behavior of the REST controllers; in particular, they employ:

    * Spring Test

    * JUnit

    * Hamcrest

    * Mockito


## Further references

* [Spring](https://spring.io/)

* [PostgreSQL](https://www.postgresql.org/)

* [Gradle](https://gradle.org/)
