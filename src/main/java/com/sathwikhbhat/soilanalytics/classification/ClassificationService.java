package com.sathwikhbhat.soilanalytics.classification;

import com.sathwikhbhat.soilanalytics.classification.config.NutrientClassificationProperties;
import com.sathwikhbhat.soilanalytics.classification.dto.NutrientClassificationResponse;
import com.sathwikhbhat.soilanalytics.classification.model.NutrientLevel;
import com.sathwikhbhat.soilanalytics.classification.model.NutrientThreshold;
import com.sathwikhbhat.soilanalytics.soilrecord.entity.NutrientData;
import org.springframework.stereotype.Service;

@Service
public class ClassificationService {

    private final NutrientClassificationProperties properties;

    public ClassificationService(NutrientClassificationProperties properties) {
        this.properties = properties;
    }

    public NutrientClassificationResponse classify(NutrientData nutrients) {
        return new NutrientClassificationResponse(
                classify(nutrients.ph(), properties.ph()),
                classify(nutrients.ec(), properties.ec()),
                classify(nutrients.organicCarbon(), properties.organicCarbon()),
                classify(nutrients.nitrogen(), properties.nitrogen()),
                classify(nutrients.phosphorus(), properties.phosphorus()),
                classify(nutrients.potassium(), properties.potassium()),
                classify(nutrients.sulfur(), properties.sulfur()),
                classify(nutrients.zinc(), properties.zinc()),
                classify(nutrients.boron(), properties.boron()),
                classify(nutrients.iron(), properties.iron()),
                classify(nutrients.copper(), properties.copper()),
                classify(nutrients.manganese(), properties.manganese()));
    }

    private NutrientLevel classify(double value, NutrientThreshold threshold) {
        if (value <= threshold.low()) {
            return NutrientLevel.LOW;
        }

        if (value <= threshold.medium()) {
            return NutrientLevel.MEDIUM;
        }

        return NutrientLevel.HIGH;
    }
}
