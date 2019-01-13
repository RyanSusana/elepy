package com.elepy.routes;

public final class ServiceBuilder<T> {
    private FindHandler<T> find;
    private CreateHandler<T> create;
    private UpdateHandler<T> update;
    private DeleteHandler<T> delete;

    public ServiceBuilder() {
        delete = new DefaultDelete<>();
        create = new DefaultCreate<>();
        update = new DefaultUpdate<>();
        find = new DefaultFind<>();
    }

    public ServiceBuilder find(FindHandler<T> find) {
        this.find = find;
        return this;
    }

    public ServiceBuilder create(CreateHandler<T> create) {
        this.create = create;
        return this;
    }

    public ServiceBuilder update(UpdateHandler<T> update) {
        this.update = update;
        return this;
    }

    public ServiceBuilder delete(DeleteHandler<T> delete) {
        this.delete = delete;
        return this;
    }

    public FinalService<T> build() {
        return new FinalService<>(find, create, update, delete);
    }
}
