package com.sathwikhbhat.soilanalytics.dashboard;

import com.sathwikhbhat.soilanalytics.entity.SoilRecord;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class DashboardRepository {

    private final MongoTemplate mongoTemplate;

    public DashboardRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public long getTotalSamples() {
        return mongoTemplate.count(new Query(), SoilRecord.class);
    }

    public long getDistrictCoverage() {
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.group("location.district"));
        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, SoilRecord.class, Document.class);
        return results.getMappedResults().size();
    }

    public long getCropCoverage() {
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.group("crop"));
        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, SoilRecord.class, Document.class);
        return results.getMappedResults().size();
    }
}
