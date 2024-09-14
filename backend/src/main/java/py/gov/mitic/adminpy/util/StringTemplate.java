package py.gov.mitic.adminpy.util;

import java.lang.reflect.Field;

/**
 * Permite reemplazar placeholder a partir de propiedades de un objeto.
 *
 * <pre>
 * <code>
 * String tpl = "Hola {{nombre}} {{apellido}}, como estás?";
 * Persona p = new Persona("Jorge", "Ramírez");
 * try {
 *     System.out.println(StringTemplate.replace(tpl, p));
 * } catch (Exception e) {
 * 	   e.printStackTrace();
 * }
 * </code>
 * </pre>
 *
 * @author Jorge Ramírez
 **/
public class StringTemplate {

    /**
     * Reemplaza los placeholders en el template tomando como datasource el
     * bean.
     *
     * @param template
     *            El template a utilizar como base.
     * @param bean
     *            La fuente de datos.
     **/
    public static String replace(String template, Object bean) throws Exception {
        String rpl = template;

        for (Field f : bean.getClass().getDeclaredFields()) {

            if (f.get(bean) != null) {
                rpl = rpl.replaceAll("\\{\\{" + f.getName() + "\\}\\}",
                        f.get(bean).toString());
            }
        }
        return rpl;
    }

    public static void main(String args[]) {
        String tpl = "Hola {{nombre}} {{apellido}}, como estás?";
        Persona p = new Persona("Jorge", "Ramírez");
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class Persona {
        String nombre;
        String apellido;
        String direccion;

        Persona(String nombre, String apellido) {
            this.nombre = nombre;
            this.apellido = apellido;
        }
    }
}
