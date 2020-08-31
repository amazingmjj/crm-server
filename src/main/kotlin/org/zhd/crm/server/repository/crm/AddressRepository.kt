package org.zhd.crm.server.repository.crm

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.zhd.crm.server.model.crm.Address
import org.zhd.crm.server.model.projection.CityProvProjection

interface AddressRepository : CrudRepository<Address, Long> {
    fun findByCode(code: String): Address?

    @Query("from Address t where (?1 is null or to_char(t.type)=?1) and (t.name like %?2% or ?2 is null)")
    fun findAllPg(type: String?, name: String?, pageable: Pageable): Page<Address>

    @Query(nativeQuery =true, value =  "select * from v_address_city where 1=1 and (name like %?1% or ?1 is null) order by ?#{#pageable}", countQuery = "select count(id) from v_address_city where 1=1 and (name like ?1 or ?1 is null) order by ?#{#pageable}")
    fun findByCityAllPg(name: String?, pageable: Pageable): Page<Address>

    @Query(nativeQuery = true, value = "SELECT t.name,t.provice_name,t.type,t.PARENT_CODE FROM v_city_prov t")
    fun findCityProv():List<CityProvProjection>

    @Query("from Address t where t.type=?1 and (t.parentCode=?2 or ?2 is null)")
    fun findAddressTree(type: Int , parentCode:String?):List<Address>
}