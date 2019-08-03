package com.elepy.mongo;

import com.elepy.dao.Crud;
import com.elepy.exceptions.ElepyException;
import com.elepy.id.DefaultIdentityProvider;
import com.elepy.id.IdentityProvider;
import com.elepy.utils.ReflectionUtils;
import org.bson.types.ObjectId;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class ElepyIdUpdater implements org.jongo.ObjectIdUpdater {

    private final Crud crud;
    private IdentityProvider identityProvider;

    public ElepyIdUpdater(Crud crud) {
        this.crud = crud;
    }


    @Override
    public boolean mustGenerateObjectId(Object pojo) {
    	Optional<Serializable> idFieldOptional = ReflectionUtils.getId(pojo);
    	
    	boolean generateId = idFieldOptional.map(o -> (o instanceof String && ((String) o).trim().isEmpty()) || (o instanceof Long && ((Long) o) == Long.MIN_VALUE) || (o instanceof Integer && ((Integer) o) == Integer.MIN_VALUE)).orElse(true);
    	
    	return generateId;
    }

    @Override
    public Object getId(Object pojo) {
        return ReflectionUtils.getId(pojo).orElseThrow(() -> new ElepyException("No ID found"));
    }

    @Override
    public void setObjectId(Object target, ObjectId id) {
        try {
            provider(target).provideId(target, crud);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    private IdentityProvider provider(Object item) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        if (identityProvider == null) {
            ReflectionUtils.getIdField(item.getClass()).orElseThrow(() -> new ElepyException("No ID found"));
            final com.elepy.annotations.IdProvider annotation = item.getClass().getAnnotation(com.elepy.annotations.IdProvider.class);

            if (annotation != null) {
                final Optional<Constructor<?>> o = ReflectionUtils.getEmptyConstructor(annotation.value());
                if (!o.isPresent()) {
                    throw new IllegalStateException(annotation.value() + " has no empty constructor.");
                }
                return ((Constructor<IdentityProvider>) o.get()).newInstance();

            } else {
                return new DefaultIdentityProvider();
            }
        }
        return identityProvider;

    }
}
