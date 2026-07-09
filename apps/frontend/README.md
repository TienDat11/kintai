# 勤怠管理システム Frontend

Attendance Management System - Angular 20

## 技術スタック

| 項目 | バージョン |
|------|-----------|
| Angular | 20 |
| PrimeNG | 19 |
| NgRx Signals | 19 |
| TypeScript | 5.7 |

## セットアップ

### 1. 前提条件
- Node.js 20+
- npm 10+

### 2. インストール

```bash
npm install
```

### 3. 開発サーバー起動

```bash
ng serve
```

http://localhost:4200 にアクセス

### 4. ビルド

```bash
ng build --configuration production
```

## 構造

```
src/app/
├── core/             # シングルトン
│   ├── auth/         # 認証サービス、ガード
│   ├── http/         # インターセプター
│   └── layout/       # メインレイアウト
├── shared/           # 共通コンポーネント
│   ├── components/   # UIコンポーネント
│   ├── pipes/        # yen, wareki, zenkaku
│   ├── constant/     # 定数
│   ├── util/         # ユーティリティ
│   └── model/        # 型定義
└── features/         # 機能モジュール
    ├── auth/         # ログイン
    ├── dashboard/    # ダッシュボード
    ├── employee/     # 社員管理
    ├── attendance/   # 勤怠管理
    ├── leave/        # 有給管理
    ├── payroll/      # 給与
    └── report/       # レポート
```

## テスト

```bash
ng test
ng lint
```

## i18n

ビルド時にロケール別バンドル生成:
```bash
ng build --localize
```

## デザイントークン

`src/styles.scss` で定義。コンポーネントはトークンのみ使用。
