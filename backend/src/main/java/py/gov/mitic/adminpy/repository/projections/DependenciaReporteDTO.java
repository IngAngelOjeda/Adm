package py.gov.mitic.adminpy.repository.projections;

public interface DependenciaReporteDTO {

    Long getid_dependencia();

    Long getid_dependencia_padre();

    String getcodigo();

    String getdescripcion_dependencia();

    String getdescripcion_dependencia_padre();

    String getdescripcion_oee();
}
