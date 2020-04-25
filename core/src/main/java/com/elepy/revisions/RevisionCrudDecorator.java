package com.elepy.revisions;

import com.elepy.dao.Crud;
import com.elepy.models.Schema;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.*;

public class RevisionCrudDecorator<T> {
    private Crud<T> crud;
    private Crud<Revision> revisions;

    private ObjectMapper objectMapper;


    public void delete(Iterable<Serializable> ids) {
        crud.delete(ids);
    }

    public void deleteById(Serializable id) {
        crud.deleteById(id);
    }

    public void delete(Serializable... ids) {
        crud.delete(ids);
    }

    public void update(T item) {
        crud.update(item);
    }

    public void updateWithPrototype(Map<String, Object> prototype, Serializable... ids) {
        crud.updateWithPrototype(prototype, ids);
    }

    public void update(Iterable<T> items) {
        crud.update(items);
    }

    public void create(T item) {
        crud.create(item);
    }

    private Revision createRevision(T record) throws JsonProcessingException {
        final var revision = new Revision();

        revision.setSchemaPath(getSchema().getPath());
        revision.setRecordSnapshot(objectMapper.writeValueAsString(record));

        revision.setRevisionNumber(0);
        revision.setTimestamp(Calendar.getInstance().getTime());
        return revision;
    }


    // Read operations

    public Optional<T> getById(Serializable id) {
        return crud.getById(id);
    }

    public List<T> getByIds(Iterable<? extends Serializable> ids) {
        return crud.getByIds(ids);
    }


    public List<T> getAll() {
        return crud.getAll();
    }

    public Serializable getId(T item) {
        return crud.getId(item);
    }



    public long count() {
        return crud.count();
    }

    public Class<T> getType() {
        return crud.getType();
    }

    public Schema<T> getSchema() {
        return crud.getSchema();
    }

    public ObjectMapper getObjectMapper() {
        return crud.getObjectMapper();
    }
}
