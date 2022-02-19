package com.practise.demo.dataJob;

import com.practise.demo.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * @author lingwang
 * @date 2021/3/15 20:02
 * 定时任务的业务层
 */
@Service
public class HomeManager {

    private static Logger logger = LoggerFactory.getLogger(HomeManager.class);

    public void deleteHomeNewsData() {
        File file = new File("logs");
        File[] files = file.listFiles();
        String date = DateUtil.getDayMonthEnd();//date为null的处理
        if (date != null) {
            for (File file1 : files) {
                String[] fileName = file1.getName().split("_");
                if (date.equals(fileName[0])) {
                    logger.info("删除文件：" + file1);
                    file1.delete();
                }
            }
        }
        return;
    }
}
