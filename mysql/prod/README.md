# 生产库脚本

| 文件 | 用途 |
|------|------|
| `init_mms_prod_core.sql` | **生产全量**：新环境初始化。历史 `YYYYMMDD_*.sql` 增量已于 2026-08-08 并入本文件后删除。 |
| `20260808_2105_finance_tpl_payroll_align.sql` | **已上线库补丁**：工资条相关模板账户/分类（新装库可跳过）。 |
| `clone_dev_to_prod.sql` | 从开发库克隆到生产的辅助脚本（按需）。 |

## 约定

1. 新环境只跑 `init_mms_prod_core.sql`。
2. **已上线库**若还需补结构/种子：新增 `YYYYMMDD_HHMM_描述.sql`，执行后把同等最终态写回 `init_mms_prod_core.sql`。
3. 开发侧改动对齐 `../init_mms_dev_core.sql`。
