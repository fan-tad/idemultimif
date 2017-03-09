package fr.univ_lyon1.info.m1.Modele;

public class ObjetReponse {

    /**
     * Message d'erreur de la réponse objet
     */
    private final String err = "err: ";

    /**
     * Statut de la réponse objet
     */
    private String status;

    /**
     * Contenu de la réponse objet
     */
    private Object contenu;

    /**
     * Message de la réponse objet
     */
    private String message;

    /**
     * Constructeur par défaut de la réponse objet
     */
    public ObjetReponse() {
        this.contenu = "Réponse par défaut";
        this.message = "";
        this.setStatus("success");
    }

    /**
     * Constructeur paramétrique de la réponse objet
     *
     * @param s Statut de la réponse objet
     * @param o Contenu de la réponse objet
     * @param m Message de la réponse objet
     */
    public ObjetReponse(String s, Object o, String m) {
        this.message = "";
        this.contenu = o;
        this.setStatus(s);
        this.message += m;
    }

    /**
     * Retourne le statut de la réponse objet
     * @return Statut de la réponse objet
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * Spécifie le statut de la réponse objet
     * @param s Statut de la réponse objet
     */
    public void setStatus(String s) {
        if(s.equals("success") || s.equals("error") || s.equals("redirect")) {
            this.status = s;
            if(this.status.equals("error")) {
                this.message = this.err;
            }
        }
        else {
            this.status = "error";
            this.contenu = "";
            this.message = this.err + "statut incorrect dans l'objet réponse";
        }
    }

    /**
     * Retourne le contenu de la réponse objet
     * @return Contenu de la réponse objet
     */
    public Object getContenu() {
        return this.contenu;
    }

    /**
     * Spécifie le contenu de la réponse objet
     * @param o Contenu de la réponse objet
     */
    public void setContenu(Object o) {
        this.contenu = o;
    }

    /**
     * Retourne le message de la réponse objet
     * @return Message de la réponse objet
     */
    public String getMessage() {
        return  this.message;
    }

    /**
     * Spécifie le message de la réponse objet
     * @param s Message de la réponse objet
     */
    public void setMessage(String s) {
        this.message = "";
        if(this.status.equals("error")) {
            this.message = this.err;
        }
        this.message += s;
    }
}
