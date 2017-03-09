package fr.univ_lyon1.info.m1.Modele;

import java.io.Serializable;

public class BelongsId implements Serializable {
    /**
     * Identifiant universel de la classe sérialisée "BelongsId"
     */
    private static final long serialVersionUID = 1L;

    /**
     * Identifiant de l'utilisateur
     */
    private int userId;

    /**
     * Identifiant du projet
     */
    private int projectId;

    /**
     * Retourne l'identifiant de l'utilisateur
     *
     * @return Identifiant de l'utilisateur
     */
    public int getUserId() {
        return this.userId;
    }

    /**
     * Spécifie l'identifiant de l'utilisateur
     * @param userId Identifiant de l'utilisateur
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Retourne l'identifiant du projet
     * @return Identifiant du projet
     */
    public int getProjectId() {
        return this.projectId;
    }

    /**
     * Spécifie l'identifiant du projet
     * @param projectId Identifiant du projet
     */
    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        return userId + projectId;
    }

    /**
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof BelongsId){
            BelongsId belongsId = (BelongsId) obj;
            return belongsId.projectId == projectId && belongsId.userId == userId;
        }
        return false;
    }
}
