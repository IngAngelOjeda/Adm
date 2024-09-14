package py.gov.mitic.adminpy.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(schema = "public", name = "dependencia")
@Getter
@Setter
@ToString
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)

public class Dependencia implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "DEPENDENCIA_ID_GENERATOR", sequenceName = "public.dependencia_id_dependencia_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DEPENDENCIA_ID_GENERATOR")
    @Column(name = "id_dependencia", unique = true, nullable = false)
    private Long idDependencia;

    @Column(name = "descripcion_dependencia")
    private String descripcionDependencia;

    @Column(name = "orden")
    private Long orden;

    @Column(name = "codigo")
    private String codigo;

    @ManyToOne
    @JoinColumn(name = "id_dependencia_padre", nullable = true)
    private Dependencia dependenciaPadre;

    // ####TreeNode
//    @OneToMany(mappedBy = "dependenciaPadre", cascade = CascadeType.ALL)
//    private List<Dependencia> dependenciasHijas;

    @ManyToOne
    @JoinColumn(name = "id_oee")
    private Oee oee;

    @Column(name = "estado_dependencia")
    private Boolean estadoDependencia;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Asuncion")
    @Column(name = "fecha_creacion")
    private Date fechaCreacion;

    public Dependencia(){}

    public Dependencia(Long idDependencia, String descripcionDependencia) {
        super();
        this.idDependencia = idDependencia;
        this.descripcionDependencia = descripcionDependencia;
    }



    // ####TreeNode
//    @Getter
//    @Setter
//    public static class TreeNode {
//        private String label;
//        private boolean expanded;
//        private List<Dependencia.TreeNode> children;
//
//        // Constructor
//        public TreeNode(String label, boolean expanded, List<TreeNode> children) {
//            this.label = label;
//            this.expanded = expanded;
//            this.children = children;
//        }
//
//        // Getter y Setter para label
//        public String getLabel() {
//            return label;
//        }
//
//        public void setLabel(String label) {
//            this.label = label;
//        }
//
//        // Getter y Setter para expanded
//        public boolean isExpanded() {
//            return expanded;
//        }
//
//        public void setExpanded(boolean expanded) {
//            this.expanded = expanded;
//        }
//
//        // Getter y Setter para children
//        public List<TreeNode> getChildren() {
//            return children;
//        }
//
//        public void setChildren(List<TreeNode> children) {
//            this.children = children;
//        }
//
//        // Método estático para crear un TreeNode desde una Dependencia
//        public static TreeNode fromDependencia(Dependencia dependencia) {
//            TreeNode node = new TreeNode(dependencia.getDescripcionDependencia(), true, null);
//
//            if (dependencia.getDependenciasHijas() != null && !dependencia.getDependenciasHijas().isEmpty()) {
//                List<TreeNode> children = dependencia.getDependenciasHijas().stream()
//                        .map(TreeNode::fromDependencia)
//                        .collect(Collectors.toList());
//                node.setChildren(children);
//            }
//
//            return node;
//        }
//    }

}
