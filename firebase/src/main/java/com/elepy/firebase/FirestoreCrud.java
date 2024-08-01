package com.elepy.firebase;

import com.elepy.dao.Crud;
import com.elepy.dao.Expression;
import com.elepy.dao.Query;
import com.elepy.exceptions.ElepyException;
import com.elepy.models.Schema;
import com.elepy.utils.ReflectionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class FirestoreCrud<T> implements Crud<T> {

    private final Firestore db;

    private final ObjectMapper objectMapper;
    private final String collection;

    private final Schema<T> schema;

    public FirestoreCrud(Firestore db, Schema<T> schema) {
        this.db = db;
        this.objectMapper = new ObjectMapper();
        this.collection = schema.getPath();
        this.schema = schema;
    }

    @Override

    // TODO
    public List<T> find(Query query) {

//        if (!StringUtils.isEmpty(query.)) {
//            throw new ElepyException("Firestore does not support table scans");
//        }
//
//
//        com.google.cloud.firestore.Query q = db.collection(collection);
//
//        for (Filter filter : query.getFilters()) {
//            q = FirestoreQueryFactory.getQuery(q, filter, schema);
//        }
//
//        long amountOfResultsWithThatQuery = count(q);
//
//        // Add pagination settings
//        q = q.offset(Math.toIntExact(settings.getPageSize() * settings.getPageNumber())).limit(settings.getPageSize());
//
//        final long remainder = amountOfResultsWithThatQuery % settings.getPageSize();
//        long amountOfPages = amountOfResultsWithThatQuery / settings.getPageSize();
//        if (remainder > 0) amountOfPages++;
//
//        return toList(q);
        return
                null;

    }


    private long count(com.google.cloud.firestore.Query query) {

        query.select(ReflectionUtils.getIdField(getType()).orElseThrow().getName());

        try {
            return query.get().get().size();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw ElepyException.internalServerError(e);
        } catch (ExecutionException e) {
            throw ElepyException.internalServerError(e);
        }
    }

    @Override
    public Optional<T> getById(Serializable id) {
        ApiFuture<DocumentSnapshot> future = document(id.toString()).get();

        try {
            var document = future.get();

            if (document.exists()) {
                return Optional.ofNullable(document.toObject(getType()));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw ElepyException.internalServerError(e);
        }

        return Optional.empty();
    }

    @Override
    public void update(T item) {
        try {
            document(id(item)).set(item).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw ElepyException.internalServerError(e);
        }

    }

    @Override
    public void create(T item) {
        try {
            document(id(item)).set(item).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw ElepyException.internalServerError(e);
        }
    }

    @Override
    public List<T> getAll() {
        return toList(db.collection(collection));
    }

    private List<T> toList(com.google.cloud.firestore.Query query) {
        try {
            var docs = query.get().get();

            return docs.toObjects(getType());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw ElepyException.internalServerError(e);
        }
        return List.of();
    }

    @Override
    public void deleteById(Serializable id) {
        try {
            document(id.toString()).delete().get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw ElepyException.internalServerError(e);
        }
    }

    @Override
    public void delete(Expression expression) {
        // TODO
        
    }

    @Override
    //TODO
    public long count(Query query) {
        return 0;
    }


    @Override
    public Schema<T> getSchema() {
        return schema;
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return null;
    }

    private String id(T t) {
        return ReflectionUtils.getId(t).orElseThrow(() -> ElepyException.notFound("ID of record")).toString();
    }

    private DocumentReference document(String id) {
        return db.collection(collection).document(id);
    }
}
