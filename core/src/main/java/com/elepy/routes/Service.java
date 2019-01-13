package com.elepy.routes;

public interface Service<T> extends FindHandler<T>, CreateHandler<T>, UpdateHandler<T>, DeleteHandler<T> {


}
