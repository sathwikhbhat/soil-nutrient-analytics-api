package com.sathwikhbhat.soilanalytics.map;

import com.sathwikhbhat.soilanalytics.map.dto.MapFilterRequest;
import com.sathwikhbhat.soilanalytics.map.dto.MapMarkerResponse;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/map")
public class MapController {

    private final MapService mapService;

    public MapController(MapService mapService) {
        this.mapService = mapService;
    }

    @GetMapping("/markers")
    public ResponseEntity<List<MapMarkerResponse>> getMarkers(@ModelAttribute MapFilterRequest filters) {
        List<MapMarkerResponse> mapMarkerResponses = mapService.getMarkers(filters);
        return ResponseEntity.ok(mapMarkerResponses);
    }
}
