# 勤怠管理システム Backend

Attendance Management System - Spring Boot 3.4.x + MyBatis

## 技術スタック

| 項目 | バージョン |
|------|-----------|
| Java | 17 LTS |
| Spring Boot | 3.4.1 |
| MyBatis | 3.0.5 |
| MapStruct | 1.6.3 |
| JWT (jjwt) | 0.12.6 |
| PostgreSQL | 16 |

## セットアップ

### 1. 前提条件
- Java 17+
- Maven 3.9+
- PostgreSQL (または H2 で開発)

### 2. 開発環境で起動

```bash
# H2インメモリDBで起動
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 3. 本番環境で起動

```bash
# Docker Composeで起動
docker-compose up -d
```

## API仕様

起動後、Swagger UIにアクセス:
- http://localhost:8080/swagger-ui/index.html

## 認証

JWT認証を使用:
1. POST /api/v1/auth/login でログイン
2. レスポンスの `accessToken` を `Authorization: Bearer {token}` に設定

## 構造

```
com.company.kintai/
├── common/           # 共通レイヤー
│   ├── config/       # Security, OpenAPI
│   ├── constant/     # ApiPath, ErrorCode, AppConstant
│   ├── dto/          # ApiResponse, PageResult
│   ├── exception/    # GlobalExceptionHandler
│   ├── security/     # JWT
│   └── util/         # JapaneseTextUtil, WarekiUtil, CsvUtil
├── auth/             # 認証機能
├── employee/         # 社員管理
├── attendance/       # 勤怠管理
├── leave/            # 有給管理
├── payroll/          # 給与計算
└── report/           # レポート
```

## テスト

```bash
# ユニットテスト
mvn test

# 統合テスト
mvn verify
```

## DBConvention

- テーブル: `snake_case` (単数形)
- カラム: `snake_case`
- PK: `id` (AUTO_INCREMENT)
- ソフトデリート: `deleted`カラム
- オプティミスティックロック: `version`カラム
