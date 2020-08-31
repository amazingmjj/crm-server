package org.zhd.crm.server.repository.crm

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.CustomerEvaluation
import org.zhd.crm.server.model.projection.AreaSaleCountProjection
import org.zhd.crm.server.model.projection.AreaSaleProjection
import org.zhd.crm.server.model.projection.CustomerEvaluationObjProjection
import org.zhd.crm.server.model.projection.CustomerEvaluationProjection

interface CustomerEvaluationRepository : CrudRepository<CustomerEvaluation, Long> {
    fun findByCstmId(cstmId: Long): CustomerEvaluation?

    @Query(nativeQuery = true, countQuery = "select count(1) from v_cstm_evaluation where 1=1 and (?1 is null or comp_name like ?1) and (?2 is null or linker_name like ?2) and (?3 is null or dpt_name like ?3) and (?4 is null or employee_name like ?4) and (?5 is null or customer_property_mark = ?5) and (?6 is null or create_at >= ?6) and (?7 is null or create_at <= ?7) and (?8 is null or mark = ?8) and (?9 is null or employee_id = ?9) and (?10 is null or dpt_id=?10) order by ?#{#pageable}", value = "select * from v_cstm_evaluation where 1=1 and (?1 is null or comp_name like ?1) and (?2 is null or linker_name like ?2) and (?3 is null or dpt_name like ?3) and (?4 is null or employee_name like ?4) and (?5 is null or customer_property_mark = ?5) and (?6 is null or create_at >= ?6) and (?7 is null or create_at <= ?7) and (?8 is null or mark = ?8) and (?9 is null or employee_id = ?9) and (?10 is null or dpt_id=?10) order by ?#{#pageable}")
    fun findByPg(compName: String?, linkName: String?, dptName: String?, employeeName: String?, propertyMark: String?, startDate: String?, endDate: String?, mark: String?, acctId: String?, dptId: String?, pageable: Pageable): Page<CustomerEvaluationProjection>

    @Query(nativeQuery = true, countQuery = "select count(1) from v_cstm_evaluation where cstm_id = 0 and (?1 is null or comp_name like ?1) and (?2 is null or linker_name like ?2) and (?3 is null or dpt_name like ?3) and (?4 is null or employee_name like ?4) and (?5 is null or customer_property_mark = ?5) and (?6 is null or create_at >= ?6) and (?7 is null or create_at <= ?7) and (?8 is null or mark = ?8) and (?9 is null or employee_id = ?9) and (?10 is null or dpt_id=?10) order by ?#{#pageable}", value = "select * from v_cstm_evaluation where cstm_id = 0 and (?1 is null or comp_name like ?1) and (?2 is null or linker_name like ?2) and (?3 is null or dpt_name like ?3) and (?4 is null or employee_name like ?4) and (?5 is null or customer_property_mark = ?5) and (?6 is null or create_at >= ?6) and (?7 is null or create_at <= ?7) and (?8 is null or mark = ?8) and (?9 is null or employee_id = ?9) and (?10 is null or dpt_id=?10) order by ?#{#pageable}")
    fun findByNeedUpdatePg(compName: String?, linkName: String?, dptName: String?, employeeName: String?, propertyMark: String?, startDate: String?, endDate: String?, mark: String?, acctId: String?, dptId: String?, pageable: Pageable): Page<CustomerEvaluationProjection>

    @Query(nativeQuery = true, countQuery = "select count(1) from v_cstm_evaluation where cstm_id > 0 and (?1 is null or comp_name like ?1) and (?2 is null or linker_name like ?2) and (?3 is null or dpt_name like ?3) and (?4 is null or employee_name like ?4) and (?5 is null or customer_property_mark = ?5) and (?6 is null or create_at >= ?6) and (?7 is null or create_at <= ?7) and (?8 is null or mark = ?8) and (?9 is null or employee_id = ?9) and (?10 is null or dpt_id=?10) order by ?#{#pageable}", value = "select * from v_cstm_evaluation where cstm_id > 0 and (?1 is null or comp_name like ?1) and (?2 is null or linker_name like ?2) and (?3 is null or dpt_name like ?3) and (?4 is null or employee_name like ?4) and (?5 is null or customer_property_mark = ?5) and (?6 is null or create_at >= ?6) and (?7 is null or create_at <= ?7) and (?8 is null or mark = ?8) and (?9 is null or employee_id = ?9) and (?10 is null or dpt_id=?10) order by ?#{#pageable}")
    fun findByUpdatedPg(compName: String?, linkName: String?, dptName: String?, employeeName: String?, propertyMark: String?, startDate: String?, endDate: String?, mark: String?, acctId: String?, dptId: String?, pageable: Pageable): Page<CustomerEvaluationProjection>

