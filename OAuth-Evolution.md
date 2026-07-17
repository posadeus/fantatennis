# OAuth / Authorization — Evolution Notes

This document tracks how authentication & authorization are wired **today** and the
deliberate simplifications we chose to defer. Each "Evolution" section is a planned next
increment, roughly in priority order.

---

## Current state (implemented)

- **Authentication:** Google OAuth2 login (`oauth2Login`). Provider details are
  auto-configured from `CommonOAuth2Provider.GOOGLE`; only the client registration lives in
  `application.yml`, with `GOOGLE_CLIENT_ID` / `GOOGLE_CLIENT_SECRET` supplied via env vars.
- **Who can log in at all:** currently gated by the **Test users** list on the Google OAuth
  consent screen (a Google-console setting, *not* app logic).
- **Identity → domain owner:** the authenticated email is turned into a **pseudonymous `ownerId`**
  at the outermost layer, so the raw email never enters the domain. `SecurityContextCurrentUser`
  reads the email from the security context, normalizes it (`trim().lowercase()`) and anonymizes it
  via `Anonymizer` (`infrastructure/security/Sha256Anonymizer.kt`: deterministic SHA-256 + a secret
  `app.security.owner-id-pepper` / `OWNER_ID_PEPPER`) before exposing it through the `CurrentUser`
  port as `ownerId(): String`. `CreateTeamService` ignores any client-supplied `ownerId` and always
  uses `currentUser.ownerId()`. **Note:** this is *pseudonymization*, not anonymization — the value
  is still personal data under GDPR (see Evolution 5).
- **Roles:** a single admin address (`app.security.admin-email`, default from `ADMIN_EMAIL`) is
  granted `ROLE_ADMIN` at login by `SecurityConfig.adminAwareOidcUserService`; everyone else gets
  `ROLE_USER`. Only `/job/**` (data-import jobs) requires `ROLE_ADMIN`.

### Known limitations to close next

- **Per-team ownership is not yet enforced on read/mutate.** `retrieve`, `addPlayers`, and
  `swap` in `TeamController` still act on a raw `teamId` with no check that the team belongs to
  the caller. Any authenticated user can currently touch any team by id. **This is the most
  important immediate follow-up** (see Evolution 3).
- The `ownerId` field still exists on the `create` request schema (`openApi-Team.yml`) but is
  ignored server-side. It should be removed from the spec so the API doesn't advertise a field
  that does nothing.
- CSRF is disabled (fine for a stateless REST API; revisit if server-rendered forms are added).

---

## Evolution 1 — Introduce a User / Account entity

**Why:** using the raw email as `ownerId` couples domain data to a mutable string. A stable
internal identity decouples "who they signed in as" from "who they are in the game", and gives a
home for profile data and roles.

**Sketch:**
- New `users` table: `id` (internal PK), `google_sub` (stable Google subject id, unique),
  `email`, `display_name`, `created_at`.
- On first login, upsert by `google_sub`; map the OIDC principal to the internal user id.
- `CurrentUser` gains something like `id(): UserId` alongside (or instead of) `email()`.
- `FantaTeam.ownerId` becomes a FK to `users.id` rather than an email string. Migrate existing
  rows (email → user id) with a data migration.

**Touches:** `CurrentUser` port + impl, a `users` DAO/repository (mirroring the existing JDBC DAO
pattern), `CreateTeamService`, and the team schema/migration.

---

## Evolution 2 — Real role model (replace the hard-coded admin email)

**Why:** the admin is currently one address baked into config. That doesn't scale to multiple
admins or finer-grained permissions, and it lives outside the data model.

**Sketch:**
- Roles stored per user (e.g. `user_roles` table, or a `role` column) once Evolution 1 exists.
- `adminAwareOidcUserService` stops reading `app.security.admin-email` and instead loads
  authorities from the user's persisted roles.
- Consider method-level security (`@EnableMethodSecurity` + `@PreAuthorize`) for finer rules
  beyond URL matching.

**Interim compatibility:** keep `app.security.admin-email` working until the DB-backed roles are
seeded, then remove it.

---

## Evolution 3 — Per-team ownership enforcement (do this soon)

**Why:** closes the remaining hole where any authenticated user can read/modify another user's
team by id. Per `CLAUDE.md`, ownership is business logic and belongs in the **services**, not the
controllers.

**Sketch:**
- In `RetrieveTeamService`, `AddPlayersTeamService`, and `SwapPlayersTeamService`, load the
  target team's owner and compare against `currentUser` before returning/mutating.
- Return a distinct "forbidden" domain outcome (mapped to HTTP 403) rather than reusing
  not-found/error, so the controller can respond correctly.
- Add unit tests covering "owner" vs "not owner" for each mutating operation.

---

## Evolution 4 — Move access control out of the consent screen

**Why:** the Google **Test users** list is our de-facto allow-list today, but it's a console
setting and caps at 100 users while the app is unpublished.

**Options:**
- Publish the OAuth consent screen and enforce membership in-app (Evolution 1's `users` table as
  an allow-list, rejecting unknown accounts at login), **or**
- Keep an explicit allow-list (config or DB) checked in the `OidcUserService`.

---

## Evolution 5 — GDPR compliance

**Why:** hashing the email into `ownerId` (SHA-256 + pepper) is a good data-minimization and
security measure, but it is **pseudonymization, not anonymization**. The hash is deterministic (a
stable per-person identifier) and reversible with additional information (the pepper + a list of
known emails), so under GDPR (Recital 26, art. 4(5)) `ownerId` **remains personal data** and the
regulation still applies in full. Compliance is a property of the whole system and its processes,
not of a single hashed field. *(This is engineering guidance, not legal advice — validate with a
DPO / lawyer.)*

**What the current pseudonymization already gives us:** it supports *privacy by design* (art. 25)
and *security of processing* (art. 32) — the raw email no longer flows into the domain, the DB, or
API responses.

**What still needs to be in place (technical/product work):**
- **Don't log raw PII.** ✅ Done in `HomeController` — the login `println`s of the email and OIDC
  attributes were removed (logs are a form of processing and often leave the system). Keep auditing
  for any other PII that might reach the logs, and prefer logging the pseudonymous `ownerId`.
- **Treat the pepper as a secret (art. 32).** It is the key of the pseudonymization: keep it out of
  the repo and DB, restrict access, use a distinct value per environment, and have a rotation plan
  (note: rotating the pepper invalidates all existing `ownerId`s — needs a re-mapping strategy,
  easier once Evolution 1's `users` table exists).
- **Right to erasure (art. 17).** Design a way to delete a user's data on request — remove their
  teams and any `users` row (Evolution 1). Deleting rows suffices; the hash itself is not separately
  erasable.
- **Right of access / portability (art. 15, 20).** Be able to export the data tied to a user.
- **Retention policy.** Define how long team/identity data is kept and enforce it.
- **Consider using the Google `sub`** (opaque, non-PII subject id) as the internal identity instead
  of a hashed email once Evolution 1 lands — it avoids deriving the identifier from an email
  altogether.

**What is organizational (outside the codebase) but required for actual compliance:**
- A lawful basis for processing the email (consent / contract / legitimate interest) and a
  **privacy policy / informativa** describing what is collected and why.
- A **record of processing activities**, **DPAs** with processors (Google for OAuth, the DB host),
  and a **data-breach notification** procedure (72h).

**Touches:** ~~`HomeController` (remove PII logging)~~ ✅ done, secret management for the pepper,
and — for erasure/access — the `users` table and DAO from Evolution 1.
