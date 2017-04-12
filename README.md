# Ethio.gov

Digital public services platform for Ethiopian government agencies and citizens.

## Repository layout

The production monorepo lives under [`ethio.gov/`](ethio.gov/):

- **platform/** — shared BOM, commons, security, persistence, messaging, observability, web, testing
- **services/** — domain microservices (citizen, identity, land, payments, licensing, …)
- **docs/** — architecture and domain documentation
- **infrastructure/** — deployment and ops assets

## Quick start

```bash
cd ethio.gov
./gradlew build
```

## License

For authorized government use.
