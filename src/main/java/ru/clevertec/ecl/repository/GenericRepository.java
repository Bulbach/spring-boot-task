package ru.clevertec.ecl.repository;

import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;


public interface GenericRepository<T,K>{

    Collection<T> findAll();

   Optional<T> findById(K id) ;

    T create(T item) ;

    T update(T item) ;

    void delete(K id);

 }
