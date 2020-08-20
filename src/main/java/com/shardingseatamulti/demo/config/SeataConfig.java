package com.shardingseatamulti.demo.config;

import io.seata.spring.annotation.GlobalTransactionScanner;
import io.seata.spring.annotation.GlobalTransactionalInterceptor;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Aspect
@Configuration
public class SeataConfig {
    private static final String AOP_POINTCUT_EXPRESSION = "@annotation(io.seata.spring.annotation.GlobalTransactional)";

    @Value("${seata.txServiceGroup}")
    private String txServiceGroup;

    @Bean
    public GlobalTransactionalInterceptor globalTransactionalInterceptor(){
        System.out.println("globalTransactionalInterceptor");
        GlobalTransactionalInterceptor globalTransactionalInterceptor = new GlobalTransactionalInterceptor(null);
        return globalTransactionalInterceptor;
    }

    @Bean
    public GlobalTransactionScanner getGlobalTransactionScanner() {
        return new GlobalTransactionScanner("performance", txServiceGroup);
    }

    @Bean
    public Advisor seataAdviceAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(AOP_POINTCUT_EXPRESSION);
        return new DefaultPointcutAdvisor(pointcut,globalTransactionalInterceptor());
    }
}