package com.godaddy.evapi.service;

import java.util.Date;

import com.godaddy.evapi.model.LogModel;

public interface ILoggingService {
    boolean insertLog(LogModel logEntry);
    LogModel fetchLog(String id);
    LogModel fetchLogs(Date startTime, Date endTime);
    LogModel fetchLogsByCA(String ca, Date startTime, Date endTime);
    LogModel fetchLogsByEndpoint(String endpoint, Date startTime, Date endTime);
    LogModel fetchLogsByEnpdointAndOperation(String endpoint, String operation, Date startTime, Date endTime);

}
