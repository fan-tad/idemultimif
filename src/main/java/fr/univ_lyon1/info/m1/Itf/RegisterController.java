package fr.univ_lyon1.info.m1.Itf;

import fr.univ_lyon1.info.m1.Metier.Security;
import fr.univ_lyon1.info.m1.Metier.UserManager;
import fr.univ_lyon1.info.m1.Modele.ObjetReponse;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Team Sharkode on 21/10/16.
 */
@RestController
@RequestMapping("/api/register")
public class RegisterController {

    private EntityManager em = CreateEntity.em;
    private UserManager userManager = new UserManager(em);
    @PostConstruct
    public void init(){
        em = Persistence.createEntityManagerFactory("pu-sharkode").createEntityManager();
        userManager = new UserManager(em);
    }

    /**
     * ATTENTION : Cette méthode n'est pas censée être appelée, mais existe pour empêcher le serveur de planter en cas
     * d'appel inattendu (ou forcé par une attaque du système) à cette méthode.
     *
     * @param reponse   Réponse du serveur.
     * @return  ObjetReponse contenant un retour d'erreur.
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ObjetReponse receiveGet(HttpServletResponse reponse) {
        reponse.setStatus(405);
        return new ObjetReponse("error", "", "la demande n'a pas été prise en compte (GET sur register)");
    }

    /**
     * Gère l'enregistrement de l'utilisateur en vérifiant son existence et la validité de son mot de passe.
     *
     * @param pseudo    Pseudo de l'utilisateur renvoyé par le formulaire.
     * @param password  Mot de passe de l'utilisateur renvoyé par le formulaire.
     * @return  ObjetReponse indiquant la réussite ou l'échec de l'enregistrement.
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ObjetReponse receivePost(@RequestParam(value="pseudo", required = true) String pseudo,
                                    @RequestParam(value="password", required = true) String password) {
        if(pseudo.length() <= 45 && password.length() <= 45) {
            Security security = new Security();
            if(userManager.newUser(pseudo, security.createPwd(password)) != null) {
                return new ObjetReponse();
            } else {
                return new ObjetReponse("error", "", "erreur à la création compte.");
            }
        } else {
            return new ObjetReponse("error", "", "Pseudo ou mot de passe trop long.");
        }
    }
}
