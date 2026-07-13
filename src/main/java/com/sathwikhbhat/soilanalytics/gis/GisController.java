package com.sathwikhbhat.soilanalytics.gis;

import com.sathwikhbhat.soilanalytics.gis.dto.GisFilterRequest;
import com.sathwikhbhat.soilanalytics.gis.dto.GisHeatMapResponse;
import com.sathwikhbhat.soilanalytics.gis.dto.GisMarkerResponse;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/gis")
public class GisController {

    private final GisService gisService;

    public GisController(GisService gisService) {
        this.gisService = gisService;
    }

    @GetMapping("/markers")
    public ResponseEntity<List<GisMarkerResponse>> getMarkers(@ModelAttribute GisFilterRequest filters) {
        List<GisMarkerResponse> gisMarkerResponses = gisService.getMarkers(filters);
        return ResponseEntity.ok(gisMarkerResponses);
    }

    @GetMapping("/heatmap")
    public ResponseEntity<List<GisHeatMapResponse>> getHeatMap(@ModelAttribute GisFilterRequest filters) {
        List<GisHeatMapResponse> gisHeatMapResponses = gisService.getHeatMap(filters);
        return ResponseEntity.ok(gisHeatMapResponses);
    }
}
