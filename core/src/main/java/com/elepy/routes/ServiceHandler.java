package com.elepy.routes;

public interface ServiceHandler<T> extends FindHandler<T>, CreateHandler<T>, UpdateHandler<T>, DeleteHandler<T> {


}
