package com.itxiaoqi.userservice.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OperationLog implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String operation;
    private String ip;
    private Long operatorId;
    private String requestParams;
    private Boolean operationStatus;
    private String errorMessage;
    private LocalDateTime createTime;
}