package guru.qa.niffler.data.templates;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import javax.sql.DataSource;

public class DataSources {
    private DataSources() {
    }

    private static final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();

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
                    return dsBean;
                }
        );
    }
}
