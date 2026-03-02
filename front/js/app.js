// API基础URL
const API_BASE_URL = 'http://localhost:8080/api/work-time';

// 页面加载时初始化
document.addEventListener('DOMContentLoaded', function() {
    // 设置默认日期为今天
    const today = new Date().toISOString().split('T')[0];
    document.getElementById('workDate').value = today;
    
    // 检查工作日
    checkWorkday(today);
    
    // 监听日期变化
    document.getElementById('workDate').addEventListener('change', function() {
        checkWorkday(this.value);
    });
    
    // 表单提交
    document.getElementById('recordForm').addEventListener('submit', handleSubmit);
    
    // 加载记录列表
    loadRecords();
    
    // 初始化统计筛选界面
    onTimeRangeTypeChange();
    
    // 加载平均工时统计
    loadAverageStats();
});

// 检查是否为工作日
function checkWorkday(date) {
    if (!date) return;
    
    // 这里简化处理，实际应该调用后端API判断
    // 暂时通过日期判断（周末为节假日）
    const dateObj = new Date(date);
    const dayOfWeek = dateObj.getDay();
    const isWeekend = dayOfWeek === 0 || dayOfWeek === 6;
    
    const indicator = document.getElementById('workdayIndicator');
    if (isWeekend) {
        indicator.textContent = '节假日';
        indicator.className = 'workday-indicator holiday';
    } else {
        indicator.textContent = '工作日';
        indicator.className = 'workday-indicator workday';
    }
}

// 处理表单提交
async function handleSubmit(e) {
    e.preventDefault();
    
    const formData = {
        id: document.getElementById('recordId').value || null,
        workDate: document.getElementById('workDate').value,
        startTime: document.getElementById('startTime').value + ':00',
        endTime: document.getElementById('endTime').value ? document.getElementById('endTime').value + ':00' : null,
        remark: document.getElementById('remark').value
    };
    
    if (!formData.startTime) {
        alert('请选择上班时间');
        return;
    }
    
    try {
        const response = await fetch(API_BASE_URL + '/save', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        });
        
        const result = await response.json();
        
        if (result.code === 200) {
            alert('保存成功！');
            resetForm();
            loadRecords();
            loadAverageStats();
        } else {
            alert('保存失败：' + result.message);
        }
    } catch (error) {
        console.error('保存失败:', error);
        alert('保存失败，请检查网络连接');
    }
}

// 重置表单
function resetForm() {
    document.getElementById('recordForm').reset();
    document.getElementById('recordId').value = '';
    const today = new Date().toISOString().split('T')[0];
    document.getElementById('workDate').value = today;
    checkWorkday(today);
}

// 加载记录列表
async function loadRecords() {
    const startDate = document.getElementById('filterStartDate').value;
    const endDate = document.getElementById('filterEndDate').value;
    
    let url = API_BASE_URL + '/list';
    if (startDate && endDate) {
        url = `${API_BASE_URL}/list-by-date?startDate=${startDate}&endDate=${endDate}`;
    }
    
    try {
        const response = await fetch(url);
        const result = await response.json();
        
        const recordList = document.getElementById('recordList');
        
        if (result.code === 200 && result.data && result.data.length > 0) {
            recordList.innerHTML = result.data.map(record => createRecordItem(record)).join('');
        } else {
            recordList.innerHTML = '<div class="empty">暂无记录</div>';
        }
    } catch (error) {
        console.error('加载记录失败:', error);
        document.getElementById('recordList').innerHTML = '<div class="empty">加载失败，请刷新重试</div>';
    }
}

