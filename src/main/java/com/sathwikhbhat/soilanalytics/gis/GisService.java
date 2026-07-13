package com.sathwikhbhat.soilanalytics.gis;

import com.sathwikhbhat.soilanalytics.entity.SoilRecord;
import com.sathwikhbhat.soilanalytics.gis.dto.GisFilterRequest;
import com.sathwikhbhat.soilanalytics.gis.dto.GisHeatMapResponse;
import com.sathwikhbhat.soilanalytics.gis.dto.GisMarkerResponse;
import com.sathwikhbhat.soilanalytics.mongo.MongoQueryUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class GisService {

    private final MongoTemplate mongoTemplate;

    public GisService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<GisMarkerResponse> getMarkers(GisFilterRequest filters) {
        Query query = buildQuery(filters);

        List<SoilRecord> records = mongoTemplate.find(query, SoilRecord.class);

        return records.stream().map(this::toMapMarkerResponse).toList();
    }

    public List<GisHeatMapResponse> getHeatMap(GisFilterRequest filters) {
        Query query = buildQuery(filters);

        List<SoilRecord> records = mongoTemplate.find(query, SoilRecord.class);

        Map<LocationKey, Integer> counts = new HashMap<>();

        for (SoilRecord record : records) {
            LocationKey key = new LocationKey(
                    record.getLocation().latitude(), record.getLocation().longitude());
            counts.merge(key, 1, Integer::sum);
        }

        int total = records.size();

        List<GisHeatMapResponse> response = new ArrayList<>();

        for (var entry : counts.entrySet()) {
            LocationKey key = entry.getKey();
            int count = entry.getValue();

            double intensity = (count * 100.0) / total;

            response.add(new GisHeatMapResponse(key.latitude(), key.longitude(), intensity));
        }

        return response;
    }

    private Query buildQuery(GisFilterRequest filters) {
        Query query = MongoQueryUtils.createQuery();

        if (filters.state() != null && !filters.state().isBlank()) {
            query.addCriteria(Criteria.where("location.state").is(filters.state()));
        }

        if (filters.district() != null && !filters.district().isBlank()) {
            query.addCriteria(Criteria.where("location.district").is(filters.district()));
        }

        if (filters.taluk() != null && !filters.taluk().isBlank()) {
            query.addCriteria(Criteria.where("location.taluk").is(filters.taluk()));
        }

        if (filters.village() != null && !filters.village().isBlank()) {
            query.addCriteria(Criteria.where("location.village").is(filters.village()));
        }

        if (filters.crop() != null && !filters.crop().isBlank()) {
            query.addCriteria(Criteria.where("crop").is(filters.crop()));
        }

        return query;
    }

    private GisMarkerResponse toMapMarkerResponse(SoilRecord record) {
        return new GisMarkerResponse(
                record.getSampleId(),
                record.getLocation().latitude(),
                record.getLocation().longitude());
    }

    private record LocationKey(Double latitude, Double longitude) {}
}
