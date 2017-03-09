package fr.univ_lyon1.info.m1.Itf;

import fr.univ_lyon1.info.m1.Metier.BelongsManager;
import fr.univ_lyon1.info.m1.Metier.ProjectManager;
import fr.univ_lyon1.info.m1.Metier.UserManager;
import fr.univ_lyon1.info.m1.Modele.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Team Sharkode on 20/11/16.
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectsController {
    private EntityManager em = CreateEntity.em;
    private UserManager userManager = new UserManager(em);
    private ProjectManager projectManager = new ProjectManager(em);
    private BelongsManager belongsManager = new BelongsManager(em);

    /**
     * Affiche la liste des projets auxquels un utilisateur est rattaché.
     *
     * @param session   Session HTML de l'utilisateur.
     * @return  ObjetReponse contenant la liste des projets de l'utilisateur, ou un message d'erreur.
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ObjetReponse receiveGet(HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        User user = userManager.getUser(userName);
        if(user == null) {
            return new ObjetReponse("redirect", "", "L'utilisateur " + userName + "n'existe plus");
        }
        List<Project> lp = projectManager.getProjects();
        if(lp != null) {
            List<HashMap> retour = new ArrayList<>();
            for (Project p : lp) {
                Belongs belongs = belongsManager.getBelongs(p.getProjectId(), user.getUserId());
                if(belongs != null) {
                    HashMap map = new HashMap();
                    map.put("name", p.getProjectName());
                    map.put("id", p.getProjectId());
                    map.put("description", p.getProjectDescription());
                    retour.add(map);
                }
            }

            return new ObjetReponse("success", retour, "");
        }
        else {
            return new ObjetReponse("error", "", "Aucun projet n'est disponible");
        }
    }

    /**
     * Gère les actions POSTS relatives à la liste des projets, tel que l'ajout et la suppression d'un projet.
     *
     * @param action        Action à effectuer sur le projet : ajout, supression...
     * @param nom           Nom du projet renvoyé par le formulaire.
     * @param description   Description du projet renvoyée par le formulaire.
     * @param session       Session HTML de l'utilisateur.
     * @return  ObjetReponse contenant la réussite ou l'échec de l'action.
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ObjetReponse receivePost(@RequestParam(value="action", required = true) String action,
                                    @RequestParam(value="nom", required = false) String nom,
                                    @RequestParam(value = "id", required = false) String idProjet,
                                    @RequestParam(value="description", required = false) String description,
                                    HttpServletResponse reponse,
                                    HttpSession session) {
        if(action.equals("delete")) {
            String userName = (String) session.getAttribute("userName");
            User user = userManager.getUser(userName);
            if(user == null) {
                return new ObjetReponse("redirect", "", "L'utilisateur " + userName + " n'existe plus");
            }

            if(idProjet == null) {
                return new ObjetReponse("error", "", "Erreur lors de la transmission des données");
            }

            int projectId = Integer.valueOf(idProjet);
            Project project = projectManager.getProject(projectId);
            if(project != null) {
                Belongs b = belongsManager.getBelongs(project.getProjectId(), user.getUserId());
                if(b == null) {
                    reponse.setStatus(403);
                    return new ObjetReponse("error", "", "Vous ne faites pas partie du projet");
                }

                if(b.getBelongsRight() != 0) {
                    return new ObjetReponse("erreur", "", "Vous n'avez pas les droits administrateur sur ce projet");
                }
                // l'utilisateur est admin du projet, il a donc le droit de le supprimer
                String projectName = project.getProjectName();
                projectManager.deleteProject(project);
                if(projectManager.getProject(projectId) == null) {
                    (new Vcs(userName, project.getProjectId(), true)).destroy();
                    return new ObjetReponse();
                }
                else {
                    return new ObjetReponse("error", "", "Une erreur est survenue lors de la suppression du projet");
                }
            }
            else {
                return new ObjetReponse("error", "", "Erreur lors de la transmission des données");
            }
        }
        else if(action.equals("create")) {
            if(description == null) {
                return new ObjetReponse("error", "", "Erreur lors de la transmission des données");
            }

            if(nom == null || nom.length() > 45) {
                return new ObjetReponse("erreur", "", "Erreur lors de la transmission de données");
            }

            if(projectManager.checkProject(nom) == null) {
                String userName = (String) session.getAttribute("userName");
                User user = userManager.getUser(userName);
                if(user == null) {
                    return new ObjetReponse("redirect", "", "L'utilisateur n'existe plus");
                }
                Vcs falseVcs = new Vcs(userName, -1, true);
                Project project = projectManager.newProject("Wiki " + nom, "Le wiki est vide. Modifiez-le à votre guise !", nom, falseVcs.getMasterPath(), description, user, 0);
                Vcs vcs = new Vcs(userName, project.getProjectId(), false);
                if(project != null) {
                    return new ObjetReponse();
                }
                else {
                    return new ObjetReponse("error", "", "Impossible de créer le projet");
                }
            }
            else {
                return new ObjetReponse("error", "", "Ce nom de projet est déjà pris");
            }
        }
        else {
            return new ObjetReponse("error", "", "Erreur lors de la transmission des données");
        }
    }
}
