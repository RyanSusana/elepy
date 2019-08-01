package com.elepy.handlers;

public interface ServiceHandler<T> extends FindManyHandler<T>, FindOneHandler<T>, CreateHandler<T>, UpdateHandler<T>, DeleteHandler<T> {


}
