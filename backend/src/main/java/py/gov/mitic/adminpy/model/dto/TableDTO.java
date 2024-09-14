package py.gov.mitic.adminpy.model.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class TableDTO<T> {

    private List<T> lista;

    private int totalRecords;

}