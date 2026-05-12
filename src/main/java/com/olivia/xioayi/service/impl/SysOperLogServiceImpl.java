package com.olivia.xioayi.service.impl;

import com.olivia.xioayi.dao.SysOperLog;
import com.olivia.xioayi.mapper.SysOperLogMapper;
import com.olivia.xioayi.service.SysOperLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SysOperLogServiceImpl implements SysOperLogService {

    private final SysOperLogMapper logMapper;

    @Override
    @Async // 使用异步写入，优化性能
    public void saveLog(SysOperLog log) {
        logMapper.insert(log);
    }
}