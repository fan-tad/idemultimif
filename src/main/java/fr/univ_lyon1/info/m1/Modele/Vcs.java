package fr.univ_lyon1.info.m1.Modele;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.util.ArrayUtil;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.RemoteAddCommand;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.ReflogEntry;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.URIish;

import java.io.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by guillaumemartinez on 15/11/2016.
 */

// Un Vcs par personne et par projet je pense
public class Vcs {

    private String username;

    private String masterPath;

    private File serverRepoDir;
    private File userRepoDir;

    private Repository serverRepo;
    private Git gitRepo;
    private Repository repo;
    private Git git;

    public Vcs(String username, int projectId, boolean dryRun) {
        String projectName = Integer.toString(projectId);
        this.username = username;
        masterPath = System.getProperty("java.io.tmpdir");

        // Create master dir if not exist
        File masterVCSDir = new File(masterPath);
        if (!masterVCSDir.exists()) {
            try {
                masterVCSDir.mkdir();
            } catch (Exception se) {
                System.err.println(se);
            }
        }

        // Init storage dir
        File repoDir = new File(masterPath, projectName);
        if (!repoDir.exists()) {
            try {
                repoDir.mkdir();
            } catch (Exception se) {

                System.err.println(se);
            }
        }

        // Create master repo
        serverRepoDir = new File(masterPath, projectName + "/master");
        if (!serverRepoDir.exists()) {
            try {
                serverRepoDir.mkdir();
                if (!dryRun) {
                    File pointGit = new File(serverRepoDir.getPath(), ".git");
                    serverRepo = new FileRepositoryBuilder().create(pointGit);
                    serverRepo.create();
                    gitRepo = new Git(serverRepo);
                }
            } catch (Exception se) {

                System.err.println(se);
            }
        } else {
            if (!dryRun) {
                try {
                    File pointGit = new File(serverRepoDir.getPath(), ".git");
                    serverRepo = new FileRepositoryBuilder().setGitDir(pointGit).build();
                } catch (IOException e) {

                    System.err.println(e);
                }
                gitRepo = new Git(serverRepo);
            }
        }

        // Create user repo
        userRepoDir = new File(masterPath, projectName + "/" + username + "@master");
        boolean needToCreateMasterBranch = false;
        if (!serverRepoDir.exists()) {
            needToCreateMasterBranch = true;
            try {
                FileUtils.copyDirectory(serverRepoDir, userRepoDir);
            } catch (Exception se) {
                System.err.println(se);
            }
        }

        // Init git
        if (!dryRun) {
            try {
                File pointGit = new File(userRepoDir.getPath(), ".git");
                if (pointGit.exists()) {
                    repo = new FileRepositoryBuilder().setGitDir(pointGit).build();
                } else {
                    repo = new FileRepositoryBuilder().create(pointGit);
                    repo.create();
                }
            } catch (IOException e) {

                System.err.println(e);
            }
            git = new Git(repo);
        }

        if (!dryRun) {
            try {
                RemoteAddCommand remote = git.remoteAdd();
                remote.setName("origin");
                remote.setUri(new URIish("file://" + serverRepoDir.getAbsolutePath()));
                remote.call();
            } catch (Exception e) {
                System.err.println(e);
            }

            if (needToCreateMasterBranch) {
                this.createBranch("master");
            }
        }
    }

    /**
     * @description Detruit le repo
     **/
    public void destroy() {
        try {
            FileUtils.deleteDirectory(userRepoDir);
        } catch (Exception e) {

            System.err.println(e);
        }
    }

    /**
     * @param folderPath - Un chemin relatif au repo
     * @description Cree un nouveau dossier
     */
    public void mkdir(String folderPath) {
        String[] folders = folderPath.split("/");
        String path = "";
        for (String f : folders) {
            if (f != null) {
                path += f + "/";
                File file = new File(userRepoDir.getPath(), path);
                try {
                    file.mkdir();
                } catch (Exception e) {

                    System.err.println(e);
                }
            }
        }
    }

