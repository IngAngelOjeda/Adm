package py.gov.mitic.adminpy.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import py.gov.mitic.adminpy.model.request.AuthenticationRequest;
import py.gov.mitic.adminpy.model.dto.ResponseDTO;
import py.gov.mitic.adminpy.model.entity.RecuperarClave;
import py.gov.mitic.adminpy.model.entity.Usuario;
import py.gov.mitic.adminpy.repository.*;
import py.gov.mitic.adminpy.specification.GenericSpecification;

import javax.ws.rs.BadRequestException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class RecuperarClaveService extends GenericSpecification<RecuperarClave> {

    private Log logger = LogFactory.getLog(RecuperarClaveService.class);
    private Map<String, Object> auditMap = new HashMap<>();
    private Map<String, Object> auditMapOld = new HashMap<>();
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaService auditoriaService;


    @Autowired
    public RecuperarClaveService(UsuarioRepository usuarioRepository, AuditoriaService auditoriaService) {
        this.usuarioRepository = usuarioRepository;
        this.auditoriaService = auditoriaService;
    }

    public ResponseDTO getCodeByUser(String code) {

        //RecuperarClave data = repo.getCodeByUser(code);
        Usuario data = usuarioRepository.getCodeByUser(code);

        if (Objects.isNull(data)) {
            logger.info("El código de recuperación de clave es inválido: " + code);
            return new ResponseDTO("El código de recuperación de clave es inválido.", HttpStatus.BAD_REQUEST);
        }

        return new ResponseDTO(data, HttpStatus.OK);
    }

    public ResponseDTO updatePassword(String code, AuthenticationRequest auth) {

        //RecuperarClave data = repo.getCodeByUser(code);
        Usuario data = usuarioRepository.getCodeByUser(code);

        if (Objects.isNull(data)) {
            logger.info("El código de recuperación de clave es inválido: " + code);
            return new ResponseDTO("El código de recuperación de clave es inválido.", HttpStatus.BAD_REQUEST);
        }

        Usuario usuario = usuarioRepository.findByUsername(data.getUsername());
        //Usuario usuario = usuarioRepository.findByUsername(data.getUsuario().getUsername());

        if(usuario == null) {
            logger.warn("Usuario no existe");
            throw new BadRequestException("Usuario no existe.");
        }

        //if (!usuario.getEstado()) {
        //	throw new BadRequestException("Usuario no se encuentra activo.");
        //}

        if (auth.getPassword() == null || auth.getPassword().isEmpty()) {
            logger.warn("Contraseña vacía, no se cambiará la contraseña");
            throw new BadRequestException("Campo contraseña no puede estar vacía.");
        }

        if (!auth.getPassword().equals(auth.getPassword2())) {
            logger.warn("Las contraseñas no coinciden.");
            throw new BadRequestException("Las contraseñas no coinciden.");
        }

        try {
            validatePassword(auth.getPassword());
        } catch (PasswordValidationException e) {
            logger.warn("Error de validación de contraseña: " + e.getMessage());
            throw new BadRequestException(e.getMessage());
        }


        usuario.setPassword(new BCryptPasswordEncoder().encode(auth.getPassword()));
        Usuario saved = usuarioRepository.save(usuario);
        if (Objects.nonNull(saved)) {
            logger.info("Clave de recuperación eliminada.");
            //repo.deleteByCode(code);
            usuarioRepository.deleteByCode(code);
        }
//        usuario.setEstado(true);

//        auditMap = new HashMap<>();
//        auditMap.put("password", new BCryptPasswordEncoder().encode(auth.getPassword()));
//        auditMap.put("idUsuario", usuario.getIdUsuario());
//        while (auditMap.values().remove(null));
//        while (auditMap.values().remove(""));
//
//        auditMapOld = new HashMap<>();
//        auditMapOld.put("password", usuario.getPassword());
//        auditMapOld.put("idUsuario", usuario.getIdUsuario());
//        while (auditMapOld.values().remove(null));
//        while (auditMapOld.values().remove(""));
//
//
//
//        try {
//            auditoriaService.auditar("MODIFICAR",
//                    Objects.nonNull(usuario) ? usuario.getIdUsuario().toString() : null,
//                    auditMap,
//                    auditMapOld,
//                    "usuario",
//                    "/recuperar_clave",
//                    "/updatePassword",
//                    "/updatePassword",
//                    null,
//                    null,
//                    null,
//                    null);
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//            logger.error("Error inesperado", e);
//        }


        return new ResponseDTO("Clave actualizada con éxito para el usuario ".concat(usuario.getUsername()), HttpStatus.OK);
    }

    private void validatePassword(String password) throws PasswordValidationException {

        if (password.length() < 6) {
            throw new PasswordValidationException("La contraseña debe tener al menos 6 caracteres.");
        }

        boolean contieneMayuscula = false;
        boolean contieneCaracterEspecial = false;


        String caracteresEspeciales = "!@#$%^&*/.()-_+=<>?";

        for (char c : password.toCharArray()) {

            if (Character.isUpperCase(c)) {
                contieneMayuscula = true;
            }
            if (caracteresEspeciales.contains(String.valueOf(c))) {
                contieneCaracterEspecial = true;
            }
            if (contieneMayuscula && contieneCaracterEspecial) {
                break;
            }
        }

        if (!contieneMayuscula) {
            throw new PasswordValidationException("La contraseña debe contener al menos una letra mayúscula.");
        }
        if (!contieneCaracterEspecial) {
            throw new PasswordValidationException("La contraseña debe contener al menos un carácter especial.");
        }
    }
    public class PasswordValidationException extends Exception {
        public PasswordValidationException(String message) {
            super(message);
        }
    }

}
