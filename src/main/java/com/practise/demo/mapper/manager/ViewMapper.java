package com.practise.demo.mapper.manager;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.practise.demo.pojo.manager.View;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lingwang
 * @date 2022/2/19 15:43
 */
@Repository
public interface ViewMapper extends BaseMapper<View> {

    //此处table_name请置换成表名
    @Select("select * from table_name where state=#{state}")
    List<View> findList(@Param("state") int state);
}
