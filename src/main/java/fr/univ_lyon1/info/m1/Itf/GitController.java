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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Team Sharkode on 21/10/16.
 */
@RestController
@RequestMapping("/api/git")
public class GitController {
    private EntityManager em = CreateEntity.em;
    private UserManager userManager = new UserManager(em);
    private ProjectManager projectManager = new ProjectManager(em);
    private BelongsManager belongsManager = new BelongsManager(em);

    /**
     * Renvoie la liste des commits d'une branche, ou du projet si le nom de la branche est vide.
     *
     * @param branch      Nom de la branche à visionner.
     * @param number      Nombre de commits à retourner (retourner, par exemple, les 5 derniers uniquement).
     * @param idProjet Id du projet selectionné par l'utilisateur.
     * @param session     Session HTML de l'utilisateur.
     * @return ObjetReponse contenant les commits du projet, ou un message d'erreur.
     */
    @RequestMapping(value = "commits", method = RequestMethod.GET)
    @ResponseBody
    public ObjetReponse receiveGet(@RequestParam(value = "branch", required = true) String branch,
                                   @RequestParam(value = "number", required = true) String number,
                                   @RequestParam(value = "project", required = true) String idProjet,
                                   HttpServletResponse reponse,
                                   HttpSession session) {
        int numero = Integer.valueOf(number);
        if(numero > 0) {
            String userName = (String)session.getAttribute("userName");
            User user = userManager.getUser(userName);
            if(user == null) {
                return new ObjetReponse("redirect", "", "L'utilisateur " + userName + " n'existe plus");
            }

            int projectId = Integer.valueOf(idProjet);
            Project project = projectManager.getProject(projectId);
            if (project == null) {
                return new ObjetReponse("error", "", "Erreur lors de la transmission des données");
            }

            Belongs belongs = belongsManager.getBelongs(project.getProjectId(), user.getUserId());
            if(belongs == null) {
                return new ObjetReponse("error", "", "Vous ne faites pas partie du projet");
            }

            Vcs vcs = new Vcs(userName, project.getProjectId(), false);
            ArrayList<HashMap<String, String>> temp = vcs.getCommits(branch);
            List<HashMap<String, String>> retour;

            if(numero > temp.size()){
                retour = temp.subList(0, temp.size());
            } else {
                retour = temp.subList(0, numero);
            }

            return new ObjetReponse("success", retour, "");
        } else {
            return new ObjetReponse("error", "", "Erreur lors de la transmission des données");
        }
    }

    @RequestMapping(value = "compile", method = RequestMethod.POST)
    @ResponseBody
    public ObjetReponse compile(@RequestParam(value = "project", required = true) String idProjet,
                                @RequestParam(value = "lang", required = true) String lang,
                               HttpServletResponse reponse,
                               HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        User user = userManager.getUser(userName);
        if (user == null) {
            return new ObjetReponse("redirect", "", "L'utilisateur " + userName + " n'existe plus");
        }

        int projectId = Integer.valueOf(idProjet);
        Project project = projectManager.getProject(projectId);
        if (project == null) {
            return new ObjetReponse("error", "", "Erreur lors de la transmission des données");
        }

        Belongs belongs = belongsManager.getBelongs(project.getProjectId(), user.getUserId());
        if (belongs == null) {
            reponse.setStatus(403);
            return new ObjetReponse("error", "", "Vous ne faites pas partie du projet");
        }

        if(belongs.getBelongsRight() != 2) {
            Vcs vcs = new Vcs(userName, project.getProjectId(), true);

            vcs.compile(lang);
            return new ObjetReponse();
        }
        else {
            return new ObjetReponse("error","","Vous n'avez pas le droit.");
        }
    }

    @RequestMapping(value = "commit", method = RequestMethod.POST)
    @ResponseBody
    public ObjetReponse commit(@RequestParam(value = "message", required = true) String message,
                               @RequestParam(value = "project", required = true) String idProjet,
                               HttpServletResponse reponse,
                               HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        User user = userManager.getUser(userName);
        if (user == null) {
            return new ObjetReponse("redirect", "", "L'utilisateur " + userName + " n'existe plus");
        }

        int projectId = Integer.valueOf(idProjet);
        Project project = projectManager.getProject(projectId);
        if (project == null) {
            return new ObjetReponse("error", "", "Erreur lors de la transmission des données");
        }

        Belongs belongs = belongsManager.getBelongs(project.getProjectId(), user.getUserId());
        if(belongs == null) {
            reponse.setStatus(403);
            return new ObjetReponse("error", "", "Vous ne faites pas partie du projet");
        }
        if(belongs.getBelongsRight() != 2) {
            Vcs vcs = new Vcs(userName, project.getProjectId(), false);

            vcs.add(".");
            vcs.commit(message);
            return new ObjetReponse();
        }else{
            return new ObjetReponse("error","","Vous n'avez pas le droit.");
        }
    }

