# Architecture

## Overview

Kintai — monorepo HR/payroll system.
- Database: 30 tables, see `.omc/plans/database-v3.md`
- Backend: Spring Boot 3.4 + MyBatis + Flyway
- Frontend: Angular 20 + PrimeNG + Signals
- Infrastructure: Neon, Cloudflare R2, Upstash Redis, Fly.io, Vercel
- Security: AES-256-GCM, RBAC, RLS, audit triggers
- Monitoring: Sentry, Axiom