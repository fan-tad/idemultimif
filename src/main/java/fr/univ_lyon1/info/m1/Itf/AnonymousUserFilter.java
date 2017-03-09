package fr.univ_lyon1.info.m1.Itf;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.websocket.Session;
import javax.websocket.SessionException;
import java.io.IOException;

/**
 * Created by Team Sharkode on 17/11/16.
 */
public class AnonymousUserFilter implements Filter {

    /**
     * Initialisation du filtre.
     *
     * @param filterConfig  Configuration du filtre pour l'initialisation.
     */
    @Override
    public void init(FilterConfig filterConfig) {
    }

    /**
     * Filtre les contrôleurs en vérifiant que l'utilisateur est en session. Si l'utilisateur n'est pas authentifié, il
     * est redirigé sur la page d'accueil (la page par défaut).
     *
     * @param servletRequest    Requête envoyée au serveur.
     * @param servletResponse   Réponse du filtre au serveur.
     * @param filterChain       Chaine des filtres (ici 1 filtre) traitant les requêtes.
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpSession session = req.getSession();
        String uri = req.getRequestURI();
        String url = req.getRequestURL().toString();
        String contextPath = req.getContextPath();

            if(!(uri.equals(contextPath + "/") || uri.contains("/login") || uri.contains("/register") || url.contains("/src"))) {

                String userName = (String) session.getAttribute("userName");
                if(userName == null) {
                    try {
                        servletRequest.getRequestDispatcher("/redirect").forward(servletRequest, servletResponse);
                    }
                    catch (Exception e) {
                        System.err.println(e);
                    }
                }
            }

            try {
                filterChain.doFilter(servletRequest, servletResponse);
            }
            catch(Exception e) {
                System.err.println(e);
            }
    }

    /**
     * Destruction du filtre.
     */
    @Override
    public void destroy() {
    }
}
