package com.proyectoipc.revistasdigitales.servlet;

import com.proyectoipc.Entidades.Revista;
import com.proyectoipc.SQL.RevistaSQL;
import com.proyectoipc.comvert.RevistaConvert;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 *
 * @author elvis_agui
 */
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 100,
        maxFileSize = 1024 * 1024 * 1000,
        maxRequestSize = 1024 * 1024 * 1000
)
public class UploadRevista extends HttpServlet {

    private Revista revista;
    private RevistaSQL revistaSQL;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RevistaConvert revistaConver = new RevistaConvert(Revista.class);
        this.revista = revistaConver.fromJson(lector(request));

    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
       int version = this.numVersion();
       this.revistaSQL = new RevistaSQL();
        Part file = request.getPart("datafile");
        String fileName = file.getHeader("Content-type");
        String nombreArchivo = file.getSubmittedFileName();
        String path = this.getServletConfig().getServletContext().getRealPath("/archivo");
        File directorio = new File(path);
        if (!directorio.exists()) {
            directorio.mkdir();
        }
        file.write(path + "/" + version+ nombreArchivo);
        File archivo = new File(path + "/" + version+ nombreArchivo);
        System.out.println(revista.toString());
        this.revistaSQL.guardarCategoria(revista);
        this.revistaSQL.guardarRevista(revista);
        this.revistaSQL.guardarEdicion(revista, archivo.toString(), fileName);

    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private String lector(HttpServletRequest request) throws IOException {
        BufferedReader reader = request.getReader();
        String body = "";
        String line = reader.readLine();
        while (line != null) {
            body = body + line;
            line = reader.readLine();
        }
        return body;
    }
    
    private int numVersion(){
        return (int)(Math.random()*999);
    }

}
