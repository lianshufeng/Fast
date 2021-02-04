package com.fast.dev.promise.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseTaskModel extends TaskModel {


    // 响应的model
    private ResponseStatusModel status;


}
