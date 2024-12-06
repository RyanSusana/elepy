package com.elepy.firebase;

import com.elepy.crud.Crud;
import com.elepy.query.Expression;
import com.elepy.query.Query;
import com.elepy.exceptions.ElepyException;
import com.elepy.schemas.Schema;
import com.elepy.utils.ReflectionUtils;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class FirestoreCrud<T> implements Crud<T> {

    private final Firestore db;

    private final String collection;

    private final Schema<T> schema;
    private final FirestoreFilterFactory<T> filterFactory;

    public FirestoreCrud(Firestore db, Schema<T> schema) {
        this.db = db;
        this.collection = normalizeCollection(schema.getPath());
        this.schema = schema;
        this.filterFactory = new FirestoreFilterFactory<>(schema);
    }


    /**
    * If the collection name starts with a "/", it will be removed.
     * Non-leading "/" characters will be replaced with "_".
    * */
    private String normalizeCollection(String collection) {
        if (collection.startsWith("/")) {
            collection = collection.substring(1);
        }
        return collection.replace("/", "_");
    }
    @Override

    public List<T> find(Query query) {
        com.google.cloud.firestore.Query q = db.collection(collection);

        q = q.where(filterFactory.createFilterFromExpression(query.getExpression()));
        q = q.offset(query.getSkip());
        q = q.limit(query.getLimit());
        return toList(q);
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

            var objects = docs.toObjects(getType());
            return objects;
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
        try {
            var documents = db.collection(collection).where(filterFactory.createFilterFromExpression(expression)).get().get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                document.getReference().delete();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw ElepyException.internalServerError(e);
        }
    }

    @Override
    public long count(Expression query) {
        try {
            AggregateQuery count = db.collection(collection).where(filterFactory.createFilterFromExpression(query)).count();
            return count.get().get().getCount();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return 0;
        } catch (ExecutionException e) {
            throw ElepyException.internalServerError(e);
        }
    }


    @Override
    public Schema<T> getSchema() {
        return schema;
    }


    private String id(T t) {
        return ReflectionUtils.getId(t).orElseThrow(() -> ElepyException.notFound("ID of record")).toString();
    }


    private DocumentReference document(String id) {
        return db.collection(collection).document(id);
    }
}
