package com.practise.demo.repository.manager;

import com.practise.demo.pojo.manager.View;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lingwang
 * @date 2022/2/19 15:43
 */
@Repository
public interface ViewRepository extends JpaRepository<View, String> {

    //此处table_name请置换成表名
    @Query(value = "select * from table_name where state=:state", nativeQuery = true)
    List<View> findList(@Param("state") int state);
}
