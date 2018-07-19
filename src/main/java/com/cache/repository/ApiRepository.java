package com.cache.repository;

import com.cache.model.entity.ObjectKeyValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface ApiRepository extends JpaRepository<ObjectKeyValue,String> {
        }
