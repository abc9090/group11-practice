package com.example.group11practice.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;
import java.util.List;

public interface BaseService<MType, IdType> {

    MType findById(IdType id);

    public Page<MType> findAll(Specification specification, Pageable pageable);

    public List<MType> findAll();

    public List<MType> findAllById(Iterable<IdType> ids);

    Page<MType> findPageByExample(MType example, Pageable pageable);

    long countByExample(MType example);

    IdType insertOne(MType model) throws Group11Exception;

    List<IdType> insertInBatch(Collection<MType> models) throws Group11Exception;

    public void deleteById(MType model);

    public void deleteInBatch(Collection<MType> models) throws Group11Exception;

    void updateById(MType model) throws Group11Exception;

    public void updateInBatch(Collection<MType> models) throws Group11Exception;

    long deleteLogicallyByIds(Collection<IdType> ids, String updateBy) throws Group11Exception;

    long deleteHardByIds(Collection<IdType> ids) throws Group11Exception;

    List<IdType> upsertInBatch(Collection<MType> models) throws Group11Exception;

    int deleteAllHardly();
}

