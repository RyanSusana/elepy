package com.elepy.handlers;

public final class ServiceBuilder<T> {
    private FindOneHandler<T> findOne;
    private FindManyHandler<T> findMany;
    private CreateHandler<T> create;
    private UpdateHandler<T> update;
    private DeleteHandler<T> delete;

    public ServiceBuilder() {
        delete = new DefaultDelete<>();
        create = new DefaultCreate<>();
        update = new DefaultUpdate<>();
        findMany = new DefaultFindMany<>();
        findOne = new DefaultFindOne<>();
    }

    public ServiceBuilder<T> defaultFunctionality(ServiceHandler<T> service) {
        return findMany(service).create(service).update(service).delete(service);
    }

    public ServiceBuilder<T> findMany(FindManyHandler<T> find) {
        this.findMany = find;
        return this;
    }

    public ServiceBuilder<T> create(CreateHandler<T> create) {
        this.create = create;
        return this;
    }

    public ServiceBuilder<T> update(UpdateHandler<T> update) {
        this.update = update;
        return this;
    }

    public ServiceBuilder<T> delete(DeleteHandler<T> delete) {
        this.delete = delete;
        return this;
    }

    public ServiceBuilder<T> findOne(FindOneHandler<T> findOne) {
        this.findOne = findOne;
        return this;
    }

    public FinalService<T> build() {
        return new FinalService<>(findMany, findOne, create, update, delete);
    }


}
