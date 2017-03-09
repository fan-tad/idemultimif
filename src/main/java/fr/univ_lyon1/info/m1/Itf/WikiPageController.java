package fr.univ_lyon1.info.m1.Itf;

import fr.univ_lyon1.info.m1.Metier.*;
import fr.univ_lyon1.info.m1.Modele.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Team Sharkode on 11/11/16.
 */
@RestController
@RequestMapping("/api/wiki")
@SessionAttributes("user")
public class WikiPageController {
    private EntityManager em = CreateEntity.em;
    private UserManager userManager = new UserManager(em);
    private ProjectManager projectManager = new ProjectManager(em);
    private BelongsManager belongsManager = new BelongsManager(em);
    private WikiPageManager wikiPageManager = new WikiPageManager(em);


    /**
     * Récupère le contenu du WikiPage d'un projet, et de le formater dans un objet type JSON
     * Pour le renvoyer au Front End.
     *
     * @param idProjet   Id du projet renvoyé par le JavaScript pour la requête SQL.
     * @return  ObjetReponse contenant le Wiki du projet, ou une erreur si le Wiki n'a pas été trouvé.
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ObjetReponse receiveGet(@RequestParam(value = "project", required = true) String idProjet,
                                   HttpServletResponse reponse,
                                   HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        User user = userManager.getUser(userName);
        if(user == null) {
            return new ObjetReponse("redirect", "", "L'utilisateur " + userName + " n'existe plus");
        }

        int projectId = Integer.valueOf(idProjet);
        Project project = projectManager.getProject(projectId);
        if(project == null) {
            return new ObjetReponse("error", "", "Erreur lors de la transmission des données");
        }

        Belongs belongs = belongsManager.getBelongs(project.getProjectId(), user.getUserId());
        if(belongs == null) {
            reponse.setStatus(403);
            return new ObjetReponse("error", "", "Vous ne faitespas partie du projet");
        }

       // WikiPage wikiPage = manager.getwiki(project.getProjectName());
        WikiPage wikiPage = wikiPageManager.getWIkiByProject(project);
        if (wikiPage != null) {
            String wiki = wikiPage.getWikiPageContent();
            Map mapA = new HashMap();
            mapA.put("text", wiki);
            return new ObjetReponse("success", mapA, "wiki");
        } else {
            return new ObjetReponse("error", "", "Wiki non trouvé. Contactez un administrateur.");
        }
    }

    /**
     * Modifie le contenu du WikiPage d'un projet avec le texte du formulaire.
     *
     * @param idProjet Id du projet renvoyé par le JavaScript.
     * @param wikiContent Nouveau contenu du Wiki renvoyé par le JavaScript.
     * @return ObjetReponse contenant une confirmation du changement, ou une erreur si le Wiki n'a pas été trouvé.
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ObjetReponse receivePost(@RequestParam(value = "text", required = true) String wikiContent,
                                    @RequestParam(value = "project", required = true) String idProjet,
                                    HttpServletResponse reponse,
                                    HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        User user = userManager.getUser(userName);
        if(user == null) {
            return new ObjetReponse("redirect", "", "L'utilisateur " + userName + " n'existe plus");
        }

        int projectId = Integer.valueOf(idProjet);
        Project project = projectManager.getProject(projectId);
        if(project == null) {
            return new ObjetReponse("error", "", "Erreur lors de la transmission des données");
        }

        Belongs belongs = belongsManager.getBelongs(project.getProjectId(), user.getUserId());
        if(belongs == null) {
            reponse.setStatus(403);
            return new ObjetReponse("error", "", "Vous ne faitespas partie du projet");
        }

        WikiPage wikiPage = wikiPageManager.getWIkiByProject(project);
        if (wikiPage != null && wikiContent.length() <= 16777200) {
            wikiPageManager.setWiki(wikiContent, project);
            return new ObjetReponse("success", "", "wiki");
        } else {
            return new ObjetReponse("error", "", "Wiki non trouvé. Contactez un administrateur.");
        }
    }

}

