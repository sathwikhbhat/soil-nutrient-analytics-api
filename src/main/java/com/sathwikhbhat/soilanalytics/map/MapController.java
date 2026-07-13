package com.sathwikhbhat.soilanalytics.map;

import com.sathwikhbhat.soilanalytics.map.dto.MapMarkerResponse;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/map")
public class MapController {

    private final MapService mapService;

    public MapController(MapService mapService) {
        this.mapService = mapService;
    }

    @GetMapping("/markers")
    public ResponseEntity<List<MapMarkerResponse>> getMarkers(
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String taluk,
            @RequestParam(required = false) String village,
            @RequestParam(required = false) String crop) {
        List<MapMarkerResponse> mapMarkerResponses = mapService.getMarkers(state, district, taluk, village, crop);
        return ResponseEntity.ok(mapMarkerResponses);
    }
}
