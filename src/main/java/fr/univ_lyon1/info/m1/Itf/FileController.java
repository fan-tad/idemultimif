package fr.univ_lyon1.info.m1.Itf;

import fr.univ_lyon1.info.m1.Metier.BelongsManager;
import fr.univ_lyon1.info.m1.Metier.ProjectManager;
import fr.univ_lyon1.info.m1.Metier.TodoManager;
import fr.univ_lyon1.info.m1.Metier.UserManager;
import fr.univ_lyon1.info.m1.Modele.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Team Sharkode on 20/11/16.
 */
@RestController
@RequestMapping("/api/file")
public class FileController {

    private EntityManager em = CreateEntity.em;
    private UserManager userManager = new UserManager(em);
    private ProjectManager projectManager = new ProjectManager(em);
    private BelongsManager belongsManager = new BelongsManager(em);
    private TodoManager todoManager = new TodoManager(em);

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ObjetReponse receiveGET(@RequestParam(value = "path", required = true) String path,
                                   @RequestParam(value = "project", required = true) String idProjet,
                                   HttpSession session,
                                   HttpServletResponse reponse) {
        String userName = (String)session.getAttribute("userName");
        User user = userManager.getUser(userName);
        if(user == null) {
            return new ObjetReponse("redirect", "", "L'utilisateur " + userName + " n'existe plus");
        }

        int projectId = Integer.valueOf(idProjet);
        Project project = projectManager.getProject(projectId);
        if(project == null) {
            return new ObjetReponse("error", "", "Erreur lors de la tranmission des données");
        }

        Belongs belongs = belongsManager.getBelongs(project.getProjectId(), user.getUserId());
        if(belongs == null) {
            reponse.setStatus(403);
            return new ObjetReponse("error", "", "Vous ne faites pas partie du projet");
        }

        Vcs vcs = new Vcs(userName, project.getProjectId(), true);
        String retour = vcs.readFile(path);

        return new ObjetReponse("success", retour, "");
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ObjetReponse receivePost(@RequestParam(value = "action", required = true) String action,
                                    @RequestParam(value = "path", required = true) String path,
                                    @RequestParam(value = "path_dest", required = false) String pathDest,
                                    @RequestParam(value = "code", required = false) String code,
                                    @RequestParam(value = "project", required = true) String idProjet,
                                    HttpServletResponse reponse,
                                    HttpSession session) {
        String userName = (String)session.getAttribute("userName");
        User user = userManager.getUser(userName);
        if(user == null) {
            return new ObjetReponse("redirect", "", "L'utilisateur " + userName + " n'existe plus");
        }

        int projectId = Integer.valueOf(idProjet);
        Project project = projectManager.getProject(projectId);
        if(project == null) {
            return new ObjetReponse("error", "", "Erreur lors de la transmission de données");
        }

        Belongs belongs = belongsManager.getBelongs(project.getProjectId(), user.getUserId());
        if(belongs == null) {
            reponse.setStatus(403);
        }
        if(belongs.getBelongsRight() != 2) {
            Vcs vcs = new Vcs(userName, project.getProjectId(), true);
            List<String> todosContent = new ArrayList<>();

            switch (action) {
                case "create": {
                    boolean ef = vcs.existFile(path);
                    if (!ef) {
                        vcs.createFile(path);
                        return new ObjetReponse();
                    } else {
                        return new ObjetReponse("error", "", "Ce fichier existe déjà");
                    }
                }
                case "delete": {
                    boolean ef = vcs.existFile(path);
                    if (ef) {
                        vcs.removeFile(path);
                        return new ObjetReponse();
                    } else {
                        return new ObjetReponse("error", "", "Ce fichier n'existe pas");
                    }
                }
                case "move": {
                    boolean ef = vcs.existFile(path);
                    if (ef) {
                        if (pathDest == null) {
                            return new ObjetReponse("error", "", "Erreur lors de la transmission des données");
                        }
                        vcs.move(path, pathDest);
                        return new ObjetReponse();
                    } else {
                        return new ObjetReponse("error", "", "Ce fichier n'existe pas");
                    }
                }
                case "save": {
                    if (code == null) {
                        return new ObjetReponse("error", "", "Erreur lors de la transmission des données");
                    }

                    if (!vcs.existFile(path)) {
                        return new ObjetReponse("error", "", "Erreur lors de la transmission des données");
                    }
                    vcs.saveFile(path, code);
                    // Raffraichissement des todo à chaque sauvergarde des fichiers
                    todosContent = vcs.detectTodo(code);
                    todoManager.deleteTodoByFileNameAndProject(path, project);
                    for (String todo : todosContent) {
                        todoManager.newTodo(path, todo, project);
                    }
                    return new ObjetReponse();
                }
                default:
                    return new ObjetReponse("error", "", "Erreur lors de la transmission des données");
            }
        }
        else {
            return new ObjetReponse("error","","Vous n'avez pas le droit.");
        }
    }

    @RequestMapping(value = "{project}/**", method = RequestMethod.GET)
    @ResponseBody
    public String getFile(@PathVariable(value = "project", required = true) String idProjet,
                        //@PathVariable(value = "path", required = true) String path,
                        HttpServletResponse reponse, HttpServletRequest request,
                        HttpSession session) {

        String path = (String) request.getAttribute(
                HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        path = path.replace("api/file/"+idProjet+"/", "");
        System.out.println(path);

        String userName = (String)session.getAttribute("userName");
        User user = userManager.getUser(userName);
        if(user == null) {
            return null;
        }

        int projectId = Integer.valueOf(idProjet);
        Project project = projectManager.getProject(projectId);
        if(project == null) {
            return null;
        }


        Belongs belongs = belongsManager.getBelongs(project.getProjectId(), user.getUserId());
        if(belongs == null) {
            reponse.setStatus(403);
        }

        Vcs vcs = new Vcs(user.getUserName(), project.getProjectId(), true);
        return vcs.readFile(path);
    }
}
