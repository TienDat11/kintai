# Kintai Monorepo

**勤怠管理システム (Kintai HR/Payroll System)** — Vietnamese + English

## Tổng quan / Overview

Hệ thống quản lý chấm công, nhân sự, và bảng lương toàn diện.
Comprehensive HR, attendance, leave, and payroll management system.

## Tính năng / Features

- Quản lý chấm công / Attendance tracking
- Quản lý nghỉ phép / Leave management
- Tính lương / Payroll processing
- Phân quyền RBAC / Role-based access control
- Kiểm toán / Audit logging
- Thời gian thực / Real-time notifications
- Xuất PDF / PDF report generation
- Google Sheets tích hợp / Google Sheets integration

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Spring Boot 3.4 |
| ORM | MyBatis |
| Database | PostgreSQL (Flyway) |
| Frontend | Angular 20 |
| UI | PrimeNG |
| State | Angular Signals |
| Auth | Spring Security + RBAC |
| Infra | Fly.io / Vercel |
| Cache | Upstash Redis |
| Storage | Cloudflare R2 |
| Security | AES-256-GCM |

## Cấu trúc kho / Repository Structure

```
kintai-monorepo/
├── apps/
│   ├── backend/        # Spring Boot
│   └── frontend/       # Angular
├── infra/
│   ├── docker/         # Docker configs
│   ├── fly/            # Fly.io configs
│   └── github/         # GitHub Actions
├── docs/
│   ├── architecture/   # Architecture docs
│   ├── adr/            # ADR records
│   ├── api/            # API docs
│   └── runbooks/       # Runbooks
├── .github/
│   ├── workflows/      # CI/CD
│   └── ISSUE_TEMPLATE/ # Templates
└── .omc/                # OMC state
```

## Bắt đầu nhanh / Quick Start

### Backend
```bash
cd apps/backend
./mvnw spring-boot:run
```

### Frontend
```bash
cd apps/frontend
npm install
ng serve
```

## Tài liệu / Documentation

- [Kiến trúc / Architecture](ARCHITECTURE.md)
- [Cơ sở dữ liệu / Database](docs/architecture/)
- [Hướng dẫn / Runbooks](docs/runbooks/)

## Giấy phép / License

Apache 2.0 — xem [LICENSE](LICENSE)