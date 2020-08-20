package com.shardingseatamulti.demo.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.github.pagehelper.PageHelper;
import com.shardingseatamulti.demo.algorithm.DatabasePreciseShardingAlgorithm;
import com.shardingseatamulti.demo.algorithm.OrderTablePreciseShardingAlgorithm;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@MapperScan(basePackages = "com.shardingseatamulti.demo.mapper", sqlSessionFactoryRef = "shardingSqlSessionFactory")
public class ShardingDataSourceConfig {

    //分表算法
    @Resource
    private OrderTablePreciseShardingAlgorithm orderTablePreciseShardingAlgorithm;

    //分库算法
    @Resource
    private DatabasePreciseShardingAlgorithm databasePreciseShardingAlgorithm;

    //第一个订单库
    @Bean(name = "saleorder01")
    @ConfigurationProperties(prefix = "spring.datasource.druid.saleorder01")
    public DataSource saleorder01(){
        return DruidDataSourceBuilder.create().build();
    }

    //第二个订单库
    @Bean(name = "saleorder02")
    @ConfigurationProperties(prefix = "spring.datasource.druid.saleorder02")
    public DataSource saleorder02(){
        return DruidDataSourceBuilder.create().build();
    }

    //创建数据源，需要把分库的库都传递进去
    //@Bean("dataSource")

    //@Bean("dataSource")
    @Bean("dataSource")
    public DataSource dataSource(@Qualifier("saleorder01") DataSource saleorder01,@Qualifier("saleorder02") DataSource saleorder02) throws SQLException {

        System.out.println("init sharding datasource");
        // 配置真实数据源
        Map<String, DataSource> dataSourceMap = new HashMap<String, DataSource>();
        dataSourceMap.put("saleorder01", saleorder01);
        dataSourceMap.put("saleorder02", saleorder02);
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        //如果有多个数据表需要分表，依次添加到这里
        shardingRuleConfig.getTableRuleConfigs().add(getOrderTableRuleConfiguration());
        // shardingRuleConfig.getTableRuleConfigs().add(getOrderItemTableRuleConfiguration());
        Properties p = new Properties();
        p.setProperty("sql.show", Boolean.TRUE.toString());
        // 获取数据源对象
        DataSource dataSource = ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig,p);
        return dataSource;
    }

    // 创建SessionFactory
    @Bean(name = "shardingSqlSessionFactory")
    public SqlSessionFactory shardingSqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));
        return bean.getObject();
    }

    // 创建事务管理器
    @Bean("shardingTransactionManger")
    public DataSourceTransactionManager shardingTransactionManger(@Qualifier("dataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    // 创建SqlSessionTemplate
    @Bean(name = "shardingSqlSessionTemplate")
    public SqlSessionTemplate shardingSqlSessionTemplate(@Qualifier("shardingSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    //订单表的分表规则配置
    private TableRuleConfiguration getOrderTableRuleConfiguration() {
        TableRuleConfiguration result = new TableRuleConfiguration("t_order","saleorder01.t_order_$->{1..2},saleorder02.t_order_$->{3..4}");
        //result.setDatabaseShardingStrategyConfig(getDatabaseStrategyConfiguration());
        result.setDatabaseShardingStrategyConfig(new StandardShardingStrategyConfiguration("orderId",databasePreciseShardingAlgorithm));
        //result.setTableShardingStrategyConfig(getStrategyConfiguration());
        result.setTableShardingStrategyConfig(new StandardShardingStrategyConfiguration("orderId",orderTablePreciseShardingAlgorithm));
        return result;
    }

    //分页
    @Bean(name="pageHelper")
    public PageHelper getPageHelper() {
        PageHelper pageHelper = new PageHelper();
        Properties properties = new Properties();
        properties.setProperty("reasonable", "true");
        properties.setProperty("supportMethodsArguments", "true");
        properties.setProperty("returnPageInfo", "true");
        properties.setProperty("params", "count=countSql");
        pageHelper.setProperties(properties);
        return pageHelper;
    }
}