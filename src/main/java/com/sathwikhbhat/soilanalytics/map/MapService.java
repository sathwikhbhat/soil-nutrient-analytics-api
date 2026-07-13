package com.sathwikhbhat.soilanalytics.map;

import com.sathwikhbhat.soilanalytics.entity.SoilRecord;
import com.sathwikhbhat.soilanalytics.map.dto.MapMarkerResponse;
import java.util.List;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class MapService {

    private final MongoTemplate mongoTemplate;

    public MapService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<MapMarkerResponse> getMarkers(
            String state, String district, String taluk, String village, String crop) {
        Query query = new Query();

        if (state != null && !state.isBlank()) {
            query.addCriteria(Criteria.where("location.state").is(state));
        }
        if (district != null && !district.isBlank()) {
            query.addCriteria(Criteria.where("location.district").is(district));
        }
        if (taluk != null && !taluk.isBlank()) {
            query.addCriteria(Criteria.where("location.taluk").is(taluk));
        }
        if (village != null && !village.isBlank()) {
            query.addCriteria(Criteria.where("location.village").is(village));
        }
        if (crop != null && !crop.isBlank()) {
            query.addCriteria(Criteria.where("crop").is(crop));
        }

        List<SoilRecord> records = mongoTemplate.find(query, SoilRecord.class);

        return records.stream().map(this::toMapMarkerResponse).toList();
    }

    private MapMarkerResponse toMapMarkerResponse(SoilRecord record) {
        return new MapMarkerResponse(
                record.getSampleId(),
                record.getLocation().latitude(),
                record.getLocation().longitude(),
                record.getLocation().state(),
                record.getLocation().district(),
                record.getLocation().taluk(),
                record.getLocation().village(),
                record.getCrop());
    }
}