// 创建记录项HTML
function createRecordItem(record) {
    const workDate = formatDate(record.workDate);
    const startTime = record.startTime ? formatTime(record.startTime) : '未设置';
    const endTime = record.endTime ? formatTime(record.endTime) : '未设置';
    const workMinutes = record.workMinutes ? formatWorkMinutes(record.workMinutes) : '未计算';
    const isWorkday = record.isWorkday === 1;
    const badgeClass = isWorkday ? 'workday' : 'holiday';
    const badgeText = isWorkday ? '工作日' : '节假日';
    
    return `
        <div class="record-item">
            <div class="record-header">
                <span class="record-date">${workDate}</span>
                <span class="record-badge ${badgeClass}">${badgeText}</span>
            </div>
            <div class="record-time">
                <span>🕐 上班：${startTime}</span>
                <span>🕐 下班：${endTime}</span>
            </div>
            ${record.workMinutes ? `<div style="color: #666; margin-bottom: 10px;">⏱️ 工作时长：${workMinutes}</div>` : ''}
            ${record.remark ? `<div style="color: #999; font-size: 12px; margin-bottom: 10px;">📝 ${escapeHtml(record.remark)}</div>` : ''}
            <div class="record-footer">
                <div class="record-actions">
                    <button class="btn btn-edit" onclick="editRecord(${record.id})">编辑</button>
                    <button class="btn btn-danger" onclick="deleteRecord(${record.id})">删除</button>
                </div>
            </div>
        </div>
    `;
}

// 编辑记录
async function editRecord(id) {
    try {
        const response = await fetch(API_BASE_URL + '/' + id);
        const result = await response.json();
        
        if (result.code === 200 && result.data) {
            const record = result.data;
            document.getElementById('recordId').value = record.id;
            document.getElementById('workDate').value = record.workDate;
            document.getElementById('startTime').value = record.startTime ? record.startTime.substring(0, 5) : '';
            document.getElementById('endTime').value = record.endTime ? record.endTime.substring(0, 5) : '';
            document.getElementById('remark').value = record.remark || '';
            checkWorkday(record.workDate);
            
            // 滚动到表单
            document.querySelector('.left-panel').scrollIntoView({ behavior: 'smooth' });
        }
    } catch (error) {
        console.error('加载记录失败:', error);
        alert('加载记录失败');
    }
}

// 删除记录
async function deleteRecord(id) {
    if (!confirm('确定要删除这条记录吗？')) {
        return;
    }
    
    try {
        const response = await fetch(API_BASE_URL + '/' + id, {
            method: 'DELETE'
        });
        
        const result = await response.json();
        
        if (result.code === 200) {
            alert('删除成功！');
            loadRecords();
            loadAverageStats();
        } else {
            alert('删除失败：' + result.message);
        }
    } catch (error) {
        console.error('删除失败:', error);
        alert('删除失败，请检查网络连接');
    }
}

// 加载平均工时统计
async function loadAverageStats() {
    const timeRangeType = document.getElementById('timeRangeType').value;
    const timeRangeValue = document.getElementById('timeRangeValue').value;
    const onlyWorkday = document.getElementById('onlyWorkday').checked;
    const excludeOvertime = document.getElementById('excludeOvertime').checked;
    const customStartDate = document.getElementById('customStartDate').value;
    const customEndDate = document.getElementById('customEndDate').value;
    
    // 如果是全部数据且没有其他筛选条件，使用简单接口
    if (timeRangeType === 'all' && !onlyWorkday && !excludeOvertime) {
        try {
            const response = await fetch(API_BASE_URL + '/average');
            const result = await response.json();
            
            if (result.code === 200 && result.data) {
                displayStatsResult(result.data, false);
            }
        } catch (error) {
            console.error('加载统计失败:', error);
            alert('加载统计失败，请检查网络连接');
        }
        return;
    }
    
    // 使用查询接口
    let url = API_BASE_URL + '/average/query?';
    const params = [];
    
    if (timeRangeType && timeRangeType !== 'all') {
        params.push('timeRangeType=' + encodeURIComponent(timeRangeType));
    }
    
    if (timeRangeValue) {
        params.push('timeRangeValue=' + encodeURIComponent(timeRangeValue));
    } else if (timeRangeType === 'week' || timeRangeType === 'month' || timeRangeType === 'year') {
        params.push('timeRangeValue=current');
    }
    
    if (timeRangeType === 'custom') {
        if (customStartDate) {
            params.push('startDate=' + customStartDate);
        }
        if (customEndDate) {
            params.push('endDate=' + customEndDate);
        }
    }
    
    if (onlyWorkday) {
        params.push('onlyWorkday=true');
    }
    
    if (excludeOvertime) {
        params.push('excludeOvertime=true');
    }
    
    url += params.join('&');
    
    try {
        const response = await fetch(url);
        const result = await response.json();
        
        if (result.code === 200 && result.data) {
            displayStatsResult(result.data, excludeOvertime);
        } else {
            alert('查询失败：' + (result.message || '未知错误'));
        }
    } catch (error) {
        console.error('加载统计失败:', error);
        alert('加载统计失败，请检查网络连接');
    }
}

