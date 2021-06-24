# tree-structure

## Running the solution

Clone and run the solution in Docker compose by executing

```shell
git clone https://github.com/hamsatom-psql/tree-structure.git && cd tree-structure && docker-compose up
```

There's Swagger running at [localhost:8080/swagger-ui/](http://localhost:8080/swagger-ui/)  
API's base url is [localhost:8080](http://localhost:8080)

### API locally

1. down Docker compose service `tree-structure-backend`
2. have running Docker compose service `neo4j`
3. build by executing

```shell
./gradlew build
```

4. run Java application with main class [SpringBootMainClass](src/main/java/SpringBootMainClass.java)

## Database choice

I chose Neo4J because it easily operates with trees. Calculating depths after adding new root or moving node to
different level would be cumbersome in SQL. Detecting cycles in SQL is also going to be more difficult than in Neo4J.
Neo4J out of the box supports multiple independent trees. Adding functionality in the future like delete everything
under node is also going to be easier.
