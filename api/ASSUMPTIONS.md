# Fandza — Assumptions (provisional)

These decisions were left open in the product design. They are adopted until
confirmed, and the model is designed so each can be changed later.

| # | Area | Assumption |
|---|------|------------|
| A1 | Contract type | **Monthly agreements**. The model keeps a `periodicity` field on the offer so per-trip pricing can be added later. |
| A2 | Request flow | Requests are **accepted/rejected by the driver**, with a response deadline; the guardian can cancel a pending request. |
| A3 | Capacity | "Places" = **available slots** = vehicle capacity minus active agreements. Confirmed transactionally when accepting requests. |
| A4 | Search time | The time used in offer search and matching is the **pickup time** (morning direction by default). |
| A5 | Profiles | One account holds **exactly one profile**: guardian or driver (never both). The profile is chosen at registration and can still be changed while the contact is unverified and the account is pending. |
| A6 | Trip tracking | Follow-up = **trip states only** (scheduled, in_progress, completed, cancelled). No GPS tracking in this scope. |
| A7 | Notifications | **Internal only**. Notification preferences carry configurable channels (push, sms, email) reserved for future delivery providers. |
| A8 | Payments | No payments are processed by the platform. Prices are stored as numeric amount + currency + periodicity, never as free text. |
| A9 | Currency | Amounts are in **MZN** by default; currency is still stored explicitly per offer. |
| A10 | Phone | Local phones use country code **+258**, normalized to E.164 (`+258XXXXXXXXX`). |
| A11 | Time | All timestamps are stored and returned in **UTC** (`Instant`). |
| A12 | Identity | Keycloak is the identity provider (authorization code flow with PKCE on the client). The backend is an OAuth2 resource server; `/auth/login`, `/auth/refresh`, `/auth/logout` are delegated to Keycloak token endpoints, not exposed here. The backend owns registration (`POST /auth/register`), contact verification, profile selection and account state. On first `GET /me` the local user is synced by the Keycloak `sub` claim: matched by `keycloak_id`, then by normalized contact (links pre-registered accounts), otherwise auto-provisioned from the JWT email/name claims. |
| A13 | Admin | The admin role lives in Keycloak (`fandza-admin` realm role) and is mapped to `ROLE_ADMIN`; approval routes require it server-side. |
| A14 | Children | Children (`Student`) are outside the user hierarchy and never authenticate. Multiple guardians may link to one child with different permissions. |
| A15 | Prototype shortcuts | Exploration shortcuts in the prototype do not grant real permissions; authorization is always validated server-side. |
