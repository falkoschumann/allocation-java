/*
 * Allocation
 * Copyright (c) 2023 Falko Schumann <falko.schumann@muspellheim.de>
 */

package de.muspellheim.allocation.unit;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.LockModeType;
import jakarta.persistence.Query;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.metamodel.Metamodel;
import java.util.List;
import java.util.Map;

class FakeEntityManager implements EntityManager {
  private final FakeEntityTransaction transaction = new FakeEntityTransaction();

  @Override
  public void persist(Object entity) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T> T merge(T entity) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void remove(Object entity) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T> T find(Class<T> entityClass, Object primaryKey) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T> T find(
      Class<T> entityClass,
      Object primaryKey,
      LockModeType lockMode,
      Map<String, Object> properties) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T> T getReference(Class<T> entityClass, Object primaryKey) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void flush() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setFlushMode(FlushModeType flushMode) {
    throw new UnsupportedOperationException();
  }

  @Override
  public FlushModeType getFlushMode() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void lock(Object entity, LockModeType lockMode) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void lock(Object entity, LockModeType lockMode, Map<String, Object> properties) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void refresh(Object entity) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void refresh(Object entity, Map<String, Object> properties) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void refresh(Object entity, LockModeType lockMode) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void refresh(Object entity, LockModeType lockMode, Map<String, Object> properties) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void detach(Object entity) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean contains(Object entity) {
    throw new UnsupportedOperationException();
  }

  @Override
  public LockModeType getLockMode(Object entity) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setProperty(String propertyName, Object value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Map<String, Object> getProperties() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query createQuery(String qlString) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query createQuery(CriteriaUpdate updateQuery) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query createQuery(CriteriaDelete deleteQuery) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query createNamedQuery(String name) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query createNativeQuery(String sqlString) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query createNativeQuery(String sqlString, Class resultClass) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Query createNativeQuery(String sqlString, String resultSetMapping) {
    throw new UnsupportedOperationException();
  }

  @Override
  public StoredProcedureQuery createNamedStoredProcedureQuery(String name) {
    throw new UnsupportedOperationException();
  }

  @Override
  public StoredProcedureQuery createStoredProcedureQuery(String procedureName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public StoredProcedureQuery createStoredProcedureQuery(
      String procedureName, Class... resultClasses) {
    throw new UnsupportedOperationException();
  }

  @Override
  public StoredProcedureQuery createStoredProcedureQuery(
      String procedureName, String... resultSetMappings) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void joinTransaction() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isJoinedToTransaction() {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T> T unwrap(Class<T> cls) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object getDelegate() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void close() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isOpen() {
    throw new UnsupportedOperationException();
  }

  @Override
  public EntityTransaction getTransaction() {
    return transaction;
  }

  @Override
  public EntityManagerFactory getEntityManagerFactory() {
    throw new UnsupportedOperationException();
  }

  @Override
  public CriteriaBuilder getCriteriaBuilder() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Metamodel getMetamodel() {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T> EntityGraph<T> createEntityGraph(Class<T> rootType) {
    throw new UnsupportedOperationException();
  }

  @Override
  public EntityGraph<?> createEntityGraph(String graphName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public EntityGraph<?> getEntityGraph(String graphName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> entityClass) {
    throw new UnsupportedOperationException();
  }

  public boolean isCommitted() {
    return transaction.committed;
  }

  private static class FakeEntityTransaction implements EntityTransaction {
    private boolean committed;

    @Override
    public void begin() {}

    @Override
    public void commit() {
      committed = true;
    }

    @Override
    public void rollback() {}

    @Override
    public void setRollbackOnly() {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean getRollbackOnly() {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean isActive() {
      throw new UnsupportedOperationException();
    }
  }
}
