# Source walkthroughs for every runnable starter

[← Runnable starters](../starters/README.md) · [Adapt a starter](starter-adaptation-guide.md) · [Java foundation](java-spring-foundation.md)

Use this page after an unchanged starter builds. Open the files in the stated order and trace one input to one observable result before renaming anything.

## REST API: Taskboard

1. `TaskController` maps HTTP methods and JSON.
2. Request records under `task/dto/` validate input.
3. `TaskService` owns transactions and use-case coordination.
4. `TaskRepository` persists `Task` entities.
5. `TaskMapper` keeps stored state separate from API responses.
6. `ApiExceptionHandler` converts expected failures to safe HTTP problems.
7. Controller, service, and repository tests each verify a different boundary.

Observable result: create, list, update, and delete tasks through `/api/tasks` with file-backed H2 storage.

## GraphQL API

1. `graphql/schema.graphqls` defines the public query, mutation, input, and task types.
2. `TaskGraphqlController` maps schema operations to Java methods.
3. `CreateTaskInput` binds and validates mutation input.
4. `TaskService` owns the in-memory example state.
5. `TaskServiceTest` checks behavior without GraphQL transport.

Observable result: a GraphQL mutation creates a task and a query lists it.

## Server-rendered web application

1. `TaskController` handles GET and POST form flows.
2. `TaskForm` defines form fields and validation.
3. `TaskService` owns the in-memory task behavior.
4. `templates/tasks/form.html` renders input and validation messages.
5. `templates/tasks/detail.html` renders the successful result.

Observable result: a browser form rejects a blank title and renders the accepted task.

## Background worker

1. `BackgroundWorkerApplication` enables scheduling.
2. `CleanupJob` owns the trigger and schedule expression.
3. `CleanupService` owns the repeatable unit of work.
4. `application.properties` supplies the schedule.
5. `CleanupServiceTest` checks work independently of the clock.

Observable result: the terminal logs one bounded cleanup run every ten seconds.

## Event-driven service

1. `DemoEventPublisher` creates the demonstration event when messaging is enabled.
2. `OrderEventListener` receives broker records.
3. `OrderService` applies the duplicate-event guard and business handling.
4. `application.properties` keeps broker mode disabled by default.
5. `OrderServiceTest` proves duplicate input is ignored.

Observable result: with Kafka enabled, one event is published, consumed, and processed once.

## Integration service

1. `PaymentController` accepts the application-owned HTTP request.
2. `PaymentService` depends on the `ProviderClient` interface.
3. `StubProviderClient` provides safe offline behavior.
4. `HttpProviderAdapter` is the boundary for a real provider contract.
5. Configuration selects stub or HTTP mode.
6. `PaymentServiceTest` verifies coordination without a provider network.

Observable result: a local payment request returns a deterministic stub result; a real adapter can replace it without changing the service.

## API gateway

1. `application.properties` defines a route from `/service/**` to `DOWNSTREAM_URL`.
2. The route removes the public prefix before forwarding.
3. `CorrelationIdFilter` preserves or creates `X-Correlation-Id`.
4. `ApiGatewayApplicationTests` verifies application wiring.

Observable result: requests are routed to a downstream service with a traceable correlation ID.

## Batch application

1. `ImportJobConfiguration` defines the Spring Batch job and step.
2. `ImportService` owns the sample item processing.
3. H2 stores job-execution metadata.
4. `ImportServiceTest` checks processing separately from Batch infrastructure.

Observable result: a finite job processes three sample items and records its execution.

## Real-time application

1. `WebSocketConfiguration` registers the `/ws` endpoint.
2. `LiveMessageHandler` handles connection messages.
3. `static/index.html` is a minimal browser client.
4. `RealtimeApplicationTests` verifies application wiring.

Observable result: the browser connects and receives `server: <message>` without polling.

## Command-line application

1. `CommandLineApplication` starts without an HTTP server.
2. `ImportCommand` reads and validates command options.
3. `ImportService` performs the use case.
4. The process prints a result and exits.
5. `ImportServiceTest` verifies behavior without invoking a shell.

Observable result: `--name=customer-a` produces one terminal result and a successful exit.

## Questions to answer before adapting any starter

- What starts the work: HTTP, form, timer, message, batch launch, socket, or command?
- Which class owns the application use case?
- Which class is an external boundary rather than business logic?
- Where is configuration supplied?
- Which test is fastest while changing behavior?
- What state is intentionally in memory and must become durable for production?
- What exact result proves the application works?

If you cannot answer these questions, trace the unchanged example again before renaming it.
