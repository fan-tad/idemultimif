package fr.univ_lyon1.info.m1.Itf;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univ_lyon1.info.m1.Metier.BelongsManager;
import fr.univ_lyon1.info.m1.Metier.ProjectManager;
import fr.univ_lyon1.info.m1.Metier.UserManager;
import fr.univ_lyon1.info.m1.Modele.Belongs;
import fr.univ_lyon1.info.m1.Modele.ObjetReponse;
import fr.univ_lyon1.info.m1.Modele.Project;
import fr.univ_lyon1.info.m1.Modele.User;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.json.JsonArray;
import javax.jws.soap.SOAPBinding;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

/**
 * Created by Team Sharkode on 16/11/16.
 */
@RestController
@RequestMapping("/api/project")
public class ProjectController {

    private EntityManager em = CreateEntity.em;
    private UserManager userManager = new UserManager(em);
    private ProjectManager projectManager = new ProjectManager(em);
    private BelongsManager belongsManager = new BelongsManager(em);

    /**
     * Permet à l'utilisateur de selectionner un projet dans la liste.
     *
     * @param idProjet   Id du projet selectionné par l'utilisateur.
     * @param session       Session HTML de l'utilisateur.
     * @return  ObjetReponse contenant des informations relatives au projet, ou un message d'erreur.
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ObjetReponse receiveGet(@RequestParam(value="project", required = true) String idProjet,
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

        List<Belongs> belongsListByUsers = belongsManager.getBelongsUsers(project.getProjectId());

        HashMap retour = new HashMap();
        retour.put("name", project.getProjectName());
        retour.put("id", project.getProjectId());

        List<HashMap> belongsList = new ArrayList<>();
        String role;
        if(belongsListByUsers != null) {
            for(Belongs belong : belongsListByUsers) {
                if(belong != null) {
                    HashMap userRights = new HashMap();
                    userRights.put("name", belong.getUser().getUserName());
                    int userRight = belong.getBelongsRight();

                    if (userRight == 0) {
                        role = "admin";
                    }
                    else if (userRight == 1) {
                        role = "dev";
                    }
                    else {
                        role = "reporter";
                    }
                    userRights.put("role", role);
                    belongsList.add(userRights);
                }
            }
        }
        retour.put("users", belongsList);
        return new ObjetReponse("success", retour, "");
    }

    /*
    rename project ok
    change user rights ok BD mais KO js
     */
    /**
     * Gère les POST sur un projet. Dans le cas "config", cette méthode permet de mettre à jour les informations d'un
     * projet.
     *
     * @param idProjet    Id du projet
     * @param newProjectName    Nouveau nom du projet, renvoyé par le formulaire.
     * @param users             Liste des utilisateurs et leurs droits respectifs.
     * @param session           Session HTML de l'utilisateur.
     * @return  ObjetReponse contenant les modifications du projet, ou un message d'erreur.
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ObjetReponse receivePost(@RequestParam(value="project", required = true) String idProjet,
                                    @RequestParam(value="name", required = true) String newProjectName,
                                    @RequestParam(value="users", required = true) String users,
                                    HttpServletResponse reponse,
                                    HttpSession session) {
        String monUserName = (String)session.getAttribute("userName");
        User userRequest = userManager.getUser(monUserName);
        if(userRequest == null) {
            return new ObjetReponse("redirect", "", "L'utilisateur n'existe plus");
        }

        int projectId = Integer.valueOf(idProjet);
        Project project = projectManager.getProject(projectId);
        if(project == null) {
            return new ObjetReponse("error", "", "Erreur lors de la transmission des données");
        }

        Belongs belongs = belongsManager.getBelongs(project.getProjectId(), userRequest.getUserId());
        if(belongs == null) {
            reponse.setStatus(403);
            return new ObjetReponse("error", "", "Vous ne faites pas partie du projet");
        }
        if(belongs.getBelongsRight() != 0) {
            return new ObjetReponse("error", "", "Vous n'avez pas les droits suffisants pour cette action");
        }

        String oldProjectName = project.getProjectName();
        if(!oldProjectName.equals(newProjectName)) {
            projectManager.updateProjectName(oldProjectName, newProjectName);
        }

        int userRight = 2;
        int managerId = -1;
        int managerRight = 0;
        boolean managerCanChange = false;
        boolean multipileUsers = false;
        ObjectMapper mapper = new ObjectMapper();
        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(users);
            JSONArray array = (JSONArray)obj;
            Iterator tab = array.iterator();
            System.out.println(array);
            if(array.size() > 1){
                multipileUsers = true;
            }
            if(multipileUsers) {
                while (tab.hasNext()) {
                    Object couple = tab.next();
                    Map<String, Object> map = mapper.readValue(couple.toString(), Map.class);
                    Iterator it = map.entrySet().iterator();

                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();

                        System.out.println(pair.getKey() + " = " + pair.getValue());
                        if (pair.getKey().equals("role")) {
                            String role = pair.getValue().toString();
                            if (role.equals("admin")) {
                                userRight = 0;
                            } else if (role.equals("dev")) {
                                userRight = 1;
                            } else {
                                userRight = 2;
                            }
                        }
                        if (pair.getKey().equals("name")) {
                            String userName = pair.getValue().toString();

                            User user = userManager.getUser(userName);
                            if (user != null) {
                                if (userName.equals(monUserName)) {
                                    managerRight = userRight;
                                    managerId = user.getUserId();
                                } else {
                                    if (userRight == 0) {
                                        managerCanChange = true;
                                    }
                                    belongs = belongsManager.updateUserRight(project.getProjectId(), user.getUserId(), userRight);
                                    if(belongs == null){
                                        if(belongsManager.addUserToProject(project, user,userRight) == null){
                                            return new ObjetReponse("error", "", "Erreur lors de l'insertion de " + user.getUserName());
                                        }
                                    }
                                }
                            }
                            else{
                                return new ObjetReponse("error", "", "L'utilisateur " + userName + " n'existe pas!");
                            }
                        }
                    }
                }
            }
            if(managerCanChange || managerRight==0) {
                belongsManager.updateUserRight(projectId, managerId, managerRight);
            }
            else {
                return new ObjetReponse("error", "", "Il faut au moins un administrateur par projet");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        }  catch (IOException e) {
            e.printStackTrace();
        }

        return new ObjetReponse();
    }
}
