package com.shardingseatamulti.demo;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import io.seata.spring.annotation.datasource.EnableAutoDataSourceProxy;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

//@SpringBootApplication
//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,DruidDataSourceAutoConfigure.class})
@SpringBootApplication(exclude = {DruidDataSourceAutoConfigure.class})
//@MapperScan("com.shardingseatamulti.demo.mapper")
//@MapperScan("com.shardingseatamulti.demo.goodsdb")
//@EnableAutoDataSourceProxy
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
