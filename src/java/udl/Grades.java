/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package udl;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author adri
 */
@Entity
@Table(name = "GRADES")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Grades.findAll", query = "SELECT g FROM Grades g")
    , @NamedQuery(name = "Grades.findByStudentId", query = "SELECT g FROM Grades g WHERE g.gradesPK.studentId = :studentId")
    , @NamedQuery(name = "Grades.findByExamId", query = "SELECT g FROM Grades g WHERE g.gradesPK.examId = :examId")
    , @NamedQuery(name = "Grades.findByGrade", query = "SELECT g FROM Grades g WHERE g.grade = :grade")})
public class Grades implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected GradesPK gradesPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "GRADE")
    private int grade;
    @JoinColumn(name = "EXAM_ID", referencedColumnName = "EXAM_ID", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Exams exams;
    @JoinColumn(name = "STUDENT_ID", referencedColumnName = "STUDENT_ID", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Students students;

    public Grades() {
    }

    public Grades(GradesPK gradesPK) {
        this.gradesPK = gradesPK;
    }

    public Grades(GradesPK gradesPK, int grade) {
        this.gradesPK = gradesPK;
        this.grade = grade;
    }

    public Grades(int studentId, int examId) {
        this.gradesPK = new GradesPK(studentId, examId);
    }

    public GradesPK getGradesPK() {
        return gradesPK;
    }

    public void setGradesPK(GradesPK gradesPK) {
        this.gradesPK = gradesPK;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public Exams getExams() {
        return exams;
    }

    public void setExams(Exams exams) {
        this.exams = exams;
    }

    public Students getStudents() {
        return students;
    }

    public void setStudents(Students students) {
        this.students = students;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (gradesPK != null ? gradesPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Grades)) {
            return false;
        }
        Grades other = (Grades) object;
        if ((this.gradesPK == null && other.gradesPK != null) || (this.gradesPK != null && !this.gradesPK.equals(other.gradesPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "udl.Grades[ gradesPK=" + gradesPK + " ]";
    }
    
}
