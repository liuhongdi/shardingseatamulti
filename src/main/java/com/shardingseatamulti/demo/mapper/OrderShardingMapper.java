package com.shardingseatamulti.demo.mapper;

import com.shardingseatamulti.demo.pojo.OrderSharding;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface OrderShardingMapper {
    int insertOneOrder(OrderSharding orderOne);
    //String selectNameById(String userId);
    List<OrderSharding> selectAllOrder();
}