package fr.univ_lyon1.info.m1.Itf;

import fr.univ_lyon1.info.m1.Modele.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by remi on 23/11/16.
 */

@RestController
@RequestMapping("/api/logout")
public class LogoutController {

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ObjetReponse receiveGet(HttpSession session, HttpServletRequest request, HttpServletResponse reponse) {
        session.setAttribute("userName", null);

        try {
            reponse.sendRedirect(request.getContextPath());
        }
        catch (Exception e) {

        }
        return new ObjetReponse("redirect", "", "Vous avez été déconnecté");
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ObjetReponse receivePost(HttpServletResponse reponse) {
        reponse.setStatus(405);
        return new ObjetReponse("error", "", "la demande n'est pas prise en compte (POST sur logout)");
    }
}
