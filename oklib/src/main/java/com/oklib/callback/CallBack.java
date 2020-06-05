package com.oklib.callback;

import org.jetbrains.annotations.Nullable;

import okhttp3.Request;
import okhttp3.Response;

public interface CallBack<T, E> {

    /**
     * UI Thread
     * 请求Request在build（）前执行，此处可自主添加本次请求header
     *
     * @param builder request相关的操作
     */
    void onBefore(Request.Builder builder);

    /**
     * 非UI Thread
     *
     * @param response
     * @param extra    该字段可能null， 接口提供向下传递字段 此字段在ReQuest中赋值
     * @return
     * @throws Exception
     */
    T onParse(Response response, @Nullable E extra) throws Exception;

    /**
     * UI Thread
     *
     * @param progress 当前进度
     * @param total    总进度
     */
    void onProgress(float progress, long total);

    /**
     * UI Thread
     *
     * @param result
     */
    void onResponse(T result);

    /**
     * UI Thread
     *
     * @param e
     */
    void onError(Exception e);

    /**
     * UI Thread
     */
    void onAfter();


}