package org.zhd.crm.server

import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.zhd.crm.server.util.CommUtil

@RunWith(SpringRunner::class)
@SpringBootTest
class CommUtilTest{
//    @Autowired
//    private lateinit var commUtil: CommUtil
//
//    private val log = LoggerFactory.getLogger(CommUtilTest::class.java)
//
//    @Test
//    fun testDate(){
//        val value = 4
//        val ds = commUtil.getHourStart(0)
//        val de = commUtil.getHourEnd(23)
//        val dd = commUtil.getHourStart(8)
//        val ws = commUtil.getWeekStartTime(-38)
//        val we = commUtil.getWeekEndTime(-38)
//        val ms = commUtil.getMonthStartTime(value)
//        val me = commUtil.getMonthEndTime(value)
//        val qs = commUtil.getQuarterStartTime(-5)
//        val qe = commUtil.getQuarterEndTime(-5)
//        val ys = commUtil.getYearStartTime(value)
//        val ye = commUtil.getYearEndTime(value)
//        log.info(">>>天：$ds>>>$de>>>$dd>>>周：$ws>>>$we>>>月：$ms>>>$me>>>季：$qs>>>$qe>>>年：$ys>>>$ye")
//    }
}
fun main(vargs: Array<String>){
    val co = CommUtil()
    val a = co.sha1("888")
    print(a)
}