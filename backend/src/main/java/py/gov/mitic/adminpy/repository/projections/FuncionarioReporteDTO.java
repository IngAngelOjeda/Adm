package py.gov.mitic.adminpy.repository.projections;

public interface FuncionarioReporteDTO {

    Long getid_funcionario();

    String getapellido();

    String getcargo();

    String getcedula();

    String getcorreo_institucional();

    Boolean getestado_funcionario();

    String getnombre();

    String getdescripcion_dependencia();

    String getdescripcion_oee();
}
