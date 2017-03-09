package fr.univ_lyon1.info.m1.Itf;

import fr.univ_lyon1.info.m1.Metier.BelongsManager;
import fr.univ_lyon1.info.m1.Metier.ProjectManager;
import fr.univ_lyon1.info.m1.Metier.UserManager;
import fr.univ_lyon1.info.m1.Modele.*;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;

/**
 * Created by Team Sharkode on 21/10/16.
 */
@RestController
@RequestMapping("/api/folder")
public class FolderController {
    private EntityManager em = CreateEntity.em;
    private UserManager userManager = new UserManager(em);
    private ProjectManager projectManager = new ProjectManager(em);
    private BelongsManager belongsManager = new BelongsManager(em);

    /**
     * Récupère l'arborescence du dossier.
     *
     * @param path          Chemin du dossier.
     * @param idProjet   Id du projet.
     * @param session       Session HTML de l'utilisateur.
     * @return  ObjetReponse contenant l'arborescence du dossier.
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ObjetReponse receiveGET(@RequestParam(value="path", required = true) String path,
                                   @RequestParam(value = "project", required = true) String idProjet,
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

        Vcs vcs = new Vcs(userName, project.getProjectId(), true);
        HashMap retour = vcs.getTreeFrom(path);

        return new ObjetReponse("success", retour, "");
    }

    /**
     * Gère les actions POSTS relatives aux dossiers du projet, tel que l'ajout, la suppression et le déplacement d'un
     * dossier.
     *
     * @param action        Action à effectuer sur le dossier : ajout, supression...
     * @param path          Chemin du dossier.
     * @param pathDest      Nouveau chemin du dossier, en cas de déplacement.
     * @param idProjet   Id du projet.
     * @param session       Session HTML de l'utilisateur.
     * @return  ObjetReponse contenant la réussite ou l'échec de l'action.
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ObjetReponse receivePost(@RequestParam(value = "action", required = true) String action,
                                    @RequestParam(value = "path", required = true) String path,
                                    @RequestParam(value = "path_dest", required = false) String pathDest,
                                    @RequestParam(value = "project", required = true) String idProjet,
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

        if(belongs.getBelongsRight() != 2) {
            Vcs vcs = new Vcs(userName, project.getProjectId(), true);

            switch (action) {
                case "create": {
                    boolean ef = vcs.existFile(path);
                    if (!ef) {
                        vcs.createFolder(path);
                        return new ObjetReponse();
                    } else {
                        return new ObjetReponse("error", "", "Ce dossier existe déjà");
                    }
                }
                case "delete": {
                    boolean ef = vcs.existFile(path);
                    if (ef) {
                        vcs.removeFolder(path);
                        return new ObjetReponse();
                    } else {
                        return new ObjetReponse("error", "", "Ce dossier n'existe pas");
                    }
                }
                case "move": {
                    boolean ef = vcs.existFile(path);
                    if (ef) {
                        if (pathDest == null) {
                            return new ObjetReponse("error", "", "Erreur lors de la transmision des données");
                        }
                        vcs.move(path, pathDest);
                        return new ObjetReponse();
                    } else {
                        return new ObjetReponse("error", "", "Ce dossier n'existe pas");
                    }
                }
                default:
                    return new ObjetReponse("error", "", "Erreur lors de la transmission des données");
            }
        }
        else{
            return new ObjetReponse("error","","Vous n'avez pas le droit.");
        }
    }
}