    @RequestMapping(value = "push", method = RequestMethod.POST)
    @ResponseBody
    public ObjetReponse push(@RequestParam(value = "project", required = true) String idProjet,
                               HttpServletResponse reponse,
                               HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        User user = userManager.getUser(userName);
        if (user == null) {
            return new ObjetReponse("redirect", "", "L'utilisateur " + userName + " n'existe plus");
        }

        int projectId = Integer.valueOf(idProjet);
        Project project = projectManager.getProject(projectId);
        if (project == null) {
            return new ObjetReponse("error", "", "Erreur lors de la transmission des données");
        }

        Belongs belongs = belongsManager.getBelongs(project.getProjectId(), user.getUserId());
        if (belongs == null) {
            reponse.setStatus(403);
            return new ObjetReponse("error", "", "Vous ne faites pas partie du projet");
        }

        if(belongs.getBelongsRight() != 2) {
            Vcs vcs = new Vcs(userName, project.getProjectId(), false);

            vcs.push();
            return new ObjetReponse();
        }
        else{
            return new ObjetReponse("error","","Vous n'avez pas le droit.");
        }
    }

    @RequestMapping(value = "pull", method = RequestMethod.POST)
    @ResponseBody
    public ObjetReponse pull(@RequestParam(value = "project", required = true) String idProjet,
                             HttpServletResponse reponse,
                             HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        User user = userManager.getUser(userName);
        if (user == null) {
            return new ObjetReponse("redirect", "", "L'utilisateur " + userName + " n'existe plus");
        }

        int projectId = Integer.valueOf(idProjet);
        Project project = projectManager.getProject(projectId);
        if (project == null) {
            return new ObjetReponse("error", "", "Erreur lors de la transmission des données");
        }

        Belongs belongs = belongsManager.getBelongs(project.getProjectId(), user.getUserId());
        if (belongs == null) {
            reponse.setStatus(403);
            return new ObjetReponse("error", "", "Vous ne faites pas partie du projet");
        }

        if(belongs.getBelongsRight() != 2) {
            Vcs vcs = new Vcs(userName, project.getProjectId(), false);
            vcs.pull();
            return new ObjetReponse();
        } else{
            return new ObjetReponse("error","","Vous n'avez pas le droit.");
        }
    }

    @RequestMapping(value = "branch/checkout", method = RequestMethod.POST)
    @ResponseBody
    public ObjetReponse checkout(@RequestParam(value = "branch", required = true) String branch,
                                     @RequestParam(value = "project", required = true) String idProjet,
                                     HttpServletResponse reponse,
                                     HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        User user = userManager.getUser(userName);
        if (user == null) {
            return new ObjetReponse("redirect", "", "L'utilisateur " + userName + " n'existe plus");
        }

        int projectId = Integer.valueOf(idProjet);
        Project project = projectManager.getProject(projectId);
        if (project == null) {
            return new ObjetReponse("error", "", "Erreur lors de la transmission des données");
        }

        Belongs belongs = belongsManager.getBelongs(project.getProjectId(), user.getUserId());
        if (belongs == null) {
            reponse.setStatus(403);
            return new ObjetReponse("error", "", "Vous ne faites pas partie du projet");
        }

        Vcs vcs = new Vcs(userName, project.getProjectId(), false);

        vcs.checkout(branch);
        return new ObjetReponse();
    }

