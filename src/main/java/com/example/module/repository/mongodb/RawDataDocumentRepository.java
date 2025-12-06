package com.example.module.repository.mongodb;

import com.example.module.entity.mongodb.RawDataDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RawDataDocumentRepository extends MongoRepository<RawDataDocument, String> {
    
    Optional<RawDataDocument> findByRawDataId(Long rawDataId);
    
    List<RawDataDocument> findByDataType(String dataType);
    
    @Query("{ 'metadata.?0' : ?1 }")
    List<RawDataDocument> findByMetadataKeyValue(String key, Object value);
    
    List<RawDataDocument> findByDataNameContainingIgnoreCase(String dataName);
    
    void deleteByRawDataId(Long rawDataId);
}