// 显示统计结果
function displayStatsResult(stats, excludeOvertime) {
    // 显示时间范围
    if (stats.timeRange) {
        document.getElementById('timeRangeDisplay').textContent = '📅 ' + stats.timeRange;
        document.getElementById('timeRangeDisplay').style.display = 'block';
    } else {
        document.getElementById('timeRangeDisplay').style.display = 'none';
    }
    
    // 显示统计结果
    document.getElementById('totalRecords').textContent = stats.totalRecords || 0;
    document.getElementById('workdayRecords').textContent = stats.workdayRecords || 0;
    document.getElementById('totalHours').textContent = formatNumber(stats.totalHours || 0) + ' 小时';
    document.getElementById('workdayTotalHours').textContent = formatNumber(stats.workdayTotalHours || 0) + ' 小时';
    document.getElementById('averageHours').textContent = formatNumber(stats.averageHours || 0) + ' 小时';
    document.getElementById('workdayAverageHours').textContent = formatNumber(stats.workdayAverageHours || 0) + ' 小时';
    
    // 显示/隐藏剔除加班相关统计
    if (excludeOvertime && stats.normalAverageHours !== undefined) {
        document.getElementById('normalStatsItem').style.display = 'block';
        document.getElementById('overtimeStatsItem').style.display = 'block';
        document.getElementById('normalAverageHours').textContent = formatNumber(stats.normalAverageHours || 0) + ' 小时';
        document.getElementById('overtimeHours').textContent = formatNumber(stats.overtimeHours || 0) + ' 小时';
    } else {
        document.getElementById('normalStatsItem').style.display = 'none';
        document.getElementById('overtimeStatsItem').style.display = 'none';
    }
}

// 格式化数字（保留2位小数）
function formatNumber(value) {
    if (typeof value === 'number') {
        return value.toFixed(2);
    }
    if (typeof value === 'string') {
        const num = parseFloat(value);
        return isNaN(num) ? '0.00' : num.toFixed(2);
    }
    return '0.00';
}

// 时间范围类型变化
function onTimeRangeTypeChange() {
    const timeRangeType = document.getElementById('timeRangeType').value;
    const timeRangeValueGroup = document.getElementById('timeRangeValueGroup');
    const customDateRangeGroup = document.getElementById('customDateRangeGroup');
    const timeRangeValueLabel = document.getElementById('timeRangeValueLabel');
    
    if (timeRangeType === 'custom') {
        timeRangeValueGroup.style.display = 'none';
        customDateRangeGroup.style.display = 'block';
    } else if (timeRangeType === 'week' || timeRangeType === 'month' || timeRangeType === 'year') {
        timeRangeValueGroup.style.display = 'block';
        customDateRangeGroup.style.display = 'none';
        
        if (timeRangeType === 'week') {
            timeRangeValueLabel.textContent = '周（格式：2025-W01，留空表示当前周）';
        } else if (timeRangeType === 'month') {
            timeRangeValueLabel.textContent = '月（格式：2025-01，留空表示当前月）';
        } else if (timeRangeType === 'year') {
            timeRangeValueLabel.textContent = '年（格式：2025，留空表示当前年）';
        }
    } else {
        timeRangeValueGroup.style.display = 'none';
        customDateRangeGroup.style.display = 'none';
    }
}

