package com.example.manager.repository;

import com.example.manager.model.CrackEntity;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class CrackHashRepositoryImpl implements CrackHashRepository{
    private final Map<String, CrackEntity> crackEntities = new ConcurrentHashMap<>();

    @Override
    public Map<String, CrackEntity> getCrackEntities() {
        return crackEntities;
    }

    @Override
    public void save(String id, CrackEntity crackEntity) {
        crackEntities.put(id, crackEntity);
    }

    @Override
    public Optional<CrackEntity> getById(String id) {
        if(crackEntities.containsKey(id)) {
            return Optional.of(crackEntities.get(id));
        }

        return Optional.empty();
    }
}
