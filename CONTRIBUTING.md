# RetailLab, Git Conventions

This document defines the branching model and commit message convention for the RetailLab project.

## Branching model

Simplified Git Flow, adapted for a small team of 2 to 3 people.

- `main`, always deployable. Only receives merges from `develop` through a pull request. Represents what is running in production.
- `develop`, integration branch. All finished features are merged here first. This is the base branch for new feature branches.
- `feature/<short-description>`, one branch per feature or task. Created from `develop`, merged back into `develop` via pull request.
- `fix/<short-description>`, for bug fixes that are not urgent enough to be a hotfix.
- `hotfix/<short-description>`, for urgent fixes that must go straight to `main`, then get merged back into `develop` as well.

### Examples

```
feature/product-catalog-endpoint
feature/cart-frontend
fix/wrong-price-calculation
hotfix/checkout-crash
```

### Workflow

1. Create a branch from `develop`:
```
git checkout develop
git pull
git checkout -b feature/product-catalog-endpoint
```

2. Commit your work following the convention below.

3. Push and open a pull request into `develop`:
```
git push -u origin feature/product-catalog-endpoint
```

4. After review, merge into `develop`. Delete the branch afterward.

5. When ready to release, open a pull request from `develop` into `main`.

## Commit message convention

This project follows Conventional Commits.

```
<type>(<optional scope>): <short description>
```

### Types

- `feat`, a new feature
- `fix`, a bug fix
- `docs`, documentation only changes
- `style`, formatting, missing semicolons, no code logic change
- `refactor`, code change that neither fixes a bug nor adds a feature
- `test`, adding or correcting tests
- `chore`, tooling, build config, dependency updates

### Examples

```
feat(catalog): add product listing endpoint
fix(cart): correct total price rounding
docs(onboarding): add PowerShell execution policy workaround
chore(deps): bump spring-boot to 3.4.1
```

### Guidelines

- Keep the short description under 72 characters, imperative mood ("add", not "added" or "adds")
- Use the body of the commit (a blank line after the short description, then free text) for context when the change is not self-explanatory
- One logical change per commit, avoid bundling unrelated changes

## Pull requests

- Title follows the same convention as commits, e.g. `feat(catalog): add product listing endpoint`
- Description should briefly explain what changed and why, and link the related Jira issue (e.g. `SCRUM-15`)
- At least one reviewer approval required before merging into `develop`
