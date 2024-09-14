package py.gov.mitic.adminpy.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import py.gov.mitic.adminpy.model.dto.ResponseDTO;
import py.gov.mitic.adminpy.model.response.TokenSIIResponse;

import java.util.Collections;

@Configuration
public class UtilRest {

    @Value("${sii.username}") String username;

    @Value("${sii.password}") String password;

    @Value("${sii.server}") String server;

    private static Log logger = LogFactory.getLog(UtilRest.class);

    public static final String URL_SECURITY = "/mbohape-core/sii/security";

    /**
     *
     * @param uri       URL de consulta
     * @param metodo    Method { GET...}
     * @return          ResponseDTO(String..., status)
     * @throws JsonProcessingException
     */
    public ResponseDTO responseCommon(String uri, String metodo) throws JsonProcessingException {

        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();
        ResponseEntity<String> response = null;

        logger.info("IN-responseCommon:uri: " + uri);

        if (this.obtenerToken().isSuccess()) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(obtenerToken().getToken());
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<String> request  = new HttpEntity<String>(null, headers);
            response = restTemplate.exchange(
                    uri,
                    HttpMethod.resolve(metodo),
                    request,
                    String.class);
        }
        logger.info(mapper.readTree(response.getBody()));

        return new ResponseDTO(mapper.readTree(response.getBody()), response.getStatusCode());
    }

    /**
     * Generar token del core SII
     * @return  object { token, success }
     */
    public TokenSIIResponse obtenerToken() {

        String url = server + UtilRest.URL_SECURITY;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject credentials = new JSONObject();
        credentials.put("username", username);
        credentials.put("password", password);

        ResponseEntity<TokenSIIResponse> response = null;

        HttpEntity<String> request = new HttpEntity<String>(credentials.toString(), headers);
        try{
            response = restTemplate.postForEntity(
                    url,
                    request ,
                    TokenSIIResponse.class
            );
        }
        catch (ResourceAccessException e){ logger.info("Downstream dependency is offline: " + e.getMessage()); }
        catch (Exception e) { logger.info("Other Exception caught: " + e.getMessage()); }

        return response.getBody();
    }

}