    @Query(nativeQuery = true, value = "select * from v_cstm_evaluation where id=?1")
    fun findOneCstm(cstmId: Long): List<CustomerEvaluationObjProjection>

    @Query(nativeQuery = true, value = "select * from v_cstm_evaluation where status = 1 and (cstm_property_ids like %?1% or ?1 is null) " +
            "and (comp_name=?2 or ?2 is null)")
    fun findByCstProperty(cstProperty:String?,compName: String?) : List<CustomerEvaluationProjection>

    @Query(nativeQuery = true ,
            value = "SELECT d.name,d.provice_name,c.wsflag,c.ny,round(sum(c.weight),3) as weight " +
                    " FROM V_MONTH_SALE c left join t_customer b on c.datas_customer=b.erp_code " +
                    " left join v_city_prov d on b.region||'市'=d.name " +
                    " where d.ID is not null and (c.ny>=?1 or ?1 is null) and (c.ny<=?2 or ?2 is null) and (c.wsflag=?3 or ?3 is null)" +
                    " group by d.name,d.provice_name,c.wsflag,c.ny order by sum(c.weight) desc")
    fun findAreaSale(startNy: String?,endNy: String?,wsFlag:String?):List<AreaSaleProjection>

    @Query(nativeQuery = true ,
            value = "SELECT a.name,t.area_name,a.provice_name,count(*) as sum_num," +
                    "(SELECT count(*) FROM t_customer_evaluation t1 where t1.area_name=t.area_name and t1.cstm_property_ids like '%贸易商%') as mycount," +
                    "(SELECT count(*) FROM t_customer_evaluation t1 where t1.area_name=t.area_name and t1.cstm_property_ids like '%终端客户%') as zdcount," +
                    "(SELECT count(*) FROM t_customer_evaluation t1 where t1.area_name=t.area_name and t1.cstm_property_ids like '%加工单位%') as jgcount," +
                    "(SELECT count(*) FROM t_customer_evaluation t1 where t1.area_name=t.area_name and t1.cstm_property_ids like '%物流单位%') as wlcount," +
                    "(SELECT count(*) FROM t_customer_evaluation t1 where t1.area_name=t.area_name and t1.cstm_property_ids like '%其他%') as qtcount" +
                    " FROM t_customer_evaluation t left join v_city_prov a on t.area_name||'市'=a.name " +
                    " where t.cstm_property_ids is not null group by a.name,t.area_name,a.provice_name ")
    fun findCstAreaCount():List<AreaSaleCountProjection>

    @Query(nativeQuery = true,
            value = "SELECT s.provice_name,s.main_delivery_way,s.way_num,to_char(round(s.way_num/m.sum_num*100,2),'990.99') FROM" +
                    " (SELECT a.provice_name,t.main_delivery_way,count(*) as way_num" +
                    " FROM t_customer_evaluation t left join v_city_prov a on t.area_name||'市'=a.name " +
                    "where t.main_delivery_way is not null group by t.main_delivery_way,a.provice_name) s" +
                    " left join " +
                    "(SELECT a.provice_name,count(*) as sum_num FROM t_customer_evaluation t " +
                    "left join v_city_prov a on t.area_name||'市'=a.name " +
                    " where t.main_delivery_way is not null group by a.provice_name) m " +
                    " on s.provice_name=m.provice_name order by round(s.way_num/m.sum_num,4)")
    fun findProvCstDeliveryPercent():List<Array<Any>>

