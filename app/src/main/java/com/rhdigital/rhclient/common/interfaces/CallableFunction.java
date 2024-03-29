package com.rhdigital.rhclient.common.interfaces;

import com.google.android.gms.tasks.Continuation;

import java.util.concurrent.Callable;

public interface CallableFunction<T, R> {

  public abstract R call(T... args) throws Exception;

  public static <T, R> Callable<R> callable(CallableFunction<T, R> callableFunction, T... args) {
    return () -> callableFunction.call(args);
  }

  public abstract R then(T... args) throws Exception;

  public static <T, R> Continuation<T, R> continuation(CallableFunction<T, R> callableFunction, T... args) {
    return (tTask) -> callableFunction.then(args);
  }
}
