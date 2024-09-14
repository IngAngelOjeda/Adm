package py.gov.mitic.adminpy.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import py.gov.mitic.adminpy.model.dto.ClasificadorDTO;
import py.gov.mitic.adminpy.model.dto.CommonDTO;
import py.gov.mitic.adminpy.model.entity.Clasificador;
import py.gov.mitic.adminpy.repository.ClasificadorRepository;
import py.gov.mitic.adminpy.model.dto.ResponseDTO;
import py.gov.mitic.adminpy.specification.GenericSpecification;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClasificadorService extends GenericSpecification<Clasificador>{

    private Log logger = LogFactory.getLog(ClasificadorService.class);

    private final ClasificadorRepository clasificadorRepository;

    public ClasificadorService(ClasificadorRepository clasificadorRepository) {
        this.clasificadorRepository = clasificadorRepository;
    }

    public ResponseDTO getList() {
        List<ClasificadorDTO> list = clasificadorRepository.findByEstadoClasificador("A")
                .stream()
                .map(o -> new ClasificadorDTO(o.getIdClasificador(), o.getNombreClasificador()))
                .collect(Collectors.toList());

        return new ResponseDTO(list, HttpStatus.OK);
    }

    public ResponseDTO getListbyId(Long id){

        List<ClasificadorDTO> list = clasificadorRepository.findByIdServicio(id)
                .stream()
                .map(o -> new ClasificadorDTO(o.getId_Clasificador(), o.getNombre_Clasificador()))
                .collect(Collectors.toList());

        return new ResponseDTO(list, HttpStatus.OK);

    }

}
