# GraphQL API starter

Run in this folder:

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

Open `http://localhost:8080/graphiql` and run:

```graphql
mutation { createTask(input: {title: "Learn GraphQL"}) { id title } }
```

Then run `{ tasks { id title } }`.
