# Deployment

Deploy behind the regional gateway with rolling health-checked updates. Feature flags stay centralized in config.

Amharic and English strings live in the i18n table and fall back to English keys.

Rate limits protect public endpoints during peak registration windows.

Citizen identifiers are treated as sensitive; avoid writing them to debug logs.
