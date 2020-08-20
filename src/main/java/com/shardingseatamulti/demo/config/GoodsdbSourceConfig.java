package com.shardingseatamulti.demo.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import io.seata.rm.datasource.DataSourceProxy;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.shardingseatamulti.demo.goodsdb", sqlSessionTemplateRef = "goodsdbSqlSessionTemplate")
public class GoodsdbSourceConfig {

    @Bean(name = "goodsdbDataSource")
    @Primary
    @ConfigurationProperties("spring.datasource.druid.goodsdb")
    public DataSource goodsdbDataSource() {
        System.out.println("init goodsdb datasource");
        return DruidDataSourceBuilder.create().build();
    }



    /*
    @Bean
    @Primary
    public SqlSessionFactory goodsdbSqlSessionFactory(@Qualifier("goodsdbDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mappergoodsdb/*.xml"));
        return bean.getObject();
    }

    @Bean
    @Primary
    public DataSourceTransactionManager goodsdbTransactionManager(@Qualifier("goodsdbDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    @Primary
    public SqlSessionTemplate goodsdbSqlSessionTemplate(@Qualifier("goodsdbSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
    */

    @Bean(name = "goodsdbDataSourceProxy")
    public DataSourceProxy goodsdbDataSourceProxy(@Qualifier("goodsdbDataSource") DataSource dataSource) {
        //DataSourceProxy dsproxy = new DataSourceProxy(dataSource,"my_test_tx_group");
        DataSourceProxy dsproxy = new DataSourceProxy(dataSource,"DEFAULT");
        //dsproxy.

        return dsproxy;
    }

    // 创建SessionFactory
    @Bean(name = "goodsdbSqlSessionFactory")
    public SqlSessionFactory goodsdbSqlSessionFactory(@Qualifier("goodsdbDataSourceProxy") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mappergoodsdb/*.xml"));
        return bean.getObject();
    }

    // 创建事务管理器
    @Bean("goodsdbTransactionManager")
    public DataSourceTransactionManager goodsdbTransactionManger(@Qualifier("goodsdbDataSourceProxy") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    // 创建SqlSessionTemplate
    @Bean(name = "goodsdbSqlSessionTemplate")
    public SqlSessionTemplate goodsdbSqlSessionTemplate(@Qualifier("goodsdbSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }


}