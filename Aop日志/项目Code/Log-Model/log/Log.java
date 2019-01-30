package org.cnbi.utils.log;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by FangQiang on 2019/1/29
 */
@Getter
@Setter
public class Log implements Serializable {

    private String id;

    private int type;

    private Long executedTime;

    private String ip;

    private String operator;

    private String browser;

    private String description;

    private int level;

    private String className;

    private String methodName;

    private Long createTime;

    private String args;

    private String exceptionClass;

    private String exceptionDetail;

    @Override
    public String toString() {
        return "Log{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", executedTime=" + executedTime +
                ", ip='" + ip + '\'' +
                ", operator='" + operator + '\'' +
                ", browser='" + browser + '\'' +
                ", description='" + description + '\'' +
                ", level='" + level + '\'' +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", createTime=" + createTime +
                ", args='" + args + '\'' +
                ", exceptionClass='" + exceptionClass + '\'' +
                ", exceptionDetail='" + exceptionDetail + '\'' +
                '}';
    }
}
