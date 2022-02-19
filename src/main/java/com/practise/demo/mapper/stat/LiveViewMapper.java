package com.practise.demo.mapper.stat;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.practise.demo.pojo.stat.LiveView;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author lingwang
 * @date 2022/2/19 15:43
 */
@Repository
@DS("stat")//请不要忘记该注解，否则切换数据源失效
public interface LiveViewMapper extends BaseMapper<LiveView> {

    //此处table_name请置换成表名
    @Select("select * from table_name")
    List<LiveView> findAll(IPage page);

}
