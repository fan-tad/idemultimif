package fr.univ_lyon1.info.m1.Itf;

import fr.univ_lyon1.info.m1.Metier.*;
import fr.univ_lyon1.info.m1.Modele.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.persistence.Persistence;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Team Sharkode on 21/10/16.
 */
@RestController
@RequestMapping("/api/todo")
public class TodoController {

    private EntityManager em = CreateEntity.em;
    private UserManager userManager = new UserManager(em);
    private ProjectManager projectManager = new ProjectManager(em);
    private BelongsManager belongsManager = new BelongsManager(em);
    private TodoManager todoManager = new TodoManager(em);

    /**
     * Récupère la TODOList du projet.
     *
     * @param idProjet   Id du projet.
     * @param session       Session HTML de l'utilisateur.
     * @return  ObjetReponse contenant la liste des TODOS du projet.
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ObjetReponse receiveGet(@RequestParam(value="project", required = true) String idProjet,
                                   HttpServletResponse reponse,
                                   HttpSession session) {
        String userName = (String)session.getAttribute("userName");
        User user  = userManager.getUser(userName);
        if(user == null) {
            return new ObjetReponse("redirect", "", "L'utilisateur " + userName + " n'existe plus");
        }

        int projectId = Integer.valueOf(idProjet);
        Project project = projectManager.getProject(projectId);
        if(project == null) {
            return new ObjetReponse("erreur", "", "Erreur lors de la transmission de données");
        }

        Belongs belongs = belongsManager.getBelongs(project.getProjectId(), user.getUserId());
        if(belongs == null) {
            reponse.setStatus(403);
            return new ObjetReponse("error", "", "Vous ne faites pas partie du projet");
        }

        List<Todo> lt = todoManager.getTodos(project);
        List<HashMap> retour = new ArrayList<>();
        for(Todo t : lt) {
            HashMap map = new HashMap();
            map.put("id", t.getTodoId());
            map.put("name", t.getTodoName());
            map.put("description", t.getTodoContent());
            retour.add(map);
        }

        return new ObjetReponse("success", retour, "");
    }

    /**
     * ATTENTION : Cette méthode n'est pas censée être appelée, mais existe pour empêcher le serveur de planter en cas
     * d'appel inattendu (ou forcé par une attaque du système) à cette méthode.
     *
     * @param reponse   Réponse du serveur.
     * @return  ObjetReponse contenant un retour d'erreur.
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ObjetReponse receivePost(HttpServletResponse reponse) {
        reponse.setStatus(405);
        return new ObjetReponse("error", "", "la demande n'est pas prise en compte (POST sur todo)");
    }
}
