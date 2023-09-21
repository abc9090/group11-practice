package com.example.group11practice.utils;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Service
public abstract class BaseServiceImpl<MType, EntityType extends BaseEntity<IdType>, IdType>
        implements BaseService<MType, IdType> {
    private static Logger logger = LoggerFactory.getLogger(BaseServiceImpl.class);

    @Autowired
    private BaseRepository<EntityType, IdType> repo;

    @Autowired
    private EntityManager entityManager;

    @Override
    public MType findById(IdType id) {
        if (id == null) {
            logger.warn("input id is null, will return null.");
            return null;
        }
        Optional<EntityType> entity = repo.findById(id);
        if (entity.isPresent() && !isLogicDeleted(entity.orElse(null))) {
            return mapBean(entity.orElse(null), getModelType());
        }
        return null;
    }

    private boolean isLogicDeleted(EntityType entityType) {
        return Boolean.TRUE.equals(entityType.getDeleted());
    }

    protected <S, T> T mapBean(S source, Class<T> toType) {
        if (source == null) {
            return null;
        }
        if (getEntityType() == source.getClass() && toType == getModelType()) {
            return (T) mapToModel((EntityType) source);
        }
        return OrikaUtil.map(source, toType);
    }

    protected MType mapToModel(EntityType source) {
        if (source == null) {
            return null;
        }
        MType result = OrikaUtil.map(source, getModelType());
        if (result != null) {
            joinModelOtherInfo(result);
        }
        return result;
    }

    protected <T extends Collection<EntityType>> List<MType> mapToModelList(T sourceList) {
        List<MType> result = new ArrayList<>();
        if (sourceList == null) {
            return result;
        }

        sourceList.forEach(ent -> {
            result.add(mapToModel(ent));
        });
        return result;
    }


    /**
     * entity转换成model后需要结合其他信息时候，请改写本方法。
     * 默认什么都不做。
     *
     * @param resultModel 结果模型列表。不为null。
     */
    protected MType joinModelOtherInfo(MType resultModel) {
        return resultModel;
    }


    protected abstract Class<MType> getModelType();

    protected abstract Class<EntityType> getEntityType();

    @Override
    public List<MType> findAllById(Iterable<IdType> ids) {
        if (CheckUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        List<EntityType> entityList = repo.findAllById(ids);

        return entityList.stream().filter(e -> !isLogicDeleted(e)).map(e -> mapBean(e, getModelType()))
                .collect(Collectors.toList());
    }

    @Override
    public Page<MType> findAll(Specification specification, Pageable pageable) {
        Page<EntityType> entityPage = repo.findAll(specification, pageable);
        List<MType> modelList = entityPage.getContent().stream().
                filter(e -> !isLogicDeleted(e)).map(e -> mapBean(e, getModelType())).collect(Collectors.toList());
        return new PageImpl<>(modelList, pageable, entityPage.getTotalElements());
    }

    @Override
    public List<MType> findAll() {
        List<EntityType> entityList = repo.findAll();

        return entityList.stream().filter(e -> !isLogicDeleted(e)).map(e -> mapBean(e, getModelType()))
                .collect(Collectors.toList());
    }


    @Override
    public IdType insertOne(MType model) throws Group11Exception {
        if (model == null) {
            throw Group11Exception.emptyArgument("model参数不可为空");
        }

        List<IdType> ids = insertInBatch(Arrays.asList(model));
        return ids.isEmpty() ? null : ids.get(0);
    }

    /**
     * 生成主键id并设置到实体中。默认什么都不做，由db生成主键。
     *
     * @param entity
     */
    protected EntityType generateId(EntityType entity) {
        Field genedField = getFieldByAnnotation(entity.getClass(), GeneratedValue.class);
        if (genedField != null) {
            // 自增长id，需要设置主键为null，让系统自动生成。
            entity.setId(null);
        }

        return entity;
    }

    private Field getFieldByAnnotation(Class<?> type, Class<? extends Annotation> annotation) {
        if (type != null) {
            for (Field field : type.getDeclaredFields()) {
                if (field.getAnnotationsByType(annotation).length > 0) {
                    return field;
                }
            }
        }
        return null;

    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<IdType> insertInBatch(Collection<MType> models) throws Group11Exception {
        if (models == null || models.isEmpty()) {
            throw Group11Exception.emptyArgument("models参数不可为空");
        }

        List<EntityType> entityList = new ArrayList<>();
        for (MType model : models) {
            EntityType entity = generateId(mapBean(model, getEntityType()));
            entity.setCreateTime(new Date());
            entity.setUpdateTime(new Date());
            entity.setDeleted(false);
            entityList.add(entity);
        }

        repo.saveAll(entityList);
        repo.flush();
        return entityList.stream().map(e -> e.getId()).collect(Collectors.toList());
    }

    /**
     * 根据id插入或者更新记录
     *
     * @param models
     * @return
     * @throws Group11Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<IdType> upsertInBatch(Collection<MType> models) throws Group11Exception {
        if (models == null || models.isEmpty()) {
            throw Group11Exception.emptyArgument("models参数不可为空");
        }

        List<IdType> result = new ArrayList<>();
        List<MType> insertList = new ArrayList<>();
        List<MType> updateList = new ArrayList<>();
        for (MType model : models) {
            EntityType entity = mapBean(model, getEntityType());
            if (CheckUtil.isEmpty(entity.getId())
                    || !repo.findById(entity.getId()).isPresent()) {
                insertList.add(model);
            } else {
                updateList.add(model);
                result.add(entity.getId());
            }

        }

        if (!insertList.isEmpty()) {
            result.addAll(this.insertInBatch(insertList));
        } else if (!updateList.isEmpty()) {
            this.updateInBatch(updateList);
        }

        return result;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteInBatch(Collection<MType> models) throws Group11Exception {
        if (models == null || models.isEmpty()) {
            throw Group11Exception.emptyArgument("models参数不可为空");
        }

        Map<IdType, EntityType> inputEntityList = new HashMap<>();
        List<IdType> ids = new ArrayList<>();
        for (MType model : models) {
            EntityType entity = mapBean(model, getEntityType());
            if (CheckUtil.isEmpty(entity.getId())) {
                throw Group11Exception.emptyArgument("待逻辑删除的model的id不可为空");
            }
            ids.add(entity.getId());
            inputEntityList.put(entity.getId(), entity);
        }

        /*
         * 重新从db读取，然后过滤逻辑删除，设置逻辑删除标识，保存更新
         */
        List<EntityType> entityList = repo.findAllById(ids);
        entityList = entityList.stream().filter(e -> !isLogicDeleted(e)).collect(Collectors.toList());
        entityList.forEach(e -> {
            e.setDeleted(true);
            e.setUpdateTime(new Date());
            EntityType inputEnt = inputEntityList.get(e.getId());
            if (inputEnt != null) {
                if (CheckUtil.isNotEmpty(inputEnt.getUpdateBy())) {
                    e.setUpdateBy(inputEnt.getUpdateBy());
                }
            }
        });

        // do save
        logger.info("[deleteInBatch]批量删除记录:{}", JSON.toJSONString(ids));
        repo.saveAll(entityList);

    }

    /**
     * 使用原生sql，根据主键批量逻辑删除记录。
     *
     * @param ids      主键id
     * @param updateBy 更新者id
     * @return 更新记录数
     * @throws Group11Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long deleteLogicallyByIds(Collection<IdType> ids, String updateBy) throws Group11Exception {
        if (ids == null || ids.isEmpty()) {
            throw Group11Exception.emptyArgument("ids参数不可为空");
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaUpdate<EntityType> upQuery = cb.createCriteriaUpdate(this.getEntityType());
        Root<EntityType> root = upQuery.from(this.getEntityType());
        upQuery.set("deleted", 1);
        if (CheckUtil.isNotEmpty(updateBy)) {
            upQuery.set("updateBy", updateBy);
        }
        upQuery.set("updateTime", new Date());

        In<Object> ins = cb.in(root.get(getIdFieldName()));
        ids.forEach(val -> ins.value(val));
        upQuery.where(ins);

        // do save
        logger.info("[deleteLogicallyByIds]批量删除记录:{}", JSON.toJSONString(ids));
        return entityManager.createQuery(upQuery).executeUpdate();

    }

    /**
     * 使用原生sql，根据主键批量物理删除记录。
     *
     * @param ids 主键id
     * @return 更新记录数
     * @throws Group11Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long deleteHardByIds(Collection<IdType> ids) throws Group11Exception {
        if (ids == null || ids.isEmpty()) {
            throw Group11Exception.emptyArgument("ids参数不可为空");
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<EntityType> upQuery = cb.createCriteriaDelete(this.getEntityType());
        Root<EntityType> root = upQuery.from(this.getEntityType());

        In<Object> ins = cb.in(root.get(getIdFieldName()));
        ids.forEach(val -> ins.value(val));
        upQuery.where(ins);

        // do save
        logger.info("[deleteHardByIds]批量删除记录:{}", JSON.toJSONString(ids));
        return entityManager.createQuery(upQuery).executeUpdate();
    }

    private String getIdFieldName() {
        Field idField = this.getFieldByAnnotation(getEntityType(), javax.persistence.Id.class);
        return idField == null ? "id" : idField.getName(); // 默认取id字段
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(MType model) {
        if (model == null) {
            throw Group11Exception.emptyArgument("model参数不可为空");
        }

        this.deleteInBatch(Arrays.asList(model));
    }


    @Override
    public Page<MType> findPageByExample(MType example, Pageable pageable) {
        Page<EntityType> rawRs = findEntityPageByExample(example, pageable);
        List<MType> modelList = rawRs.getContent().stream().map(ent -> mapBean(ent, getModelType())).collect(Collectors.toList());
        return new PageImpl<MType>(modelList, rawRs.getPageable(), rawRs.getTotalElements());
    }

    protected Page<EntityType> findEntityPageByExample(MType example, Pageable pageable) {
        if (example == null) {
            throw Group11Exception.emptyArgument("example参数不可为空");
        }

        EntityType entityExample = mapBean(example, getEntityType());
        entityExample.setDeleted(false); // 未删除的

        Example<EntityType> example2 = Example.of(entityExample);
        List<EntityType> rawList;
        long totalCount;
        if (pageable == null) {
            rawList = repo.findAll(example2);
            totalCount = rawList.size();
        } else {
            Page<EntityType> pageData = repo.findAll(example2, pageable);
            rawList = pageData.getContent();
            totalCount = pageData.getTotalElements();
        }

        return new PageImpl<EntityType>(rawList, pageable, totalCount);
    }

    @Override
    public long countByExample(MType example) {
        if (example == null) {
            throw Group11Exception.emptyArgument("example参数不可为空");
        }

        EntityType entityExample = mapBean(example, getEntityType());
        entityExample.setDeleted(false); // 未删除的

        Example<EntityType> example2 = Example.of(entityExample);
        return repo.count(example2);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateById(MType model) throws Group11Exception {
        if (model == null) {
            throw Group11Exception.emptyArgument("model参数不可为空");
        }
        updateInBatch(Arrays.asList(model));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateInBatch(Collection<MType> models) {
        if (CheckUtil.isEmpty(models)) {
            throw Group11Exception.emptyArgument("models参数不可为空");
        }

        Map<IdType, EntityType> inputEntityMap = new HashMap<>();
        List<IdType> ids = new ArrayList<>();
        for (MType model : models) {
            EntityType entity = mapBean(model, getEntityType());
            if (CheckUtil.isEmpty(entity.getId())) {
                throw Group11Exception.emptyArgument("待更新的model的id不可为空: " + JSON.toJSONString(model));
            }
            ids.add(entity.getId());
            inputEntityMap.put(entity.getId(), entity);
        }

        /*
         * 重新从db读取，然后合并更新字段，批量更新
         */
        List<EntityType> entityList = repo.findAllById(ids);
        entityList = entityList.stream().filter(e -> !isLogicDeleted(e)).collect(Collectors.toList());
        for (EntityType entity : entityList) {
            try {
                OrikaUtil.mergeNotNullProperties(entity, inputEntityMap.get(entity.getId()));
            } catch (Exception e1) {
                throw Group11Exception.illegalState("model的非空属性合并到db的entity时发生错误。" + e1.getMessage(), e1);
            }
            entity.setUpdateTime(new Date());

        }
        // do save
        logger.info("[updateInBatch]批量更新记录:{}", JSON.toJSONString(models));
        repo.saveAll(entityList);
        repo.flush();

    }

    /**
     * 物理删除所有记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteAllHardly() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<EntityType> upQuery = cb.createCriteriaDelete(this.getEntityType());
        Root<EntityType> root = upQuery.from(this.getEntityType());

        int count = entityManager.createQuery(upQuery).executeUpdate();
        logger.info("[deleteAllHardly]批量删除记录{}: size={}", getEntityType().getSimpleName(), count);
        return count;
    }

}