    /**
     * @param filePath - Un chemin relatif au repo
     * @param fileData - Data du fichier
     * @description Sauvgarde un fichier dans le repo
     */
    public void saveFile(String filePath, String fileData) {
        String[] path = filePath.split("/");

        path[path.length - 1] = null;
        path = ArrayUtil.removeNulls(path);
        mkdir(String.join("/", path));
        try {
            PrintWriter writer = new PrintWriter(userRepoDir.getPath() + "/" + filePath, "UTF-8");
            writer.print(fileData);
            writer.close();
        } catch (Exception e) {

            System.err.println(e);
        }
    }

    public void appendFile(String filePath, String fileData) {
        String[] path = filePath.split("/");
        path[path.length - 1] = null;
        path = ArrayUtil.removeNulls(path);
        mkdir(String.join("/", path));
        try {
            PrintWriter writer = new PrintWriter(new FileOutputStream(
                    new File(userRepoDir.getPath(), "/" + filePath),
                    true));
            writer.print(fileData + "\n");
            writer.close();
        } catch (Exception e) {

            System.err.println(e);
        }
    }

    /**
     * @param filePath
     * @return
     * @description Read the content of a file
     */
    public String readFile(String filePath) {
        String data = "";

        String line;
        BufferedReader buffer = null;

        try {
            buffer = new BufferedReader(new FileReader(userRepoDir.getPath() + "/" + filePath));

            while ((line = buffer.readLine()) != null) {
                data += line + "\n";
            }
        } catch (Exception e) {
            System.err.println(e);
        }

        return data;
    }

    public File readFileF(String filePath) {
        File f = new File(userRepoDir.getPath(), filePath);
        return f;
    }

    /**
     * @param filePath
     * @return
     * @description Get the size of a file
     */
    public long getFileSize(String filePath) {
        File f = new File(userRepoDir.getPath(), filePath);
        return f.length();
    }

    /**
     * @param branch
     * @description Creation d'une branch
     */
    public void createBranch(String branch) {
        try {
            String branchFrom = git.getRepository().getBranch();
            git.branchCreate().setName(branch).call();
            this.checkout(branch);
            this.add(".");
            this.commit("[Fork] " + branch + " from " + branchFrom);
        } catch (Exception e) {

            System.err.println(e);
        }
    }

    public String getCurrentBranch() {
        String branch = "";
        try {
            branch = repo.getBranch();
        } catch (Exception e) {
            System.err.println(e);
        }

        return branch;
    }

    public ArrayList<String> getBranchesList() {
        ArrayList<String> branchesList = new ArrayList<>();
        try {
            List<Ref> branches = git.branchList().call();

            for (Ref b : branches) {
                branchesList.add(b.getName());
            }
        } catch (Exception e) {
            System.err.println(e);
        }

        return branchesList;
    }

    /**
     * @param branch
     * @description Supression d'une branch
     */
    public void deleteBranch(String branch) {
        try {
            git.branchDelete().setBranchNames(branch).call();
        } catch (Exception e) {

            System.err.println(e);
        }
    }

    /**
     * @param fromBranch
     * @param intoBranch
     * @description Merge branches
     */
    public void mergeBranch(String fromBranch, String intoBranch) {
        try {
            git.checkout().setName(intoBranch).call();
            List<Ref> branches = git.branchList().call();
            Ref branch = null;
            for (Ref r : branches) {
                if (r.getName().contains(fromBranch)) {
                    branch = r;
                    break;
                }
            }
            if (branch != null) {
                git.merge().include(branch).call();
            }
            this.add(".");
            this.commit("[Merge] " + fromBranch + " into " + intoBranch);
        } catch (Exception e) {

            System.err.println(e);
        }
    }

    /**
     * @param branch - Branche existante
     * @description Change de branche
     */
    public void checkout(String branch) {
        try {
            git.checkout().setName(branch).call();
        } catch (Exception e) {

            System.err.println(e);
        }
    }

    /**
     * @param filePath - Un chemin relatif au repo
     * @description Ajoute un fichier a git
     */
    public void add(String filePath) {
        try {
            git.add().addFilepattern(filePath).call();
        } catch (Exception e) {

            System.err.println(e);
        }
    }

