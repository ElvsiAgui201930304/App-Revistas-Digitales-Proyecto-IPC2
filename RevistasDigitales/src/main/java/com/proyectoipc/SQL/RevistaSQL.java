package com.proyectoipc.SQL;

import com.proyectoipc.Entidades.Etiqueta;
import com.proyectoipc.Entidades.Revista;
import com.proyectoipc.conexionSQL.Conexion;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author elvis_agui
 */
public class RevistaSQL {

    private PreparedStatement query = null;
    private ResultSet result = null;
    private Connection conexion = null;

    public void guardarRevista(Revista revista) {
        int precio_Global = 0;
        if (revista.getTipoRevista().equalsIgnoreCase("Gratis")) {
            revista.setPrecio(precio_Global);
        }
        boolean permisosInicial = true;
        String consulta = "INSERT INTO revista(titulo, editor, categoria, precio, costo_Global, suscripcion, reaccionar, comentar) VALUES (?,?,?,?,?,?,?,?)";
        try {
            conexion = Conexion.getConexion();
            query = conexion.prepareStatement(consulta);
            query.setString(1, revista.getTitulo());
            query.setString(2, revista.getEditor());
            query.setString(3, revista.getCategoria());
            query.setInt(4, revista.getPrecio());
            query.setInt(5, precio_Global);
            query.setBoolean(6, permisosInicial);
            query.setBoolean(7, permisosInicial);
            query.setBoolean(8, permisosInicial);
            query.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Error insertar revista " + ex.getMessage());
        } finally {
            cierre();
        }
    }

    public void guardarEdicion(Revista revista, String path, String contenType) {
        String consulta = "INSERT INTO edicion(codigo, fecha, descripcion, revista_pdf, revista, content_type) VALUES (?,?,?,?,?,?)";
        try {
            conexion = Conexion.getConexion();
            query = conexion.prepareStatement(consulta);
            query.setInt(1, this.codigo());
            query.setDate(2, this.getFecha(revista.getFecha()));
            query.setString(3, revista.getDescripcion());
            query.setString(4, path);
            query.setString(5, revista.getTitulo());
            query.setString(6, contenType);
            query.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Error insertar EDICION " + ex.getMessage());
        } catch (ParseException ex) {
            System.out.println("erroe en parse fecha" + ex.getMessage());
        } finally {
            cierre();
        }
    }

    public void guardarCategoria(Revista revista) {
        if (null != revista.getCategoriaNueva()) {
            revista.setCategoria(revista.getCategoriaNueva());
            String consulta = "INSERT INTO categoria(nombre_Categoria) VALUES (?)";
            try {
                conexion = Conexion.getConexion();
                query = conexion.prepareStatement(consulta);
                query.setString(1, revista.getCategoria());
                query.executeUpdate();
            } catch (SQLException ex) {
                System.out.println("Error insertar categoria nueva " + ex.getMessage());
            } finally {
                cierre();
            }
        }
    }

    public List<Revista> revistas(boolean filtroAutor, String editor) {
        List<Revista> revistas = new ArrayList<>();
        String consulta;
        try {
            if (filtroAutor) {
                consulta = "SELECT * FROM revista WHERE editor =?";
                conexion = Conexion.getConexion();
                query = conexion.prepareStatement(consulta);
                query.setString(1, editor);
                result = query.executeQuery();
            } else {
                consulta = "SELECT * FROM revista";
                conexion = Conexion.getConexion();
                query = conexion.prepareStatement(consulta);
                result = query.executeQuery();
            }
            while (result.next()) {
                Revista temp = new Revista();
                temp.setTitulo(result.getString("titulo"));
                temp.setEditor(result.getString("editor"));
                temp.setCategoria(result.getString("categoria"));
                temp.setPrecio(result.getInt("precio"));
                temp.setSuscripcion(result.getBoolean("suscripcion"));
                temp.setReaccionar(result.getBoolean("reaccionar"));
                temp.setComentar(result.getBoolean("comentar"));
                revistas.add(temp);
            }
        } catch (SQLException e) {
            System.out.println("erro en listar las Revistas");
        } finally {
            this.cierre();
        }
        return revistas;
    }

    private int codigo() {
        return (int) (Math.random() * 999999);
    }

    private Date getFecha(String localDate) throws ParseException {
         SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        Date fecha = null;
        java.util.Date nFecha = formato.parse(localDate);
        fecha = new java.sql.Date(nFecha.getTime());

        return fecha;

        return fecha;
    }

    private void cierre() {
        if (conexion != null) {
            try {
                conexion.close();
            } catch (SQLException ex) {
                System.out.println("Error al cerrar sql  db");
            }
        }
        if (result != null) {
            try {
                result.close();
            } catch (SQLException ex) {
                System.out.println("error al cerrar resul");
            }
        }
        if (query != null) {
            try {
                query.close();
            } catch (SQLException ex) {
                System.out.println("error al cerrar query");
            }
        }

    }

}
