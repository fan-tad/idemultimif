package fr.univ_lyon1.info.m1.Itf;

import fr.univ_lyon1.info.m1.Metier.BelongsManager;
import fr.univ_lyon1.info.m1.Metier.ProjectManager;
import fr.univ_lyon1.info.m1.Metier.UserManager;
import fr.univ_lyon1.info.m1.Modele.*;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.ApplicationScope;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Team Sharkode on 20/11/16.
 */
@RestController
@ApplicationScope
@RequestMapping("/api/message")
public class ChatController{
    private EntityManager em = CreateEntity.em;
    private UserManager userManager = new UserManager(em);
    private ProjectManager projectManager = new ProjectManager(em);
    private BelongsManager belongsManager = new BelongsManager(em);


    private HashMap<Integer, ArrayList<Message>> salons = new HashMap();

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ObjetReponse receiveGet(@RequestParam(value="numero", required = true) int numero,
                                   @RequestParam(value="project", required = true) String idProjet,
                                   HttpServletResponse reponse,
                                   HttpSession session){
        String userName = (String) session.getAttribute("userName");
        int projectId = Integer.valueOf(idProjet);
        User user = userManager.getUser(userName);
        if(user == null) {
            return new ObjetReponse("redirect", "", "L'utilisateur n'existe plus");
        }


        Project project = projectManager.getProject(projectId);
        if(project == null) {
            return new ObjetReponse("error", "", "Erreur lors de la transmission des données");
        }

        Belongs belongs = belongsManager.getBelongs(project.getProjectId(), user.getUserId());
        if(belongs == null) {
            reponse.setStatus(403);
            return new ObjetReponse("error", "", "Vous ne faites pas partie du projet");
        }

        ArrayList<Message> messages = new ArrayList<>();
        if(salons.get(projectId) != null) {
            messages = salons.get(projectId);
        }

        if(numero > messages.size()) {
            return new ObjetReponse("error", "", "Erreur lors de la transmission des données");
        }

        List<Message> retour = new ArrayList<>();
        if(numero == messages.size()) {
            reponse.setStatus(304);
        }
        else {
            if(messages.size() > 0) {
                retour = messages.subList(numero, messages.size());
            }
        }

        return new ObjetReponse("success", retour, "");
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ObjetReponse receivePost(@RequestParam(value="message", required = true) String message,
                                    @RequestParam(value="project", required = true) String idProjet,
                                    HttpServletResponse reponse,
                                    HttpSession session){
        String userName = (String) session.getAttribute("userName");
        User user = userManager.getUser(userName);
        if(user == null) {
            return new ObjetReponse("redirect", "", "L'utilisateur n'existe plus");
        }

        int projectId = Integer.valueOf(idProjet);
        Project project = projectManager.getProject(projectId);
        if(project == null) {
            return new ObjetReponse("error", "", "Erreur lors de la transmission des données");
        }

        Belongs belongs = belongsManager.getBelongs(project.getProjectId(), user.getUserId());
        if(belongs == null) {
            reponse.setStatus(403);
            return new ObjetReponse("error", "", "Vous ne faites pas partie du projet");
        }
        if(!message.isEmpty()) {
            Message msg = new Message(userName, message);
            if (salons.get(project.getProjectId()) == null) {
                salons.put(project.getProjectId(), new ArrayList<Message>());
            }
            salons.get(project.getProjectId()).add(msg);
        }
        return new ObjetReponse();
    }
}