    /**
     * @param message - Message du commit
     * @description Cree un commit
     */
    public void commit(String message) {
        try {
            git.commit().setMessage(message).setCommitter(this.username, this.username + "@sharkode").call();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    /**
     * Generate tree from folder
     *
     * @param folderPath
     * @return
     */
    public HashMap getTreeFrom(String folderPath) {
        folderPath = folderPath.replace(userRepoDir.getPath(), "");
        File f = new File(userRepoDir.getPath(), folderPath);
        HashMap tree = new HashMap<>();

        if (f.isDirectory() && !f.getName().equals(".git")) {
            File[] childs = f.listFiles();
            for (File c : childs) {
                if (c.isDirectory()) {
                    HashMap folder = new HashMap<>();
                    folder.put("name", c.getName());
                    folder.put("type", "dir");
                    folder.put("commit", null);
                    folder.put("date", c.lastModified());
                    folder.put("child", getTreeFrom(c.getPath()));
                    tree.put(c.getName(), folder);
                } else {
                    HashMap file = new HashMap<>();
                    file.put("name", c.getName());
                    file.put("type", "file");
                    file.put("commit", null);
                    file.put("date", c.lastModified());
                    tree.put(c.getName(), file);
                }
            }
        } else {
            tree.put(f.getName(), f.getName());
        }

        return tree;
    }

    /**
     * Create an empty file
     *
     * @param filePath
     */
    public void createFile(String filePath) {
        saveFile(filePath, "");
    }

    /**
     * Create an empty folder
     *
     * @param filePath
     */
    public void createFolder(String filePath) {
        File f = new File(userRepoDir.getPath(), filePath);
        try {
            f.mkdir();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    /**
     * Is a file exist
     *
     * @param filePath
     * @return
     */
    public boolean existFile(String filePath) {
        File f = new File(userRepoDir.getPath(), filePath);
        return f.exists();
    }

    /**
     * Remove file
     *
     * @param filePath
     */
    public void removeFile(String filePath) {
        File f = new File(userRepoDir.getPath(), filePath);
        if (f.isFile()) {
            try {
                f.delete();
            } catch (Exception e) {
                System.err.println(e);
            }
        } else {
            this.removeFolder(filePath);
        }
    }

    /**
     * Remove folder
     *
     * @param folderPath
     */
    public void removeFolder(String folderPath) {
        File f = new File(userRepoDir.getPath(), folderPath);
        try {
            FileUtils.deleteDirectory(f);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    /**
     * Move folder / file into another folder
     *
     * @param filePath
     * @param intoFolder
     */
    public void move(String filePath, String intoFolder) {
        String[] path = filePath.split("/");
        intoFolder += "/" + path[path.length - 1];

        File file = new File(userRepoDir.getPath(), filePath);
        File folder = new File(userRepoDir.getPath(), intoFolder);
        try {
            Files.move(file.toPath(), folder.toPath());
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public void compile(String lang) {
        HashMap<String, String> res = new HashMap<>();

        String[] command = {
                "/bin/sh",
                "-c",
                "cd " + userRepoDir.getPath() + " ; "
        };

        switch (lang) {
            case "maven":
            case "mvn":
            case "java":
                command[2] += "mvn clean install";
                break;
            case "c":
            case "cpp":
            case "c++":
                command[2] += "mkdir build 2> /dev/null; cd build 2> /dev/null; cmake ../src/; make";
                break;
            default:
                return;
        }

        command[2] += ";";
        String stderrLink = "";
        String stdoutLink = "";
        try {
            this.createFile("out/stderr.txt");
            this.createFile("out/stdout.txt");
            String s;

            Process p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            // read the output from the command
            while ((s = stdInput.readLine()) != null) {
                this.appendFile("out/stdout.txt", s);
            }

            // read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
                this.appendFile("out/stderr.txt", s);
            }

        } catch (Exception e) {
            System.err.println(e);
        }
    }


    public ArrayList<HashMap<String, String>> getCommits(String getBranch) {
        ArrayList<HashMap<String, String>> commits = new ArrayList<HashMap<String, String>>();

        HashMap<String, String> tetes = new HashMap<>();

        try {

            Iterable<RevCommit> commitsRevertList = git.log().all().call();

            Pattern patternMerge = Pattern.compile("\\[Merge\\] ([^ ]*) into ([^ ]*)");
            Pattern patternFakeMerge = Pattern.compile("Merge branch '([^\']*)'");
            Pattern patternFork = Pattern.compile("\\[Fork\\] ([^ ]*) from ([^ ]*)");

            ArrayList<RevCommit> commitsList = new ArrayList<>();
            for (RevCommit commit : commitsRevertList) {
                commitsList.add(0, commit);
            }

            tetes.put(commitsList.get(0).getName(), "master");
            for (RevCommit commit : commitsList) {

                Matcher matcherMerge = patternMerge.matcher(commit.getFullMessage());
                Matcher matcherFakeMerge = patternFakeMerge.matcher(commit.getFullMessage());
                Matcher matcherFork = patternFork.matcher(commit.getFullMessage());


                if (matcherFakeMerge.matches()) continue;

                HashMap<String, String> c = new HashMap<String, String>();
                if (matcherMerge.matches()) {
                    tetes.put(commit.getName(), matcherFork.group(2));
                    c.put("action", "merge");
                    c.put("branch", matcherMerge.group(1));
                    c.put("mergeOn", matcherMerge.group(2));

                    c.put("action", "commit");
                    c.put("branch", matcherFork.group(1));
                    c.put("hash", commit.getName());
                    c.put("message", commit.getFullMessage());
                    c.put("author", commit.getCommitterIdent().getName());
                } else if (matcherFork.matches()) {
                    tetes.put(commit.getName(), matcherFork.group(1));
                    c.put("action", "fork");
                    c.put("branch", matcherFork.group(2));
                    c.put("forkChild", matcherFork.group(1));

                    c.put("action", "commit");
                    c.put("branch", matcherFork.group(1));
                    c.put("hash", commit.getName());
                    c.put("message", commit.getFullMessage());
                    c.put("author", commit.getCommitterIdent().getName());
                } else {
                    String branch;
                    try {
                        RevCommit parent = commit.getParent(0);
                        branch = tetes.get(parent.getName());
                        tetes.remove(parent.getName());
                    } catch (Exception e) {
                        branch = "master";
                    }
                    tetes.put(commit.getName(), branch);

                    c.put("action", "commit");
                    c.put("branch", branch);
                    c.put("hash", commit.getName());
                    c.put("message", commit.getFullMessage());
                    c.put("author", commit.getCommitterIdent().getName());
                }

                commits.add(c);
            }
        } catch (Exception e) {
            System.err.println(e);
        }

        return commits;
    }

    public void pull() {
        try {
            git.pull().call();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public void push() {
        try {
            git.push().call();
            gitRepo.reset().setMode(ResetCommand.ResetType.HARD).call();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    // Fonction de parsing et de détection de todo dans un fichier
    public List<String> detectTodo(String fileData) {
        List<String> todosContent = new ArrayList<>();
        String[] lines = fileData.split("\n");
        for (String line : lines) {
            String regexTodo = "(\\*|//)\\s*[Tt][Oo][Dd][Oo]";
            Pattern t = Pattern.compile(regexTodo);
            Matcher matcherTodo = t.matcher(line);
            if (matcherTodo.find()) {
                String afterTodo = "([Tt][Oo][Dd][Oo])(.*$)";
                Pattern aT = Pattern.compile(afterTodo);
                Matcher matcherAfterTodo = aT.matcher(line);
                if (matcherAfterTodo.find()) {
                    todosContent.add(matcherAfterTodo.group(2).toString());
                }
            }
        }
        return todosContent;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMasterPath() {
        return masterPath;
    }

    public void setMasterPath(String masterPath) {
        this.masterPath = masterPath;
    }

    public File getServerRepoDir() {
        return serverRepoDir;
    }

    public void setServerRepoDir(File serverRepoDir) {
        this.serverRepoDir = serverRepoDir;
    }

    public File getUserRepoDir() {
        return userRepoDir;
    }

    public void setUserRepoDir(File userRepoDir) {
        this.userRepoDir = userRepoDir;
    }

    public Repository getServerRepo() {
        return serverRepo;
    }

    public void setServerRepo(Repository serverRepo) {
        this.serverRepo = serverRepo;
    }

    public Git getGitRepo() {
        return gitRepo;
    }

    public void setGitRepo(Git gitRepo) {
        this.gitRepo = gitRepo;
    }

    public Repository getRepo() {
        return repo;
    }

    public void setRepo(Repository repo) {
        this.repo = repo;
    }

    public Git getGit() {
        return git;
    }

    public void setGit(Git git) {
        this.git = git;
    }
}
