package net.whydah.uss.util.repository;

import java.util.Set;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;

public class CountQueryHelper<T> {

    final Class<T> typeParameterClass;

    public CountQueryHelper(Class<T> typeParameterClass) {
        this.typeParameterClass = typeParameterClass;
    }

    public CriteriaQuery<Long> getCountQuery(CriteriaQuery<T> originalQuery, EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        // create count query
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);


        // start copying root/joins/restrictions from  the original query

        // copy roots
        for (Root r : originalQuery.getRoots()) {
            Root root = countQuery.from(r.getModel());
            root.alias(r.getAlias());
        }

        // copy joins
        for (Root r : originalQuery.getRoots()) {
            Set<Join<T, ?>> joins = r.getJoins();
            for (Join<T, ?> join : joins) {
                for (Root countRoot : countQuery.getRoots()) {
                    try {
                        Join joinOnCount = countRoot.join(join.getAttribute().getName());
                        joinRecursive(joinOnCount, join);
                    } catch (IllegalArgumentException e) {
                        // attribute does not exist on this root
                    }
                }
            }
        }

        countQuery.select(cb.count(countQuery.from(this.typeParameterClass)));

        //  copy restrictions
        if (originalQuery.getRestriction() != null) {
            countQuery.where(originalQuery.getRestriction());
        }

        return countQuery;
    }

    private void joinRecursive(Join countJoins, Join<T, ?> originalJoin) {
        for(Join original : originalJoin.getJoins()) {
            Join<Object, Object> childJoin = countJoins.join(original.getAttribute().getName());
            joinRecursive(childJoin, original);
        }
    }
}