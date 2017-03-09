package fr.univ_lyon1.info.m1.Itf;

import fr.univ_lyon1.info.m1.Metier.BelongsManager;
import fr.univ_lyon1.info.m1.Metier.ProjectManager;
import fr.univ_lyon1.info.m1.Metier.TicketManager;
import fr.univ_lyon1.info.m1.Metier.UserManager;
import fr.univ_lyon1.info.m1.Modele.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;

/**
 * Created by Team Sharkode on 16/11/16.
 */
@Controller
@RequestMapping("/api/ticket")
public class TicketController {
    private EntityManager em = CreateEntity.em;
    private UserManager userManager = new UserManager(em);
    private ProjectManager projectManager = new ProjectManager(em);
    private BelongsManager belongsManager = new BelongsManager(em);
    private TicketManager ticketManager = new TicketManager(em);

    /**
     * Permet à l'utilisateur de selectionner un ticket dans la liste.
     *
     * @param idTicketString    Id du ticket selectionné.
     * @param session           Session HTML de l'utilisateur.
     * @param reponse           Type d'erreur à retourner en cas de problème.
     * @return  ObjetReponse contenant des informations relatives au ticket, ou un message d'erreur.
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ObjetReponse receiveGet(@RequestParam(value = "id", required = true) String idTicketString,
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
            return new ObjetReponse("error", "", "Erreur lors de la transmission des données");
        }

        Belongs belongs = belongsManager.getBelongs(project.getProjectId(), user.getUserId());
        if(belongs == null) {
            reponse.setStatus(403);
            return new ObjetReponse("error", "", "Vous ne faites pas partie du projet");
        }

        int ticketId = Integer.valueOf(idTicketString);
        if (ticketId > 0) {
            Ticket ticket = ticketManager.getTicket(ticketId);

            HashMap map = new HashMap();

            map.put("name", ticket.getTicketName());
            map.put("badge", ticket.getTicketBadge());
            if (ticket.getUserOwner() != null) {
                map.put("user", ticket.getUserOwner().getUserName());
            } else {
                map.put("user", "");
            }
            map.put("content", ticket.getTicketContent());

            return new ObjetReponse("success", map, "");
        } else {
            return new ObjetReponse("error", "", "Erreur lors de la transmission des données");
        }
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
        return new ObjetReponse("error", "", "la demande n'est pas prise en compte (POST sur ticket)");
    }
}
