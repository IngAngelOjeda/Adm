package py.gov.mitic.adminpy.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import py.gov.mitic.adminpy.model.dto.CommonDTO;
import py.gov.mitic.adminpy.model.dto.TipoDatoDto;
import py.gov.mitic.adminpy.repository.TipoDatoRepository;
import py.gov.mitic.adminpy.model.dto.ResponseDTO;
import py.gov.mitic.adminpy.model.entity.TipoDato;
import py.gov.mitic.adminpy.specification.GenericSpecification;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TipoDatoService extends GenericSpecification<TipoDato>{

    private Log logger = LogFactory.getLog(TipoDatoService.class);

    private final TipoDatoRepository tipoDatoRepository;

    public TipoDatoService(TipoDatoRepository tipoDatoRepository) {

        this.tipoDatoRepository = tipoDatoRepository;
    }

    public ResponseDTO getList() {
        List<TipoDatoDto> list = tipoDatoRepository.findByEstado(true)
                .stream()
                .map(o -> new TipoDatoDto(o.getIdTipoDato(), o.getDescripcionTipoDato()))
                .collect(Collectors.toList());

        return new ResponseDTO(list, HttpStatus.OK);
    }

}
