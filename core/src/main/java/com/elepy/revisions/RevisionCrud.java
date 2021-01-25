package com.elepy.revisions;

import com.elepy.auth.User;
import com.elepy.dao.Crud;
import com.elepy.dao.Expression;
import com.elepy.dao.Query;
import com.elepy.exceptions.ElepyException;
import com.elepy.http.HttpContext;
import com.elepy.models.Schema;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class RevisionCrud<T> implements Crud<T> {
    private final Crud<T> crud;

    private final Crud<Revision> revisionCrud;
    private final HttpContext http;
    private final ObjectMapper objectMapper;


    public RevisionCrud(Crud<T> crud, Crud<Revision> revisionCrud, HttpContext http) {
        this.crud = crud;
        this.revisionCrud = revisionCrud;
        this.http = http;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void update(T item) {
        update(List.of(item));
    }

    @Override
    public void update(Iterable<T> items) {
        if (keepRevisions() <= 0) {
            crud.update(items);
        } else {
            final var ids = StreamSupport.stream(items.spliterator(), true)
                    .map(this::getId)
                    .collect(Collectors.toList());

            final var oldRecords = this.getByIds(ids).stream().collect(Collectors.toMap(this::getId, Function.identity()));

            final var newRecords = StreamSupport.stream(items.spliterator(), false).collect(Collectors.toMap(this::getId, Function.identity()));

            if (!oldRecords.keySet().containsAll(newRecords.keySet())) {
                // TODO fix error message, maybe log instead
                throw new ElepyException("Error updating records: old vs new");
            }

            final var revisions = oldRecords.keySet().stream()
                    .map(recordId -> createRevision(recordId.toString(), "Updated", RevisionType.UPDATE, oldRecords.get(recordId), newRecords.get(recordId)))
                    .collect(Collectors.toList());
            crud.update(items);
            revisionCrud.create(revisions);
        }
    }

    @Override
    public void create(T item) {
        if (keepRevisions() <= 0) {
            crud.create(item);
        } else {
            final var revisions = Optional.of(item).stream()
                    .map(record -> createRevision(getId(record), "Created", RevisionType.CREATE, null, record)).collect(Collectors.toList());

            crud.create(item);
            revisionCrud.create(revisions);
        }
    }

    @Override
    public void create(Iterable<T> items) {
        if (keepRevisions() <= 0) {
            crud.create(items);
        } else {
            final var revisions = StreamSupport.stream(items.spliterator(), false)
                    .map(record -> createRevision(getId(record), "Created", RevisionType.CREATE, null, record))
                    .collect(Collectors.toList());

            crud.create(items);
            revisionCrud.create(revisions);
        }
    }

    @Override
    public void deleteById(Serializable id) {
        if (keepRevisions() <= 0) {
            crud.deleteById(id);
        } else {
            final var revisions = this.getById(id)
                    .stream()
                    .map(record -> createRevision(getId(record), "Deleted", RevisionType.DELETE, record, null))
                    .collect(Collectors.toList());
            crud.delete(id);
            revisionCrud.create(revisions);
        }
    }

    @Override
    public void delete(Expression expression) {
        if (keepRevisions() <= 0) {
            crud.delete(expression);
        } else {
            final var revisions = this.find(expression)
                    .stream()
                    .map(record -> createRevision(getId(record), "Deleted", RevisionType.DELETE, record, null))
                    .collect(Collectors.toList());
            crud.delete(expression);
            revisionCrud.create(revisions);
        }
    }

    @Override
    public void delete(Iterable<Serializable> ids) {
        if (keepRevisions() <= 0) {
            crud.delete(ids);
        } else {
            final var revisions = this.getByIds(ids)
                    .stream()
                    .map(record -> createRevision(getId(record), "Deleted", RevisionType.DELETE, record, null))
                    .collect(Collectors.toList());

            crud.delete(ids);
            revisionCrud.create(revisions);
        }
    }


    private Revision createRevision(Serializable recordId,
                                    String description,
                                    RevisionType revisionType,
                                    T oldSnapshot,
                                    T newSnapshot) {

        final var revision = new Revision();

        revision.setId(UUID.randomUUID().toString());
        revision.setRecordId(recordId.toString());
        revision.setSchemaPath(getSchema().getPath());
        revision.setUserId(getUserId());
        revision.setRevisionType(revisionType);
        revision.setDescription(description);
        try {
            if (oldSnapshot != null)
                revision.setOldSnapshot(objectMapper.writeValueAsString(oldSnapshot));
            if (newSnapshot != null)
                revision.setNewSnapshot(objectMapper.writeValueAsString(newSnapshot));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        revision.setTimestamp(Calendar.getInstance().getTime());

        return revision;
    }

    private int keepRevisions() {
        return crud.getSchema().getKeepRevisionsAmount();
    }

    private String getUserId() {
        return http.loggedInUser().map(User::getId).orElse(null);
    }

    // Query methods
    @Override
    public List<T> find(Query query) {
        return crud.find(query);
    }

    @Override
    public List<T> find(Expression expression) {
        return crud.find(expression);
    }

    @Override
    public List<T> findLimited(Integer limit, Expression expression) {
        return crud.findLimited(limit, expression);
    }

    @Override
    public Optional<T> getById(Serializable id) {
        return crud.getById(id);
    }

    @Override
    public List<T> getByIds(Iterable<? extends Serializable> ids) {
        return crud.getByIds(ids);
    }

    @Override
    public List<T> getAll() {
        return crud.getAll();
    }

    @Override
    public Serializable getId(T item) {
        return crud.getId(item);
    }

    @Override
    public long count(Query query) {
        return crud.count(query);
    }

    @Override
    public long count() {
        return crud.count();
    }

    @Override
    public Class<T> getType() {
        return crud.getType();
    }

    @Override
    public Schema<T> getSchema() {
        return crud.getSchema();
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return crud.getObjectMapper();
    }
}
