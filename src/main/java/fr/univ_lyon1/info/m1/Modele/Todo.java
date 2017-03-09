package fr.univ_lyon1.info.m1.Modele;

import javax.persistence.*;

@Entity
@NamedQueries({
        @NamedQuery(name="Todo.findById", query="SELECT t FROM Todo t WHERE t.todoId = :todoId"),
        @NamedQuery(name="Todo.findAllByProject", query="SELECT t FROM Todo t WHERE t.project = :project"),
        @NamedQuery(name="Todo.findAllByNameAndProjectId", query="SELECT t FROM Todo t WHERE t.todoName = :todoName and t.project.projectId = :projectId"),

})
public class Todo {

    /**
     * Identifiant du todo
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todoId")
    private int todoId;

    /**
     * Nom du todo
     */
    @Column(name = "todoName")
    private String todoName;

    /**
     * Contenu du todo
     */
    @Column(name = "todoContent")
    private String todoContent;

    /**
     * Fichier du todo
     */
    @ManyToOne
    @JoinColumn(name="projectId")
    private Project project;

    /**
     * Constructeur par défaut du todo
     */
    public Todo() {
        this.todoName = "";
        this.todoContent = "";
        this.project = null;
    }

    /**
     * Constructeur paramétrique du todo
     * @param todoName Nom du todo
     * @param todoContent Contenu du todo
     * @param project Fichier du todo
     */
    public Todo(String todoName, String todoContent, Project project) {
        this.todoName = todoName;
        this.todoContent = todoContent;
        this.project = project;
    }

    /**
     * Retourne l'identifiant du todo
     * @return Identifiant du todo
     */
    public int getTodoId() {
        return todoId;
    }

    /**
     * Spécifie l'identifiant du todo
     * @param todoId Identifiant du todo
     */
    public void setTodoId(int todoId) {
        this.todoId = todoId;
    }

    /**
     * Retourne le nom du todo
     * @return Nom du todo
     */
    public String getTodoName() {
        return todoName;
    }

    /**
     * Spécifie le nom du todo
     * @param todoName Nom du todo
     */
    public void setTodoName(String todoName) {
        this.todoName = todoName;
    }

    /**
     * Retourne le contenu du todo
     * @return Contenu du todo
     */
    public String getTodoContent() {
        return todoContent;
    }

    /**
     * Spécifie le contenu du todo
     * @param todoContent Contenu du todo
     */
    public void setTodoContent(String todoContent) {
        this.todoContent = todoContent;
    }

    /**
     * Retourne le projet du du todo
     * @return Projet du todo
     */
    public Project getProject() {
        return project;
    }

    /**
     * Spécifie le projet du todo
     * @param project Projet du todo
     */
    public void setProject(Project project) {
        this.project = project;
    }
}
