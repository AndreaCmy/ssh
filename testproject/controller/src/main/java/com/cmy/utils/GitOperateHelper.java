package main.java.com.cmy.utils;
import java.io.File;
import java.util.Iterator;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class GitOperateHelper {
	private static final String DownLoadPath="E:"+File.separator+"git"+File.separator+"temp";
    private static String login = "liaochun@58ganji.com";
    private static String password = "901019-cmy";
    private CloneCommand clone;
    private Git local;
    private Git remote;
    
    public CloneCommand getClone() {
		return clone;
	}
	public void setClone(CloneCommand clone) {
		this.clone = clone;
	}
	public static File createFile(String orgname,String modulename){
    	String filepath = DownLoadPath+File.separator+orgname+File.separator+modulename;
    	System.out.println("file path is :"+filepath);
    	File file = new File(filepath);
    	if(file.exists()&&file.isFile()){
    		file.delete();
    		file.mkdirs();
    	}
    	if(!file.exists()){
    		System.out.println("File do not exist!");
    		file.mkdirs();
    	}
    	return file;
    }
    public GitOperateHelper(String giturl,File file)throws Exception{

    	clone = Git.cloneRepository().setURI(giturl).setDirectory(file);
    	if (giturl.toString().contains("http") || giturl.toString().contains("https")) {
            UsernamePasswordCredentialsProvider userinfor = new UsernamePasswordCredentialsProvider(login, password);
            clone.setCredentialsProvider(userinfor);
        }
    }
    
    public Repository cloneRepository(CloneCommand clone,File localPath) throws Exception{
    	this.local = clone.call();
    	FileRepositoryBuilder builder = new FileRepositoryBuilder();
    	Repository repository = builder.setGitDir(new File(localPath + "/.git")).readEnvironment().findGitDir().build();
    	return repository;
    }
    
    public String push(String branchname) throws Exception, GitAPIException{
    	local.checkout().setName(branchname).call();
    	local.push().setRemote("master").isForce();
    	return null;
    }
    
    public String cover(String srcName,String dirName) throws Exception, GitAPIException{
    	System.out.println("begin to checkout "+srcName);
    	local.checkout().setName(srcName).call();
    	System.out.println("begin to push to"+dirName);
    	//local.push().setRemote(dirName).isForce()
    	if(local.push().isForce()){
    		System.out.println("ok");
    	}else{
    		System.out.println("fail");
    	}
    	/*Iterable<PushResult> iterable = local.push().setRemote(dirName).isForce();
        for (PushResult pushResult : iterable) {
            System.out.println(pushResult.toString());
        }*/
    	return null;
    }
    
    
   /* public String swichbranch(String orgname,String modulename,String srcName,String dirName) throws Exception, GitAPIException{
    	String localpath = DownLoadPath+File.separator+orgname+File.separator+orgname+File.separator+".git\\modules";
    	Git git = Git.open(new File(newPath + sub));
    	local.push().setRemote(dirName).isForce();
    	Iterable<PushResult> iterable = local.push().setRemote(dirName).isForce();
        for (PushResult pushResult : iterable) {
            System.out.println(pushResult.toString());
        }
    	return null;
    }*/
    
    public  void  pushSubRepository() {
        try {
        	PushCommand pushCommand = this.local.push();
        	CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(
                    "liaochun@58ganji.com", "901019-cmy");
            pushCommand.setCredentialsProvider(credentialsProvider);
        	pushCommand.setRemote( "origin" );
        	pushCommand.setRefSpecs( new RefSpec( "origin/dev1:master" ) );
        	pushCommand.setForce(true);
        	pushCommand.call();
        	Iterator<PushResult> it = pushCommand.call().iterator();
            while(!it.hasNext()){
                System.out.println(it.next().getMessages().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            //return e.getMessage();
        }
    }
    
    
    
    
    public static Repository cloneRepository(String REMOTE_URL) throws Exception {

        // prepare a new folder for the cloned repository
        File localPath = new File("E:\\git\\temp");
        System.out.println(localPath);
        if (!localPath.exists() && !localPath.isDirectory()) {
            localPath.mkdir();
        }
        System.out.println("Cloning from " + REMOTE_URL + " to " + localPath);
        CloneCommand clone = Git.cloneRepository().setURI(REMOTE_URL).setDirectory(localPath);
        if (REMOTE_URL.toString().contains("http") || REMOTE_URL.toString().contains("https")) {
            UsernamePasswordCredentialsProvider userinfor = new UsernamePasswordCredentialsProvider(login, password);
            clone.setCredentialsProvider(userinfor);
        }
        Git repo1 = clone.call();
        for (Ref b : repo1.branchList().setListMode(ListMode.ALL).call())
            System.out.println("(standard): cloned branch " + b.getName());
        repo1.close();
        // now open the created repository
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        // scan environment GIT_DIR
        // GIT_WORK_TREE
               // variables
        Repository repository = builder.setGitDir(new File(localPath + "/.git")).readEnvironment().findGitDir().build();
        return repository;
    }
	public static void main(String[] args) throws Exception{
		String REMOTE_URL = "http://gitlab.58corp.com/liaochun/liaochun-test-project2.git";
		//cloneRepository(REMOTE_URL);
		File localpath=createFile("zhaopin","liaochun");
		GitOperateHelper gitOperateHelper = new GitOperateHelper(REMOTE_URL,localpath);
		gitOperateHelper.cloneRepository(gitOperateHelper.getClone(), localpath);
		gitOperateHelper.pushSubRepository();
		//pushSubRepository(localpath.toString(),"dev1");
		//Thread.sleep(5000);
		//gitOperateHelper.cover("origin/dev1", "refs/remotes/origin/master");
		
	}
}
