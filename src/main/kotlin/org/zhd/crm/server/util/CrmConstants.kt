package org.zhd.crm.server.util

class CrmConstants {
    companion object {
        //基础数据
        const val DEFAULT_ORG_ID: Long = 1L //默认的机构id
        const val DEFAULT_DPT_ID: Long = 1L //默认的部门id
        const val DEFAULT_ACCT_ID: Long = 1L //默认的业务员id
        const val DEFAULT_ROLE_ID: Long = 7L //默认角色id,业务员
        const val ROLE_HR_ID: Long = 8L //行政人资
        const val DEFAULT_BUSI_RELATION_ID: Long = 1L //默认业务关系id,客户
        const val DEFAULT_BUSI_RELATION: String = "客户" //默认业务关系
        const val BUSI_RELATION_SUPPLIER: String = "供应商" //业务关系
        const val GRADE_COEFFICIENT_FORMULA: Long = 1L //系数公式id
        const val GRADE_COEFFICIENT_REGION: Long = 3L //地区得分id
        const val GRADE_COEFFICIENT_DEBT: Long = 5L //欠款得分id
        const val GRADE_COEFFICIENT_CATEGORY: Long = 10L //品类得分id
        const val DEFAULT_LINKER_ID: Long = 1L //默认联系人幺哥
        const val DEFAULT_LINKER_PHONR: String = "15295188021" //默认幺哥电话
        const val DEFAULT_LINKER_NAME: String = "幺福生" //默认幺哥电话
        const val DEFAULT_CUSTOM_PROPERTY: String = "其他" //默认客户性质
        const val CRM_FOREGROUND_ADDR: String = "http://crm.xingyun361.com" //前台对外地址
        const val CRM_BACKGROUND_ADDR: String = "http://crmadmin.xingyun361.com" //后台对外地址
        //任务状态
        const val TASK_PENDING: Int = 0 //待处理
        const val TASK_NO_PROCESSING: Int = 1 //暂不处理
        const val TASK_UNDER_WAY: Int = 2 //进行中
        const val TASK_COMPLETE: Int = 3 //开发完成
        const val TASK_UNDER_TEST: Int = 4 //待验证
        const val TASK_IN_TEST: Int = 5 //测试中
        const val TASK_FINISH: Int = 6 //已完成
        const val TASK_RETURN: Int = 7 //退回
        const val TASK_CLOSE: Int = 8 //关闭
        //期货代码
        const val FUTURE_CODE_LWG: String = "RB0" //螺纹钢
        const val FUTURE_CODE_RZJB: String = "HC0" //热卷
        const val FUTURE_CODE_TKS: String = "I0" //铁矿
        const val FUTURE_CODE_JT: String = "J0" //焦炭
    }
}