package com.my.WorkTimeApp.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class WorkTime {

    private Long id;

    private Long EmployeeId;

    
    private LocalDateTime beginningOfWork;

    private LocalDateTime endOfWork;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmployeeId() {
        return EmployeeId;
    }

    public void setEmployeeId(Long EmployeeId) {
        this.EmployeeId = EmployeeId;
    }

    public LocalDateTime getBeginningOfWork() {
        return beginningOfWork;
    }

    public void setBeginningOfWork(LocalDateTime beginningOfWork) {
        this.beginningOfWork = beginningOfWork;
    }

    public LocalDateTime getEndOfWork() {
        return endOfWork;
    }

    public void setEndOfWork(LocalDateTime endOfWork) {
        this.endOfWork = endOfWork;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
