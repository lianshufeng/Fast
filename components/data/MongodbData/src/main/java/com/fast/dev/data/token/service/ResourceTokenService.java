package com.fast.dev.data.token.service;

public abstract class ResourceTokenService {

    /**
     * 资源令牌
     *
     * @param resourceName
     */
    public abstract Token token(String resourceName);


    /**
     * 资源令牌
     */
    public abstract void token(String resourceName, Runnable runnable);


    /**
     * 资源令牌
     */
    public static abstract class Token {
        public abstract void close();
    }

}
