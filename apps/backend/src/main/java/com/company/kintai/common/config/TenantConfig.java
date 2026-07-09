package com.company.kintai.common.config;

/**
 * Phase 0 placeholder cho hạ tầng RLS (Row Level Security) đa tenant.
 *
 * <p>Các bảng trong schema {@code app} dùng {@code ENABLE ROW LEVEL SECURITY} với policy dựa vào
 * {@code current_setting('app.current_tenant_id', true)} (xem V3__employee_relations.sql). Nếu biến
 * session này không được {@code SET}/{@code set_config} trước khi query, mọi SELECT/UPDATE sẽ trả về
 * 0 dòng một cách "câm lặng" (không lỗi) — rất khó debug nếu không biết trước.
 *
 * <p>Ở Phase 0-1, cách áp dụng được chọn (quyết định của Opus, xem
 * docs/plans/roadmap-30-tables/01_PHASE0_flyway_infra.md): nối câu {@code SELECT
 * set_config('app.current_tenant_id', #{currentTenantId}, true)} trực tiếp trong từng MyBatis Mapper
 * XML cần RLS, chưa cần AOP interceptor. Logic AOP tập trung (Spring {@code @Around} trên
 * DataSource/Connection) sẽ được nâng cấp ở Phase 8 khi cần transaction-scope thật.
 *
 * <p>Class này chỉ giữ constant dùng chung để các Mapper/Service tham chiếu, tránh hard-code chuỗi
 * rải rác.
 */
public final class TenantConfig {

    /** Tên session variable Postgres dùng trong RLS policy (xem V3__employee_relations.sql). */
    public static final String TENANT_SESSION_VARIABLE = "app.current_tenant_id";

    private TenantConfig() {}
}
