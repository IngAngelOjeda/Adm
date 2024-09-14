package py.gov.mitic.adminpy.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import py.gov.mitic.adminpy.model.dto.CommonDTO;
import py.gov.mitic.adminpy.model.dto.RequisitoDTO;
import py.gov.mitic.adminpy.model.dto.ResponseDTO;
import py.gov.mitic.adminpy.repository.RequisitoRepository;
import py.gov.mitic.adminpy.model.entity.Requisito;
import py.gov.mitic.adminpy.specification.GenericSpecification;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RequisitoService extends  GenericSpecification<Requisito>{

    private Log logger = LogFactory.getLog(RequisitoService.class);

    private final RequisitoRepository requisitoRepository;

    public RequisitoService(RequisitoRepository requisitoRepository){

        this.requisitoRepository = requisitoRepository;
    }

    public ResponseDTO getList() {
        List<RequisitoDTO> list = requisitoRepository.findByEstado(true)
                .stream()
                .map(o -> new RequisitoDTO(o.getIdRequisito(), o.getNombreRequisito()))
                .collect(Collectors.toList());

        return new ResponseDTO(list, HttpStatus.OK);
    }

    public ResponseDTO getListbyId(Long id){

        List<RequisitoDTO> list = requisitoRepository.findByIdServicio(id)
                .stream()
                .map(o -> new RequisitoDTO(o.getId_Requisito(), o.getNombre_Requisito()))
                .collect(Collectors.toList());

        return new ResponseDTO(list, HttpStatus.OK);

    }
}
