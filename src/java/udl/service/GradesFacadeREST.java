/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package udl.service;

import java.io.FileWriter;
import java.io.IOException;
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
import javax.ws.rs.core.PathSegment;
import udl.Exams;
import udl.Grades;
import udl.GradesPK;
import udl.Students;


@Stateless
@Path("udl.grades")
public class GradesFacadeREST extends AbstractFacade<Grades> {

    @PersistenceContext(unitName = "ExamsWebPU")
    private EntityManager em;

    private GradesPK getPrimaryKey(PathSegment pathSegment) {
        /*
         * pathSemgent represents a URI path segment and any associated matrix parameters.
         * URI path part is supposed to be in form of 'somePath;studentId=studentIdValue;examId=examIdValue'.
         * Here 'somePath' is a result of getPath() method invocation and
         * it is ignored in the following code.
         * Matrix parameters are used as field names to build a primary key instance.
         */
        udl.GradesPK key = new udl.GradesPK();
        javax.ws.rs.core.MultivaluedMap<String, String> map = pathSegment.getMatrixParameters();
        java.util.List<String> studentId = map.get("studentId");
        if (studentId != null && !studentId.isEmpty()) {
            key.setStudentId(new java.lang.Integer(studentId.get(0)));
        }
        java.util.List<String> examId = map.get("examId");
        if (examId != null && !examId.isEmpty()) {
            key.setExamId(new java.lang.Integer(examId.get(0)));
        }
        return key;
    }

    public GradesFacadeREST() {
        super(Grades.class);
    }

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.TEXT_PLAIN})
    public String uploadGrade(Grades entity) {
        GradesPK gradesPK = entity.getGradesPK();
        int examID = gradesPK.getExamId();
        int studentID = gradesPK.getStudentId();
        
        if(em.find(Exams.class, examID) == null){
            return "Impossible to upload, exam does not exist";
        }
        else if(em.find(Students.class, studentID) == null){
            return "Impossible to upload, student does not exist";
        }
        else{
            if(super.find(gradesPK) == null){
                entity.setExams(em.find(Exams.class, examID));
                entity.setStudents(em.find(Students.class, studentID));

                super.create(entity);
                return "Grade uploaded";
            }else{
                return "Impossible to upload, grade already exists";
            }
        }
    }
    
    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void edit(@PathParam("id") PathSegment id, Grades entity) {
        super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") PathSegment id) {
        udl.GradesPK key = getPrimaryKey(id);
        super.remove(super.find(key));
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Grades find(@PathParam("id") PathSegment id) {
        udl.GradesPK key = getPrimaryKey(id);
        return super.find(key);
    }

    @GET
    @Override
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Grades> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Grades> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }
    
    @GET
    @Path("grades/{id}")
    @Produces({MediaType.TEXT_PLAIN})
    public String downloadGrades(@PathParam("id") Integer studentID){
        String grades = "";        
        Boolean hasMatch = false;
        
        for(Grades gr: super.findAll()){
            if(gr.getGradesPK().getStudentId() == studentID){
                grades = grades + printGrade(gr.getGradesPK().getExamId(), gr.getGrade());
                hasMatch = true;
            }
        }
        
        if(!hasMatch){
            return "This student has not grades";
        }else{
            try{
                String home = System.getProperty("user.home");
                FileWriter file = new FileWriter(home + "/Descargas/gradesStudent" + studentID + ".txt"); 
                file.write(grades);
                file.close();
            }catch(IOException e){
                e.printStackTrace();
            }
            return "Grades downloaded";
        }    
    }
    
    @GET
    @Path("grades")
    @Produces({MediaType.TEXT_PLAIN})
    public String downloadAllGrades(){
        String grades = "";        
        Boolean hasMatch = false;
        
        for(Grades gr: super.findAll()){
            grades = grades + printAllGrades(gr.getGradesPK().getExamId(), gr.getGrade(), gr.getGradesPK().getStudentId());
            hasMatch = true;
        }
        
        if(!hasMatch){
            return "No grades";
        }else{
            try{
                String home = System.getProperty("user.home");
                FileWriter file = new FileWriter(home + "/Descargas/allGrades.txt"); 
                file.write(grades);
                file.close();
            }catch(IOException e){
                e.printStackTrace();
            }
            return "Grades downloaded";
        }    
    }
    
    private String printGrade(int id, int grade){ 
        return "ExamID: " + id + " Grade: " + grade + "\n\n";  
    }
    
    private String printAllGrades(int id, int grade, int stID){ 
        return "ExamID: " + id + " StudentID: " + stID + " Grade: " + grade + "\n\n";
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
