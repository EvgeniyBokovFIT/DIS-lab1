package com.example.manager.repository;

import com.example.manager.model.CrackEntity;

import java.util.Map;
import java.util.Optional;

public interface CrackHashRepository {
    Map<String, CrackEntity> getCrackEntities();

    void save(String is, CrackEntity crackEntity);

    Optional<CrackEntity> getById(String id);
}
