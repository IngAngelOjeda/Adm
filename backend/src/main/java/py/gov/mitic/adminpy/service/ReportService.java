package py.gov.mitic.adminpy.service;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import py.gov.mitic.adminpy.model.entity.Usuario;
import py.gov.mitic.adminpy.repository.UsuarioRepository;

import java.io.*;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final UsuarioRepository usuarioRepository;

    public ReportService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public <T> byte[] generateReport(String format, String fileName, Map<String, Object> reportParameters, T data) {
        try {

            //--- getFilePathFromClasspath
            ClassPathResource resource = new ClassPathResource("reportes/" + fileName + ".jasper");

            try (InputStream jasperInputStream = resource.getInputStream()) {
                JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperInputStream);
                JRDataSource dataSource;

                if (data instanceof List<?>) { // Si data es una lista, utiliza JRBeanCollectionDataSource
                    dataSource = new JRBeanCollectionDataSource((List<?>) data);
                } else { // Si data es un objeto individual, crea una lista con un solo elemento
                    dataSource = new JRBeanCollectionDataSource(List.of(data));
                }
                //--- generado por
                UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                Usuario u = usuarioRepository.findByUsername(userDetails.getUsername());
                reportParameters.put("generadoPor", u.getNombre() + " " + u.getApellido() + " (" + u.getUsername() + ")");

                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, reportParameters, dataSource);

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                JRAbstractExporter exporter;

                switch (format.toLowerCase()) {
                    case "pdf":
                        exporter = new JRPdfExporter();
                        break;
                    case "excel":
                        exporter = new JRXlsxExporter();
                        break;
                    case "csv":
                        exporter = new JRCsvExporter();
                        break;
                    default:
                        throw new IllegalArgumentException("Formato de informe no admitido: " + format);
                }

                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
                exporter.exportReport();

                return outputStream.toByteArray();
            }

        } catch (FileNotFoundException ex) {
            throw new RuntimeException("No se encontró el archivo Jasper: " + fileName, ex);
        } catch (JRException | IOException ex) {
            throw new RuntimeException("Error al generar el informe Jasper", ex);
        }
    }
}
