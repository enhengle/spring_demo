-- 上下班时间记录表
CREATE TABLE IF NOT EXISTS work_time_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    work_date DATE NOT NULL COMMENT '日期',
    start_time TIME COMMENT '上班时间',
    end_time TIME COMMENT '下班时间',
    is_workday INT DEFAULT 1 COMMENT '是否工作日（1-工作日，0-节假日）',
    work_minutes BIGINT COMMENT '工作时长（分钟）',
    remark VARCHAR(500) COMMENT '备注',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '是否删除（0-未删除，1-已删除）',
    UNIQUE KEY uk_work_date (work_date, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='上下班时间记录表';

-- 创建索引
CREATE INDEX idx_work_date ON work_time_record(work_date);
CREATE INDEX idx_deleted ON work_time_record(deleted);
