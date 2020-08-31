package org.zhd.crm.server.repository.statistic

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.projection.erp.CustomerBillTimeProjection
import org.zhd.crm.server.model.projection.erp.CustomerYearGoodsProjection
import org.zhd.crm.server.model.projection.erp.CustomerYearSaleProjection
import org.zhd.crm.server.model.statistic.BehaviorRecord

/**
 * 客户评估相关接口
 * @author samy
 * @date 2020/05/22
 */
interface CustomerEvaluationRelationRepository : CrudRepository<BehaviorRecord, Long> {
    /**
     * 客户评估相关年度销量
     */
//    @Query(nativeQuery = true, value = "SELECT YEARNUM AS YEAR_STR, DATAS_CUSTOMER AS ERP_CODE, SBILL_WEIGHT AS bill_weight , DATA_BWEIGHT AS lad_weight, GM AS high_sale FROM czzhd.v_history_sale_sum@crmstat_erp19 where czzhd.p_history_sale_sum_param.set_year_start@crmstat_erp19(?2)=?2 and czzhd.p_history_sale_sum_param.set_year_end@crmstat_erp19(?3)=?3 and czzhd.p_history_sale_sum_param.set_cstcode@crmstat_erp19(?1) = ?1 order by YEARNUM desc")
    @Query(nativeQuery = true, value = "SELECT YEARNUM AS YEAR_STR, DATAS_CUSTOMER AS ERP_CODE, SBILL_WEIGHT AS bill_weight , DATA_BWEIGHT AS lad_weight, GM AS high_sale FROM v_history_sale_sum_all where YEARNUM>=?2 and YEARNUM<=?3 and  DATAS_CUSTOMER=?1 order by YEARNUM desc")
    fun customerYearSale(erpCode: String, startYear: String, endYear: String): List<CustomerYearSaleProjection>

    /**
     * 客户品名，年销量
     */
    @Query(nativeQuery = true, value = "select YEARNUM AS YEAR_STR, DATAS_CUSTOMER AS ERP_CODE, PARTSNAME_NAME as name from v_partsname_order where YEARNUM = ?1 and DATAS_CUSTOMER =?2 order by ?#{#pageable}", countQuery = "select count(1) from v_partsname_order where YEARNUM = ?1 and DATAS_CUSTOMER =?2 order by ?#{#pageable}")
    fun customerYearGoods(year: String, erpCode: String, pageable: Pageable): Page<CustomerYearGoodsProjection>

    /**
     * 客户评估相关开单时间
     */
    @Query(nativeQuery = true, value = "SELECT DATAS_CUSTOMER AS erp_code, FIRST_SBILL AS first_bill_time, FIRST_DELIVERY AS first_lad_time FROM v_delivery_order where DATAS_CUSTOMER=?1")
    fun customerBillTime(erpCode: String): List<CustomerBillTimeProjection>

    /**
     * 当前年客户主要购买物资销量
     * @author samy
     * @date 2020/06/13
     */
    @Query(nativeQuery = true, value = "SELECT PNTREE_NAME FROM V_PNTREE_NAME_ORDER@crmstat_erp WHERE DATAS_CUSTOMER = ?1")
    fun currentYearMainPurchaseGoods(erpCode: String): List<String>
}