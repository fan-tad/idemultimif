package fr.univ_lyon1.info.m1.Itf;

import fr.univ_lyon1.info.m1.Metier.BelongsManager;
import fr.univ_lyon1.info.m1.Metier.ProjectManager;
import fr.univ_lyon1.info.m1.Metier.UserManager;
import fr.univ_lyon1.info.m1.Modele.*;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by fanuel on 19/11/16.
 */
@RestController
@RequestMapping("/api/doc")
public class DocController {

    private EntityManager em = CreateEntity.em;
    private UserManager userManager = new UserManager(em);
    private ProjectManager projectManager = new ProjectManager(em);
    private BelongsManager belongsManager = new BelongsManager(em);

    public void addJavaDoc(String path) {
        FileReader in;
        FileWriter out;
        BufferedReader br;
        Path path1 = Paths.get(path);
        try {
            File file = new File(path);

            out = new FileWriter("output");
            BufferedWriter writer = new BufferedWriter(out);
            in = new FileReader(file);
            br = new BufferedReader(in);
            boolean alreadyDone = false;
            boolean headerAlreadyDone = false;

            List<String> lines = Files.readAllLines(path1);

            if(lines.get(0).equals("/**") && lines.get(1).equals("* @version"))
                headerAlreadyDone=true;

            if(headerAlreadyDone==false) {
                String toWrite = "/**\n* @version\n* @author\n*/\n\n";
                writer.write(toWrite);
            }

            for (String line:lines) {
                String patternFunction = "((public|private)\\s+)?(\\w+(\\s*\\[\\s*\\])*(\\s*(<\\s*\\w+\\s*>)\\s*)*(\\s*\\*)*)\\s(\\s)?[a-z_A-Z]+(\\s)*\\([^\\)]*\\)(\\.[^\\)]*\\))?(\\s+(\\w+)?)*\\{";
                String patternArgs = "(\\(.*\\))";
                Pattern r = Pattern.compile(patternFunction);
                Matcher matcherFunction = r.matcher(line);

                String patternFunctionVoid = "((public|private)\\s+)?(void(\\s*\\[\\s*\\])*(\\s*(<\\s*\\w+\\s*>)\\s*)*(\\s*\\*)*)\\s(\\s)?[a-z_A-Z]+(\\s)*\\([^\\)]*\\)(\\.[^\\)]*\\))?(\\s+(\\w+)?)*\\{";
                Pattern v = Pattern.compile(patternFunctionVoid);
                Matcher matcherFunctionVoid = v.matcher(line);


                if(line.equals("\t*/"))
                    alreadyDone = true;

                if(matcherFunction.find()){
                    if(!alreadyDone) {
                        r = Pattern.compile(patternArgs);
                        matcherFunction = r.matcher(line);
                        if (matcherFunction.find()) {
                            String toWrite = "\t/**\n";
                            writer.write(toWrite);
                            String[] args = matcherFunction.group().split(",");
                            for (String arg : args) {

                                if (arg.charAt(0) == '(') {
                                    arg = arg.substring(1);
                                }
                                if (arg.charAt(0) == ' ') {
                                    arg = arg.substring(1);
                                }
                                if (arg.charAt(arg.length() - 1) == ')') {
                                    arg = arg.substring(0, arg.length() - 1);
                                }
                                System.out.println(arg);
                                if (!arg.equals("")) {
                                    toWrite = "\t * @param " + arg.split(" ")[1] + "\n";
                                    writer.write(toWrite);
                                }
                            }
                            if(!matcherFunctionVoid.find()){
                                toWrite = "\t * @return " + "\n";
                                writer.write(toWrite);
                            }
                            toWrite = "\t*/\n";
                            writer.write(toWrite);
                        }
                    }
                    alreadyDone = false;
                }
                writer.write(line + "\n");
                writer.flush();
            }
            writer.flush();
            writer.close();
            br.close();
            out.close();
            File file2 = new File("output");
            file2.renameTo(file);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ResponseBody
    public ObjetReponse commentToDoc(@RequestParam(value="project", required = true) String idProjet,
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

        try {
            Vcs vcs = new Vcs(owner.getUserName(), project.getProjectId(), true);
            Process p;
            String input = vcs.getUserRepoDir().toString().replace("/", "\\/") + "\\/src";
            String output = vcs.getUserRepoDir().toString().replace("/", "\\/") + "\\/documentation";
            String[] command = {
                    "/bin/sh",
                    "-c",
                    "cp "+vcs.getMasterPath()+"/configDoc " + vcs.getUserRepoDir() +"; " +
                    "cd " + vcs.getUserRepoDir() +"; " +
                    "sed -i -e 's/^\\(INPUT=\\).*$/\\1"+ input + "/g; " +
                            "s/^\\(OUTPUT_DIRECTORY=\\).*$/\\1"+ output +"/g; "+
                            "s/^\\(PROJECT_NAME=\\).*$/\\1\""+ project.getProjectName() + "\"/g' "+
                            vcs.getUserRepoDir()+"/configDoc; " +
                            "doxygen " +vcs.getUserRepoDir()+"/configDoc; "
            };

            p = Runtime.getRuntime().exec(command);
            p.waitFor();

        }
        catch(Exception e) {
            System.out.println(e);
        }

        return new ObjetReponse();
    }
}