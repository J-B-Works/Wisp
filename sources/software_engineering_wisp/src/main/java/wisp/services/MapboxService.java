package wisp.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import wisp.dtos.MapboxResponseDTO;
import wisp.dtos.ViaCepDTO;

@Service
public class MapboxService {
    @Value("${MAPBOX_API_KEY}")
    private String mapboxApiKey;

    public double[] getCoordinatesFromCep(String cep) {
        String cepLimpo = cep.replace("-", "").trim();
        RestTemplate restTemplate = new RestTemplate();

        try {
            // ------------- UTILIZA API ViaCEP AUXILIAR PARA CONVERTER CEP PARA NOME DE RUA -------------
            String viaCepUrl = "https://viacep.com.br/ws/" + cepLimpo + "/json/";
            ViaCepDTO viaCepData = restTemplate.getForObject(viaCepUrl, ViaCepDTO.class);

            if (viaCepData != null && viaCepData.logradouro != null) {
                // ------------- LIMPA ENDEREÇO (RUA, CIDADE) -------------
                String endereco = viaCepData.logradouro + ", " + viaCepData.localidade;
                
                // ------------- PASSA ENDEREÇO COMPLETO PARA MAPBOX -------------
                String mapboxUrl = "https://api.mapbox.com/geocoding/v5/mapbox.places/" + endereco + ".json?country=br&access_token=" + mapboxApiKey;
                MapboxResponseDTO mapboxResponse = restTemplate.getForObject(mapboxUrl, MapboxResponseDTO.class);

                // ------------- GUARDAR LATITUDE E LONGITUDE -------------
                if (mapboxResponse != null && mapboxResponse.features != null && !mapboxResponse.features.isEmpty()) {
                    double lon = mapboxResponse.features.get(0).center.get(0);
                    double lat = mapboxResponse.features.get(0).center.get(1);
                    return new double[]{lat, lon};
                }
            }
        } catch (Exception e) {
            System.out.println("Erro na geolocalização: " + e.getMessage());
        }
        
        return new double[]{0.0, 0.0}; 
    }
}