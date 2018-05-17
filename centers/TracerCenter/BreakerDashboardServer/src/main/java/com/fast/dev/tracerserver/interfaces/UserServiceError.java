package com.fast.dev.tracerserver.interfaces;

import org.springframework.stereotype.Component;

@Component
public class UserServiceError implements UserService {

    @Override
    public Object user() {
        return "error";
    }
}
