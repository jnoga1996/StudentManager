package com.smanager.dao.models;

import org.hibernate.annotations.ManyToAny;

import javax.persistence.*;

@Entity
@Table(name = "ASSIGNMENT_SOLUTIONS")
public class AssignmentSolution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "assignment_id", referencedColumnName = "assignment_id",
        foreignKey = @ForeignKey(name = "AssignmentSolution_Assignment_FK"))
    private Assignment assignment;

    @ManyToOne
    @JoinColumn(name = "solution_id", referencedColumnName = "solution_id",
        foreignKey = @ForeignKey(name = "AssignmentSolution_Solution_FK"))
    private Solution solution;

    public AssignmentSolution() {};

    public AssignmentSolution(Assignment assignment, Solution solution) {
        this.assignment = assignment;
        this.solution = solution;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public Solution getSolution() {
        return solution;
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
    }

    @Override
    public String toString() {
        return "assignmentId: " + getAssignment().getId() + ", solutionId: " + getSolution().getId();
    }

}
