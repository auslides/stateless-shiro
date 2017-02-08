package org.auslides.security.config;

import javax.sql.DataSource;

import org.auslides.security.repository.DbSetup;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import java.sql.Driver;

@Configuration
@PropertySource(value = { "classpath:datasource.properties" })
@MapperScan("com.ambow.security.mapper")
public class MybatisConfig {
	@Value("${dev.app.jdbc.driver}")
    private String driverClass ;

	@Value("${dev.app.jdbc.url}")
	private String dbUrl ;

	@Bean
	public DataSource dataSource() {
		SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        try {
            dataSource.setDriverClass((Class<Driver>)Class.forName(driverClass));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        dataSource.setUrl(dbUrl);
        dataSource.setUsername("");
		dataSource.setPassword("");

        initDb(dataSource);

		return dataSource;
	}

    private void initDb(DataSource dataSource) {
        try {
            DbSetup.runScript(dataSource, TOKEN_DDL);
        } catch (Exception e) {
            throw new RuntimeException(e) ;
        }
    }

    @Bean
	public PlatformTransactionManager transactionManager() {
		return new DataSourceTransactionManager(dataSource());
	}

	@Bean
	public SqlSessionFactoryBean sqlSessionFactory() throws Exception {
		SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setDataSource(dataSource());
		sessionFactory.setTypeAliasesPackage("org.lanyonm.playground.domain");
		return sessionFactory;
	}

    public static final String TOKEN_DDL = "scripts/schema.sql";

}
