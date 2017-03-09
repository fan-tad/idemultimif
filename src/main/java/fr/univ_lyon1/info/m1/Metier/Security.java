package fr.univ_lyon1.info.m1.Metier;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by remi on 25/11/16.
 */
public class Security {
    private String[] salts = {"5bcsST79", "WUnu72d5", "wm96Xm8C", "4Z3dVu4b"};

    public Security() {}

    public String createPwd(String password) {
        List<String> pwds = new ArrayList<>();
        pwds.add(hash(password, "u66K8yRh"));
        for(String salt :salts) {
            pwds.add(hash(pwds.get(pwds.size()-1), salt));
        }
        return pwds.get(4).substring(pwds.get(4).length()/2, pwds.get(4).length()) + pwds.get(3) + pwds.get(4).substring(0, pwds.get(4).length()/2);
    }

    private String hash(String password, String salt) {
        String retour = "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes("UTF-8"));
            byte[] bytes = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++){
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            retour = sb.toString();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return retour;
    }
}
