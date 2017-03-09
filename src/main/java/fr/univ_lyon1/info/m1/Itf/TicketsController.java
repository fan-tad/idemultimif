package fr.univ_lyon1.info.m1.Itf;

import fr.univ_lyon1.info.m1.Metier.BelongsManager;
import fr.univ_lyon1.info.m1.Metier.ProjectManager;
import fr.univ_lyon1.info.m1.Metier.TicketManager;
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
 * Created by Team Sharkode on 21/10/16.
 */
@RestController
@RequestMapping("/api/tickets")
public class TicketsController {

    private EntityManager em = CreateEntity.em;
    private UserManager userManager = new UserManager(em);
    private ProjectManager projectManager = new ProjectManager(em);
    private BelongsManager belongsManager = new BelongsManager(em);
    private TicketManager ticketManager = new TicketManager(em);

    /**
     * Affiche la liste des tickets rattachés au projet selectionné.
     *
     * @param idProjet    Id du projet.
     * @return  ObjetReponse contenant la liste des tickets du projet, ou un message d'erreur.
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ObjetReponse receiveGet(@RequestParam(value = "project", required = true) String idProjet,
                                   HttpServletResponse reponse,
                                   HttpSession session) {
        String userName = (String)session.getAttribute("userName");
        User owner = userManager.getUser(userName);
        if(owner == null) {
            return new ObjetReponse("redirect", "", "L'utilisateur " + userName + " n'existe plus");
        }

        int projectId = Integer.valueOf(idProjet);
        Project project = projectManager.getProject(projectId);
        if(project == null) {
            return new ObjetReponse("error", "", "Erreur lors de la transmission des données");
        }

        Belongs belongs = belongsManager.getBelongs(project.getProjectId(), owner.getUserId());
        if(belongs == null) {
            reponse.setStatus(403);
            return new ObjetReponse("error", "", "Vous ne faites pas partie du projet");
        }

        if (project != null) {
            List<Ticket> lt = ticketManager.getTickets(project);
            List<HashMap> retour = new ArrayList<>();
            if (lt.size() > 0) {
                for (Ticket t : lt) {
                    HashMap map = new HashMap();
                    map.put("id", t.getTicketId());
                    map.put("name", t.getTicketName());
                    map.put("badge", t.getTicketBadge());
                    retour.add(map);
                }

            }
            return new ObjetReponse("success", retour, "");
        } else {
            return new ObjetReponse("error", "", "Erreur lors de la transmission des données");
        }
    }

    /**
     * Gère les actions POSTS relatives à la liste des tickets, tel que l'ajout et la suppression d'un ticket.
     *
     * @param action            Action à effectuer sur le ticket : ajout, supression...
     * @param idTicketString    Id du ticket dans le cas d'une suppression.
     * @param nom               Nom du ticket renvoyé par le formulaire.
     * @param description       Description du ticket renvoyée par le formulaire.
     * @param badge             Type du ticket (Feature, Bug...).
     * @param userAssigned      Nom de l'utilisateur assigné à la tâche.
     * @param idProjet       Id du projet.
     * @param session           Session HTML de l'utilisateur.
     * @return  ObjetReponse contenant la réussite ou l'échec de l'action.
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ObjetReponse receivePost(@RequestParam(value = "action", required = true) String action,
                                    @RequestParam(value = "id", required = false) String idTicketString,
                                    @RequestParam(value = "nom", required = false) String nom,
                                    @RequestParam(value = "description", required = false) String description,
                                    @RequestParam(value = "badge", required = false) String badge,
                                    @RequestParam(value = "userAssigned", required = false) String userAssigned,
                                    @RequestParam(value = "project", required = true) String idProjet,
                                    HttpServletResponse reponse,
                                    HttpSession session) {
        String userName = (String)session.getAttribute("userName");
        User owner = userManager.getUser(userName);
        if(owner == null) {
            return new ObjetReponse("redirect", "", "L'utilisateur " + userName + " n'existe plus");
        }

        int projectId = Integer.valueOf(idProjet);
        Project project = projectManager.getProject(projectId);
        if(project == null) {
            return new ObjetReponse("error", "", "Erreur lors de la transmission des données");
        }

        Belongs belongs = belongsManager.getBelongs(project.getProjectId(), owner.getUserId());
        if(belongs == null) {
            reponse.setStatus(403);
            return new ObjetReponse("error", "", "Vous ne faites pas partie du projet");
        }

        if (action.equals("create")) {
            if((nom == null) || (description == null) || (badge == null)) {
                return new ObjetReponse("error", "", "Erreur lors de la transmission des données");
            }

            if(nom.length() > 45) {
                return new ObjetReponse("error", "", "Le nom du ticket est trop long");
            }

            if (nom.equals("") || !(badge.equals("bug") || badge.equals("feature"))) {
                return new ObjetReponse("error", "", "Erreur lors de la transmission des données");
            }

            Ticket t = null;

            if (userAssigned != null) {
                User assigned = userManager.getUser(userAssigned);
                t = ticketManager.newTicketWithUserAssigned(nom, badge, description, project, owner, assigned);
            } else {
                t = ticketManager.newTicketWithoutUserAssigned(nom, badge, description, project, owner);
            }

            if (t != null) {
                return new ObjetReponse();
            } else {
                return new ObjetReponse("error", "", "Une erreur interne a empêché de créer le ticket");
            }
        } else if (action.equals("delete")) {
            if (idTicketString != null) {
                int idTicket = Integer.valueOf(idTicketString);
                ticketManager.deleteTicket(idTicket);
                return new ObjetReponse();
            } else {
                return new ObjetReponse("error", "", "Erreur lors de la transmission des données");
            }
        } else {
            return new ObjetReponse("error", "", "Erreur lors de la transmission des données");
        }
    }
}
