package com.github.lkqm.spring.jdbc;

import java.util.Collection;
import java.util.List;

/**
 * CRUD operations for specific type.
 */
public interface BaseService<T, ID> {

    /**
     * Saves a given entity. Use the returned instance for further operations as the save operation might have changed
     * the entity instance completely.
     */
    void insert(T entity);

    /**
     * Updates the entity ignore null value fields by its id.
     * <p>
     * Notes: entity id value must not be null.
     */
    void update(T entity);

    /**
     * Deletes the entity with the give id.
     */
    long deleteById(ID id);

    /**
     * Deletes the entity with the give id.
     */
    long deleteById(Collection<ID> ids);

    /**
     * Retrieves an entity by its id.
     */
    T findById(ID id);

    /**
     * Retrieves an entity by its id.
     */
    List<T> findById(Collection<ID> ids);

}
