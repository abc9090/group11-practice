package com.example.group11practice.utils;

import java.util.Date;

public interface BaseEntity<IdType> {
    IdType getId();

    void setId(IdType id);

    Boolean getDeleted();

    void setDeleted(Boolean deleted);

    Date getCreateTime();

    void setCreateTime(Date createTime);

    String getCreateBy();

    void setCreateBy(String createBy);

    Date getUpdateTime();

    void setUpdateTime(Date updateTime);

    String getUpdateBy();

    void setUpdateBy(String updateBy);

}
