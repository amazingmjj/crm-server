-- 业务员相关物资品类注释
comment on table crmstatdev.CRM_SALESMAN_GOODS is '业务员相关物资品类';
comment on column CRM_SALESMAN_GOODS.category is '品类';
comment on column CRM_SALESMAN_GOODS.amount is '金额';
comment on column CRM_SALESMAN_GOODS.weight is '重量';
comment on column CRM_SALESMAN_GOODS.employee_code is '业务员编号';
comment on column CRM_SALESMAN_GOODS.employee_name is '业务员名称';
comment on column CRM_SALESMAN_GOODS.type is '类型 1线上 0线下';
comment on column CRM_SALESMAN_GOODS.create_at is '创建时间';
comment on column CRM_SALESMAN_GOODS.update_at is '更新时间';

-- 业务员相关物资高卖表注释
comment on table CRM_SALESMAN_HIGH_SELL is '业务员相关物资高卖表';
comment on column CRM_SALESMAN_HIGH_SELL.deal_date is '交易日期';
comment on column CRM_SALESMAN_HIGH_SELL.employee_code is '业务员编号';
comment on column CRM_SALESMAN_HIGH_SELL.employee_name is '业务员名称';
comment on column CRM_SALESMAN_HIGH_SELL.erp_code is '客户编号';
comment on column CRM_SALESMAN_HIGH_SELL.erp_name is '客户名称';
comment on column CRM_SALESMAN_HIGH_SELL.high_amount is '高卖金额';
comment on column CRM_SALESMAN_HIGH_SELL.create_at is '创建时间';
comment on column CRM_SALESMAN_HIGH_SELL.update_at is '更新时间';

-- 客户欠款表
comment on table CRM_customer_arrears is '客户欠款表';
comment on column crm_customer_arrears.arrear_amount is '欠款金额';
comment on column crm_customer_arrears.employee_name is '业务员名称';
comment on column crm_customer_arrears.employee_code is '业务员编码';
comment on column crm_customer_arrears.erp_code is '客户编码';
comment on column crm_customer_arrears.erp_name is '客户名称';
comment on column crm_customer_arrears.create_at is '创建时间';
comment on column crm_customer_arrears.update_at is '更新时间';

-- 客户成交次数
comment on table crm_customer_deal_count is '客户成交次数表';
comment on column crm_customer_deal_count.erp_code is '客户编号';
comment on column crm_customer_deal_count.erp_name is '客户名称';
comment on column crm_customer_deal_count.deal_count is '成交次数';
comment on column crm_customer_deal_count.create_at is '创建时间';
comment on column crm_customer_deal_count.update_at is '更新时间';

-- 客户异常次数
comment on table crm_customer_expt is '客户异常次数';
comment on column crm_customer_expt.erp_code is '客户编号';
comment on column crm_customer_expt.erp_name is '客户名称';
comment on column crm_customer_expt.expt_count is '异常次数';
comment on column crm_customer_expt.type is '类型 1 违约 2 恶意锁货 3  取消次数';
comment on column crm_customer_expt.create_at is '创建时间';
comment on column crm_customer_expt.update_at is '更新时间';

-- 客户是否开票
comment on table crm_customer_billing is '客户是否开票';
comment on column crm_customer_billing.erp_name is '客户名称';
comment on column crm_customer_billing.erp_code is '客户编号';
comment on column crm_customer_billing.create_at is '创建时间';
comment on column crm_customer_billing.update_at is '更新时间';

select * from crm_customer_order;