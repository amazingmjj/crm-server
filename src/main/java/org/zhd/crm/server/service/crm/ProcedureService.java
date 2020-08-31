package org.zhd.crm.server.service.crm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.zhd.crm.server.model.crm.ErrorLog;
import org.zhd.crm.server.repository.crm.ErrorLogRepository;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;

@Service
public class ProcedureService {
    @Autowired
    private ErrorLogRepository logErrRep;

    @Value("${spring.statistic.datasource.url}")
    private String statdbUrl = "";
    @Value("${spring.statistic.datasource.username}")
    private String statUsername = "";
    @Value("${spring.statistic.datasource.password}")
    private String statUserpwd = "";

    private Logger log = LoggerFactory.getLogger(ProcedureService.class);

    private HashMap<String, String> simplePro = new HashMap<String, String>();

    {
        simplePro.put("xy", "{call crm_customer_expt_pro()}");
        simplePro.put("erp", "{call p_erp_dataImport_all()}");
    }

    // 数据库连接

    private Connection getConn() {
        Connection conn = null;
        try {
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            conn = DriverManager.getConnection(statdbUrl, statUsername, statUserpwd);
            return conn;
        } catch (Exception e) {
            log.error("执行存储过程之前连接数据库异常:>>", e);
            saveErrLog("执行存储过程之前连接数据库异常:>>" + e.getMessage());
        }
        return conn;
    }

    // 调用无入参无出参存储过程
    public void callPurePro(String key) {
        Connection conn = getConn();
        try {
            if (conn != null) {
                CallableStatement xycb = getConn().prepareCall(simplePro.get(key));
                xycb.execute();
                conn.close();
                log.info("调用" + key + "存储过程成功");
            }
        } catch (Exception e) {
            log.error("调用" + key + "存储过程失败:>>>", e);
            saveErrLog("调用" + key + "存储过程失败:>>>>" + e.getMessage());
        }
    }

    private void saveErrLog(String content) {
        ErrorLog errlog = new ErrorLog();
        errlog.setContent(content);
        logErrRep.save(errlog);
    }

}
