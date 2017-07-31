# Developer onboarding

## Setup

1. Install JDK 21.
2. Install Docker.
3. Clone this repository.
4. Run `./gradlew check`.
5. Start local dependencies:

```bash
docker compose -f infrastructure/docker/docker-compose.yml up -d
```

## Conventions

- Package root: `et.gov.ethgov`
- Timezone in tests and local runtime: `Africa/Addis_Ababa`
- Errors: RFC7807 via `ethgov-platform-web`
- Events: CloudEvents + outbox/inbox via messaging/persistence modules
- Never add a new microservice without completing the prior phase review

## First contribution path

1. Read ADRs in `docs/adr`
2. Read Phase 0 review in `docs/reviews`
3. Implement or harden platform libraries only until Phase 0 is approved
4. After approval, Phase 1 starts with Identity Platform as a real deployable service