// 重置统计筛选
function resetStatsFilter() {
    document.getElementById('timeRangeType').value = 'all';
    document.getElementById('timeRangeValue').value = '';
    document.getElementById('customStartDate').value = '';
    document.getElementById('customEndDate').value = '';
    document.getElementById('onlyWorkday').checked = false;
    document.getElementById('excludeOvertime').checked = false;
    onTimeRangeTypeChange();
    loadAverageStats();
}

// 清空筛选
function clearFilter() {
    document.getElementById('filterStartDate').value = '';
    document.getElementById('filterEndDate').value = '';
    loadRecords();
}

// 格式化日期
function formatDate(dateStr) {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

// 格式化时间
function formatTime(timeStr) {
    if (!timeStr) return '';
    return timeStr.substring(0, 5);
}

// 格式化工作时长
function formatWorkMinutes(minutes) {
    if (!minutes) return '';
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    if (hours > 0) {
        return `${hours}小时${mins}分钟`;
    }
    return `${mins}分钟`;
}

// HTML转义
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// 批量导入
async function batchImport() {
    const csvData = document.getElementById('batchImportData').value.trim();
    
    if (!csvData) {
        alert('请输入要导入的数据');
        return;
    }
    
    const resultDiv = document.getElementById('batchImportResult');
    resultDiv.style.display = 'block';
    resultDiv.innerHTML = '<div class="info">正在导入...</div>';
    resultDiv.className = 'import-result info';
    
    try {
        const response = await fetch(API_BASE_URL + '/batch-import-csv', {
            method: 'POST',
            headers: {
                'Content-Type': 'text/plain'
            },
            body: csvData
        });
        
        const result = await response.json();
        
        if (result.code === 200 && result.data) {
            const importResult = result.data;
            displayImportResult(importResult);
            
            // 刷新记录列表和统计
            loadRecords();
            loadAverageStats();
        } else {
            resultDiv.innerHTML = `<div class="error">导入失败：${result.message || '未知错误'}</div>`;
            resultDiv.className = 'import-result error';
        }
    } catch (error) {
        console.error('批量导入失败:', error);
        resultDiv.innerHTML = '<div class="error">导入失败，请检查网络连接</div>';
        resultDiv.className = 'import-result error';
    }
}

// 显示导入结果
function displayImportResult(result) {
    const resultDiv = document.getElementById('batchImportResult');
    
    let html = '<h4>导入完成</h4>';
    html += '<div class="result-stats">';
    html += `<span>总记录数：${result.totalCount || 0}</span>`;
    html += `<span style="color: #28a745;">成功：${result.successCount || 0}</span>`;
    html += `<span style="color: #dc3545;">失败：${result.failCount || 0}</span>`;
    html += '</div>';
    
    if (result.errors && result.errors.length > 0) {
        html += '<div class="error-list">';
        html += '<strong>失败详情：</strong>';
        result.errors.forEach(error => {
            html += `<div class="error-item">第 ${error.row} 行：${escapeHtml(error.message)}</div>`;
        });
        html += '</div>';
    }
    
    resultDiv.innerHTML = html;
    
    if (result.failCount > 0) {
        resultDiv.className = 'import-result error';
    } else {
        resultDiv.className = 'import-result success';
    }
    
    // 如果全部成功，3秒后自动隐藏结果
    if (result.failCount === 0) {
        setTimeout(() => {
            resultDiv.style.display = 'none';
            clearBatchImport();
        }, 3000);
    }
}

// 清空批量导入数据
function clearBatchImport() {
    document.getElementById('batchImportData').value = '';
    document.getElementById('batchImportResult').style.display = 'none';
}
