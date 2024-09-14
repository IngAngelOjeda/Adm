package py.gov.mitic.adminpy.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
    private ObjectMapper objectMapper = new ObjectMapper();

    @Before("execution(* py.gov.mitic.adminpy.service..*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String packageName = joinPoint.getTarget().getClass().getPackage().getName();
        try {
            String jsonResponse = objectMapper.writeValueAsString(joinPoint.getArgs());
            if(methodName.equals("replaceTemplate"))
            {
                logger.info("IN-{} [{}]:params - {}", methodName, packageName, "Correo Generado "+methodName);
            }
            else if(methodName.equals("send"))
            {
                logger.info("IN-{} [{}]:params - {}", methodName, packageName, "Correo Generado "+methodName);
            }
            else if(methodName.equals("generateReport"))
            {
                logger.info("IN-{} [{}]:params - {}", methodName, packageName, "Reporte Generado "+methodName);
            }
            else
            {
                logger.info("IN-{} [{}]:params - {}", methodName, packageName, jsonResponse);
            } 
        } catch (JsonProcessingException e) {
            logger.error("Error al convertir el objeto de respuesta a JSON", e);
        }
    }

    @AfterReturning(pointcut = "execution(* py.gov.mitic.adminpy.service..*.*(..))", returning = "response")
    public void logAfter(JoinPoint joinPoint, Object response) {
        String methodName = joinPoint.getSignature().getName();
        String packageName = joinPoint.getTarget().getClass().getPackage().getName();
        String className = joinPoint.getTarget().getClass().getName();
        try {
            if (response instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) response;
                logger.info("OUT-{} [{}]:response - {}", methodName, packageName, maskInformacionSensible(userDetails));
            } else {
                if(methodName.equals("generateReport")) {
                    logger.info("OUT-{} [{}]:response - {}", methodName, packageName, "Reporte Generado "+className);
                }
                else if(methodName.equals("replaceTemplate")) {
                    logger.info("OUT-{} [{}]:response - {}", methodName, packageName, "Email enviado "+className);
                }
                else if(methodName.contains("get")) {
                    logger.info("OUT-{} [{}]:response - {}", methodName, packageName, "Listado Generado "+className);
                }
                else {
                    logger.info("OUT-{} [{}]:response - {}", methodName, packageName, objectMapper.writeValueAsString(response));
                }
            }
        } catch (JsonProcessingException e) {
            logger.error("Error al convertir el objeto de respuesta a JSON", e);
        }
    }

    private String maskInformacionSensible(UserDetails userDetails) {
        Map<String, Object> maskedInfo = new HashMap<>();
        maskedInfo.put("username", userDetails.getUsername());
        maskedInfo.put("password", "********");
        maskedInfo.put("authorities", "********");

        try {
            return objectMapper.writeValueAsString(maskedInfo);
        } catch (JsonProcessingException e) {
            logger.error("Error al convertir el objeto de respuesta a JSON", e);
            return "";
        }
    }

}


