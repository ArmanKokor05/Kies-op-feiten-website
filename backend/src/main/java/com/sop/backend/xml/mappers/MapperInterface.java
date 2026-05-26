package com.sop.backend.xml.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.sop.backend.models.BaseEntity;
import java.util.List;

public interface MapperInterface<T extends BaseEntity> {
    List<T> assembleModel (JsonNode jsonNode);
}
