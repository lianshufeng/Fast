package com.fast.dev.timercenter.service.model;

import com.fast.dev.timercenter.service.type.TaskState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ResponseTaskModel extends TaskModel {


    // 响应的model
    private ResponseStatusModel status;



}
