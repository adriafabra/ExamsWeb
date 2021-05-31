/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package udl.service;

import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import udl.Exams;


@Stateless
@Path("udl.exams")
public class ExamsFacadeREST extends AbstractFacade<Exams> {

    @PersistenceContext(unitName = "ExamsWebPU")
    private EntityManager em;

    public ExamsFacadeREST() {
        super(Exams.class);
    }

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.TEXT_PLAIN})
    public String uploadExam(Exams entity) {
        for(Exams ex: super.findAll()){
            if(ex.getDescription().toLowerCase().equals(entity.getDescription().toLowerCase()) 
                    && ex.getQuestions().toLowerCase().equals(entity.getQuestions().toLowerCase()) 
                    && ex.getLocation().toLowerCase().equals(entity.getLocation().toLowerCase())){
                return "Impossible to upload, exam already exists";
            }
        }
         
        SimpleDateFormat dateSimple = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeSimple = new SimpleDateFormat("HH:mm:ss");  
        Date now = new Date(); 
        String dateStr = dateSimple.format(now);
        String timeStr = timeSimple.format(now);
        try{
            Date date = dateSimple.parse(dateStr);
            Date time = timeSimple.parse(timeStr);
            entity.setDate(date);
            entity.setTime(time);
        }catch(ParseException e){
            e.printStackTrace();
        }
        super.create(entity);
        return "Exam uploaded";
    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void edit(@PathParam("id") Integer id, Exams entity) {
        super.edit(entity);
    }
    
    @PUT
    @Path("description/{id}")
    @Consumes({MediaType.TEXT_PLAIN})
    @Produces({MediaType.TEXT_PLAIN})
    public String modifyDescription(@PathParam("id") Integer id, String description) {
        if(super.find(id) != null){
            super.find(id).setDescription(description);
            return "Description changed";
        }else{
            return "Impossible to change, exam does not exist";
        }
    }
    
    @PUT
    @Path("answers/{id}")
    @Consumes({MediaType.TEXT_PLAIN})
    @Produces({MediaType.TEXT_PLAIN})
    public String setAnswers(@PathParam("id") Integer id, String answers) {
        if(super.find(id) != null){
            super.find(id).setAnswers(answers);
            return "Answers added";
        }else{
            return "Impossible to set answers, exam does not exist";
        }
    }

    @DELETE
    @Path("{id}")
    @Produces({MediaType.TEXT_PLAIN})
    public String remove(@PathParam("id") Integer id) {
        if(super.find(id) != null){
            if(super.find(id).getGradesCollection().isEmpty()){
                super.remove(super.find(id));
                return "Exam removed";
            }else{
                return "Exam impossible to remove, it has grades!";
            }
        }else{
            return "Exam does not exist";
        }
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Exams find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    @GET
    @Override
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Exams> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Exams> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }
    
    @GET
    @Path("search/{id}")
    @Produces({MediaType.TEXT_PLAIN})
    public String searchById(@PathParam("id") Integer id) {
        Exams ex = super.find(id);
        if(ex == null){
            return "Exam does not exist";
        }else{
            return printExam(ex.getExamId(), ex.getQuestions(), ex.getAnswers(), ex.getDescription(), ex.getDate(), ex.getTime(), ex.getLocation());
        }
    }

    @GET
    @Path("search/description/{description}")
    @Produces({MediaType.TEXT_PLAIN})
    public String searchByDescription(@PathParam("description") String description) {
        String exams = "";        
        Boolean hasMatch = false;
        
        for(Exams ex: super.findAll()){
            if(ex.getDescription().toLowerCase().contains(description.toLowerCase())){
                exams = exams + printExam(ex.getExamId(), ex.getQuestions(), ex.getAnswers(), ex.getDescription(), ex.getDate(), ex.getTime(), ex.getLocation());
                hasMatch = true;
            }
        }
        
        if(!hasMatch){
            return "Any exam matched with the description";
        }else{
            return exams;
        }
    }
    
    @GET
    @Path("download/{id}")
    @Produces({MediaType.TEXT_PLAIN})
    public String downloadExam(@PathParam("id") Integer id){
        Exams ex = super.find(id);
        if(ex == null){
            return "Exam does not exist";
        }else{
            try{
                String home = System.getProperty("user.home");
                FileWriter file = new FileWriter(home + "/Descargas/exam" + id + ".txt");
                file.write(printExam(ex.getExamId(), ex.getQuestions(), ex.getAnswers(), ex.getDescription(), ex.getDate(), ex.getTime(), ex.getLocation()));
                file.close();
            }catch(IOException e){
                e.printStackTrace();
            }
            return "Exam downloaded";
        }
    }
    
    private String printExam(int id, String questions, String answers, String description, Date date, Date time, String location){
        SimpleDateFormat dateSimple = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeSimple = new SimpleDateFormat("HH:mm:ss");  
        
        String dateStr = dateSimple.format(date);
        String timeStr = timeSimple.format(time);
            
        String exam = "ExamID: " + id +
                        "\nQuestions: " + questions +
                        "\nAnswers: " + answers +
                        "\nDescription: " + description +
                        "\nDate: " + dateStr +
                        "\nTime: " + timeStr +
                        "\nLocation: " + location +
                        "\n\n";
        
        return exam;
    }
    
    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(super.count());
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
}
