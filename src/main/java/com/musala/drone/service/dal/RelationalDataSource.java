/**
 *
 */
package com.musala.drone.service.dal;

import static com.musala.drone.service.crypto.CryptoService.ENCRYPTED_INDICATOR;

import java.sql.Connection;
import java.sql.SQLException;

import org.json.JSONObject;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.musala.drone.service.config.Configuration;
import com.musala.drone.service.crypto.JasyptUtils;

/**
 * @author ADM_AMAGBAJE
 *
 */
public class RelationalDataSource {

    /**
     * this is the datasource connector for the JDBC. the connector here is
     * referred to within the code every where access is needed for the
     * Database(postgres).
     */
    private RelationalDataSource () {
    	
    }
    private static final HikariConfig config = new HikariConfig();
    private static HikariDataSource ds = null;

    static {

        JSONObject dbconfig = Configuration.getServiceConfig().getJSONObject("dbConfiguration");

        String databaseServer = dbconfig.getString("dbHost");
        String databaseName = dbconfig.getString("dbName");
        int databaseServerPort = dbconfig.getInt("dbPort");
        String jdbcURL = "jdbc:postgresql://" + databaseServer + ":" + databaseServerPort + "/" + databaseName;
        String userName = dbconfig.getString("dbUsername");
        String password = dbconfig.getString("dbPassword");
        if (password.startsWith(ENCRYPTED_INDICATOR)) {
            password = JasyptUtils.decrypt(password.replaceFirst(ENCRYPTED_INDICATOR, ""));
        }

        config.setJdbcUrl(jdbcURL);
        config.setUsername(userName);
        config.setPassword(password);
        config.addDataSourceProperty("cachePrepStmts", true);
        config.addDataSourceProperty("prepStmtCacheSize", 250);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);

        ds = new HikariDataSource(config);

    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

}
