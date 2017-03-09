package fr.univ_lyon1.info.m1.Metier;

import fr.univ_lyon1.info.m1.DAO.*;
import fr.univ_lyon1.info.m1.Modele.Belongs;
import fr.univ_lyon1.info.m1.Modele.Project;
import fr.univ_lyon1.info.m1.Modele.User;
import fr.univ_lyon1.info.m1.Modele.WikiPage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import java.util.List;

@Transactional
public class ProjectManager {

    private ManagerUtility managerUtility;
    @PersistenceContext(unitName = "pu-sharkode")
    private EntityManager em;
    private BelongsDao belongsDao;
    private ForumPostDao forumPostDao;
    private ForumThreadDao forumThreadDao;
    private ProjectDao projectDao;
    private TicketDao ticketDao;
    private TodoDao todoDao;
    private UserDao userDao;
    private WikiPageDao wikiPageDao;

    public ProjectManager(EntityManager em) {
        //this.em = em;
        this.managerUtility = new ManagerUtility();
        this.belongsDao = new BelongsDao(em);
        this.forumPostDao = new ForumPostDao(em);
        this.forumThreadDao = new ForumThreadDao(em);
        this.projectDao = new ProjectDao(em);
        this.ticketDao = new TicketDao(em);
        this.todoDao = new TodoDao(em);
        this.userDao = new UserDao(em);
        this.wikiPageDao = new WikiPageDao(em);
    }


    public Project newProject(String wikiPageName, String wikiPageContent,
                              String projectName, String projectPath, String projectDescription,
                              User user, int belongsRight) {

        EntityTransaction transaction = managerUtility.getTransaction(em);
        transaction.begin();

        Project project = projectDao.createProject(projectName, projectPath, projectDescription);
        WikiPage wikiPage = wikiPageDao.createWikiPage(wikiPageName, wikiPageContent, project);

        Belongs belongs = belongsDao.createBelongs(belongsRight, project, user);

        project.addBelongs(belongs);
        user.addBelongs(belongs);

        transaction.commit();
        return project;
    }

    public void deleteProject(Project project) {
        EntityTransaction transaction = managerUtility.getTransaction(em);

        transaction.begin();

        belongsDao.removeBelongs(project.getProjectId(), project.getBelongs().get(0).getUser().getUserId());

        int a = ticketDao.getTicketsByProject(project).size();

        for(int i = 0; i < a; i++) {
            ticketDao.removeTicket(ticketDao.getTicketsByProject(project).get(0).getTicketId());
        }

        a = todoDao.getTodosByProject(project).size();

        for(int i = 0; i < a; i++) {
            todoDao.removeTodo(todoDao.getTodosByProject(project).get(0).getTodoId());
        }

        a = forumThreadDao.getForumThreadsByProject(project).size();

        for(int i = 0; i < a; i++) {
            int b = forumPostDao.getForumPostsByForumThread(forumThreadDao.getForumThreadsByProject(project).get(0)).size();
            for(int j = 0; j < b; j++) {
                forumPostDao.removeForumPost(forumPostDao.getForumPostsByForumThread(forumThreadDao.getForumThreadsByProject(project).get(0)).get(0).getForumPostId());
            }
            forumThreadDao.removeForumThread(forumThreadDao.getForumThreadsByProject(project).get(0).getForumThreadId());
        }

        wikiPageDao.removeWikiPage(wikiPageDao.getWikiPageByProject(project).getWikiPageId());

        projectDao.removeProject(project.getProjectName());

        transaction.commit();
    }

    public Project checkProject(String name) {
        EntityTransaction transaction = managerUtility.getTransaction(em);
        transaction.begin();

        Project project = projectDao.getProjectByName(name);

        transaction.commit();
        return project;
    }

    public List<Project> getProjects() {
        EntityTransaction transaction = managerUtility.getTransaction(em);
        transaction.begin();

        List<Project> lp = projectDao.getAllProjects();

        transaction.commit();
        return lp;
    }

    public Project getProject(int id) {
        EntityTransaction transaction = managerUtility.getTransaction(em);
        transaction.begin();

        Project project = projectDao.getProjectById(id);

        transaction.commit();
        return project;
    }

    public void updateProjectName(String oldProjectName, String newProjectName){
        EntityTransaction transaction = managerUtility.getTransaction(em);
        transaction.begin();

        Project project = projectDao.getProjectByName(oldProjectName);
        projectDao.updateProjectName(project, newProjectName);

        transaction.commit();
    }

}
