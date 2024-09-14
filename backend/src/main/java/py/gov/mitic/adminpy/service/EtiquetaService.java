package py.gov.mitic.adminpy.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import py.gov.mitic.adminpy.model.dto.CommonDTO;
import py.gov.mitic.adminpy.model.dto.EtiquetaDTO;
import py.gov.mitic.adminpy.model.dto.ServicioDTO;
import py.gov.mitic.adminpy.repository.EtiquetaRepository;
import py.gov.mitic.adminpy.model.dto.ResponseDTO;
import py.gov.mitic.adminpy.model.entity.Etiqueta;
import py.gov.mitic.adminpy.repository.ServicioEtiquetaRepository;
import py.gov.mitic.adminpy.specification.GenericSpecification;

import javax.ws.rs.BadRequestException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EtiquetaService extends GenericSpecification<Etiqueta>{

    private Log logger = LogFactory.getLog(EtiquetaService.class);

    private final EtiquetaRepository etiquetaRepository;

    private final ServicioEtiquetaRepository servicioEtiquetaRepository;

    public EtiquetaService(EtiquetaRepository etiquetaRepository,ServicioEtiquetaRepository servicioEtiquetaRepository) {
        this.etiquetaRepository = etiquetaRepository;
        this.servicioEtiquetaRepository = servicioEtiquetaRepository;
    }

    public ResponseDTO getList() {
        List<EtiquetaDTO> list = etiquetaRepository.findByEstadoEtiqueta("A")
                .stream()
                .map(o -> new EtiquetaDTO(o.getIdEtiqueta(), o.getNombreEtiqueta()))
                .collect(Collectors.toList());

        return new ResponseDTO(list, HttpStatus.OK);
    }

    public ResponseDTO getListbyId(Long id){

        List<EtiquetaDTO> list = servicioEtiquetaRepository.findByIdServicio(id)
                .stream()
                .map(o -> new EtiquetaDTO(o.getId_Etiqueta(), o.getNombre_Etiqueta()))
                .collect(Collectors.toList());

        return new ResponseDTO(list, HttpStatus.OK);

    }

}
