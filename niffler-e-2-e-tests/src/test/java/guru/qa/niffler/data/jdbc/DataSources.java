package guru.qa.niffler.data.jdbc;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.p6spy.engine.spy.P6DataSource;
import org.apache.commons.lang3.StringUtils;
import org.postgresql.ds.PGSimpleDataSource;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import javax.sql.DataSource;

public class DataSources {
    private DataSources() {
    }

    private static final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();
    private static final Map<String, DataSource> testDataSources = new ConcurrentHashMap<>();

    public static DataSource dataSource(String jdbcUrl) {
        return dataSources.computeIfAbsent(
                jdbcUrl,
                key -> {
                    AtomikosDataSourceBean dsBean = new AtomikosDataSourceBean();
                    final String uniqID = StringUtils.substringAfter(jdbcUrl, "5432/");
                    dsBean.setUniqueResourceName(uniqID);
                    dsBean.setXaDataSourceClassName("org.postgresql.xa.PGXADataSource");
                    Properties props = new Properties();
                    props.put("URL", jdbcUrl);
                    props.put("user", "postgres");
                    props.put("password", "secret");
                    dsBean.setXaProperties(props);
                    dsBean.setPoolSize(3);
                    dsBean.setMaxPoolSize(10);
                    P6DataSource p6DataSource = new P6DataSource(dsBean);
                    try {
                        InitialContext context = new InitialContext();
                        context.bind("java:comp/env/jdbc/" + uniqID, p6DataSource);
                    } catch (NamingException e) {
                        throw new RuntimeException(e);
                    }
                    return p6DataSource;                }
        );
    }

    public static DataSource testDataSource(String jdbcUrl) {
        return testDataSources.computeIfAbsent(
                jdbcUrl,
                key -> {
                    PGSimpleDataSource ds = new PGSimpleDataSource();
                    ds.setUser("postgres");
                    ds.setPassword("secret");
                    ds.setUrl(key);
                    return ds;
                }
        );
    }
}
