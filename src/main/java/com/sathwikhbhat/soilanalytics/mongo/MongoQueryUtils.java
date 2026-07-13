package com.sathwikhbhat.soilanalytics.mongo;

import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Query;

public final class MongoQueryUtils {

    private MongoQueryUtils() {}

    public static Query createQuery() {
        Query query = new Query();
        query.collation(Collation.of("en").strength(Collation.ComparisonLevel.secondary()));
        return query;
    }
}
