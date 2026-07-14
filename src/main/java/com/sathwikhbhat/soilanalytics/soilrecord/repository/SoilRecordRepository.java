package com.sathwikhbhat.soilanalytics.soilrecord.repository;

import com.sathwikhbhat.soilanalytics.soilrecord.entity.SoilRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SoilRecordRepository extends MongoRepository<SoilRecord, String> {}