    @Query(nativeQuery = true,
            value = "SELECT s.area_name||'市',s.main_delivery_way,s.way_num,to_char(round(s.way_num/m.sum_num*100,2),'990.99') FROM " +
                    "(SELECT t.area_name,t.main_delivery_way,count(*) as way_num" +
                    " FROM t_customer_evaluation t where t.main_delivery_way is not null " +
                    " group by t.main_delivery_way,t.area_name) s" +
                    " left join " +
                    "(SELECT t.area_name,count(*) as sum_num FROM t_customer_evaluation t " +
                    " where t.main_delivery_way is not null group by t.area_name) m " +
                    " on s.area_name=m.area_name order by round(s.way_num/m.sum_num,4)")
    fun findAreaCstDeliveryPercent():List<Array<Any>>

//    @Query(nativeQuery = true, countQuery = "select count(1) from v_cstm_evaluation where 1=1 and (?1 is null or comp_name like ?1) and (?2 is null or linker_name like ?2) and (?3 is null or dpt_name like ?3) and (?4 is null or employee_name like ?4) and (?5 is null or customer_property_mark = ?5) and (?6 is null or mark = ?6) order by ?#{#pageable}", value = "select * from v_cstm_evaluation where 1=1 and (?1 is null or comp_name like ?1) and (?2 is null or linker_name like ?2) and (?3 is null or dpt_name like ?3) and (?4 is null or employee_name like ?4) and (?5 is null or customer_property_mark = ?5) and (?6 is null or mark = ?6) order by ?#{#pageable}")
//    fun findByTestPg(compName: String?, linkName: String?, dptName: String?, employeeName: String?, propertyMark: String?, mark: String?, pageable: Pageable): Page<CustomerEvaluationProjection>
//
//    @Query(nativeQuery = true, countQuery = "select count(1) from v_cstm_evaluation where 1=1 and (?1 is null or comp_name like ?1) and (?2 is null or linker_name like ?2) and (?3 is null or dpt_name like ?3) and (?4 is null or employee_name like ?4) and (?5 is null or create_at >= ?5) and (?6 is null or create_at <= ?6) order by ?#{#pageable}", value = "select * from v_cstm_evaluation where 1=1 and (?1 is null or comp_name like ?1) and (?2 is null or linker_name like ?2) and (?3 is null or dpt_name like ?3) and (?4 is null or employee_name like ?4) and (?5 is null or create_at >= ?5) and (?6 is null or create_at <= ?6) order by ?#{#pageable}")
//    fun findByTestPg(compName: String?, linkName: String?, dptName: String?, employeeName: String?, startDate: Timestamp?, endDate: Timestamp?, pageable: Pageable): Page<CustomerEvaluationProjection>
//
//    @Query(nativeQuery = true, countQuery = "select count(1) from v_cstm_evaluation where 1=1 and (?1 is null or comp_name like ?1) and (?2 is null or linker_name like ?2) and (?3 is null or dpt_name like ?3) and (?4 is null or employee_name like ?4) order by ?#{#pageable}", value = "select * from v_cstm_evaluation where 1=1 and (?1 is null or comp_name like ?1) and (?2 is null or linker_name like ?2) and (?3 is null or dpt_name like ?3) and (?4 is null or employee_name like ?4) order by ?#{#pageable}")
//    fun findByTestPg(compName: String?, linkName: String?, dptName: String?, employeeName: String?, pageable: Pageable): Page<CustomerEvaluationProjection>
//    @Query(nativeQuery = true, countQuery = "select count(1) from v_cstm_evaluation order by ?#{#pageable}" , value="select v.* from v_cstm_evaluation v where 1=1 and (?1 is null or v.comp_name like ?1) and (?2 is null or v.linker_name like ?2) and (?3 is null or v.dpt_name like ?3) and (?4 is null or v.employee_name like ?4) order by ?#{#pageable}")
//    fun findByTestPg(pageable: Pageable): Page<CustomerEvaluationProjection>

}