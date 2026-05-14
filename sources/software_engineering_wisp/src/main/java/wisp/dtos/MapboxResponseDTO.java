package wisp.dtos;

import java.util.List;

public class MapboxResponseDTO {
    public List<FeatureDTO> features;

    public static class FeatureDTO {
        public List<Double> center; // Mapbox retorna [latitude, longitude]
    }
}