    @RequestMapping(value = "branch/create", method = RequestMethod.POST)
    @ResponseBody
    public ObjetReponse createBranch(@RequestParam(value = "branch", required = true) String branch,
                                    @RequestParam(value = "project", required = true) String idProjet,
                                    HttpServletResponse reponse,
                                    HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        User user = userManager.getUser(userName);
        if (user == null) {
            return new ObjetReponse("redirect", "", "L'utilisateur " + userName + " n'existe plus");
        }

        int projectId = Integer.valueOf(idProjet);
        Project project = projectManager.getProject(projectId);
        if (project == null) {
            return new ObjetReponse("error", "", "Erreur lors de la transmission des données");
        }

        Belongs belongs = belongsManager.getBelongs(project.getProjectId(), user.getUserId());
        if (belongs == null) {
            reponse.setStatus(403);
            return new ObjetReponse("error", "", "Vous ne faites pas partie du projet");
        }

        if(belongs.getBelongsRight() != 2) {
            Vcs vcs = new Vcs(userName, project.getProjectId(), false);

            vcs.createBranch(branch);
            return new ObjetReponse();
        }
        else{
            return new ObjetReponse("error","","Vous n'avez pas le droit.");
        }
    }

    @RequestMapping(value = "branch/delete", method = RequestMethod.POST)
    @ResponseBody
    public ObjetReponse deleteBranch(@RequestParam(value = "branch", required = true) String branch,
                                     @RequestParam(value = "project", required = true) String idProjet,
                                     HttpServletResponse reponse,
                                     HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        User user = userManager.getUser(userName);
        if (user == null) {
            return new ObjetReponse("redirect", "", "L'utilisateur " + userName + " n'existe plus");
        }

        int projectId = Integer.valueOf(idProjet);
        Project project = projectManager.getProject(projectId);
        if (project == null) {
            return new ObjetReponse("error", "", "Erreur lors de la transmission des données");
        }

        Belongs belongs = belongsManager.getBelongs(project.getProjectId(), user.getUserId());
        if (belongs == null) {
            reponse.setStatus(403);
            return new ObjetReponse("error", "", "Vous ne faites pas partie du projet");
        }

        if(belongs.getBelongsRight() != 2) {
            Vcs vcs = new Vcs(userName, project.getProjectId(), false);

            vcs.deleteBranch(branch);
            return new ObjetReponse();
        }else{
            return new ObjetReponse("error","","Vous n'avez pas le droit.");
        }
    }

    @RequestMapping(value = "branch/merge", method = RequestMethod.POST)
    @ResponseBody
    public ObjetReponse mergeBranch(@RequestParam(value = "branch", required = true) String branch,
                                     @RequestParam(value = "branchDest", required = true) String branchDest,
                                     @RequestParam(value = "project", required = true) String idProjet,
                                     HttpServletResponse reponse,
                                     HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        User user = userManager.getUser(userName);
        if (user == null) {
            return new ObjetReponse("redirect", "", "L'utilisateur " + userName + " n'existe plus");
        }

        int projectId = Integer.valueOf(idProjet);
        Project project = projectManager.getProject(projectId);
        if (project == null) {
            return new ObjetReponse("error", "", "Erreur lors de la transmission des données");
        }

        Belongs belongs = belongsManager.getBelongs(project.getProjectId(), user.getUserId());
        if (belongs == null) {
            reponse.setStatus(403);
            return new ObjetReponse("error", "", "Vous ne faites pas partie du projet");
        }

        if(belongs.getBelongsRight() != 2) {
            Vcs vcs = new Vcs(userName, project.getProjectId(), false);

            vcs.mergeBranch(branch, branchDest);
            return new ObjetReponse();
        }
        else {
            return new ObjetReponse("error","","Vous n'avez pas le droit.");
        }
    }


    @RequestMapping(value = "branches", method = RequestMethod.GET)
    @ResponseBody
    public ObjetReponse getBranches(@RequestParam(value = "project", required = true) String idProjet,
                                    HttpServletResponse reponse,
                                    HttpSession session){

        String userName = (String) session.getAttribute("userName");
        User user = userManager.getUser(userName);
        if (user == null) {
            return new ObjetReponse("redirect", "", "L'utilisateur " + userName + " n'existe plus");
        }

        int projectId = Integer.valueOf(idProjet);
        Project project = projectManager.getProject(projectId);
        if (project == null) {
            return new ObjetReponse("error", "", "Erreur lors de la transmission des données");
        }

        Belongs belongs = belongsManager.getBelongs(project.getProjectId(), user.getUserId());
        if (belongs == null) {
            reponse.setStatus(403);
            return new ObjetReponse("error", "", "Vous ne faites pas partie du projet");
        }
        Vcs vcs = new Vcs(userName, project.getProjectId(), false);

        HashMap retour = new HashMap<>();
        retour.put("current", vcs.getCurrentBranch());
        retour.put("all", vcs.getBranchesList());

        return new ObjetReponse("success", retour, "");

    }
}