package com.test.jgit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javassist.bytecode.Descriptor.Iterator;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.SubmoduleAddCommand;
import org.eclipse.jgit.api.errors.AbortedByHookException;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.api.errors.UnmergedPathsException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.ProcessResult.Status;

public class TestJGit {
	private String name = "cmy";
	private String email = "cmy_jj@163.com";
	private String password = "a13774110498";
	private String url = "https://github.com/AndreaCmy/testgit.git";
	public Git cloneGit() throws Exception {


		// credentials
		CredentialsProvider cp = new UsernamePasswordCredentialsProvider(email,
				password);
		// clone
		File file = new File("/tmp/abc");
    	 
		CloneCommand cc = new CloneCommand().setCredentialsProvider(cp)
				.setDirectory(file).setURI(url).setCloneAllBranches(true).setBare(false);
		Git git = cc.call();
//		Git git = Git.open(new File("/tmp/abc/.git"));
		 
	    java.util.Iterator<Ref> it = git.branchList().setListMode(ListMode.ALL).call().iterator();
		while(it.hasNext()){
			Ref ref = it.next();
			System.out.println(ref.getName());
		} 
		return git;
	}
	public void pushSubRepository(Git git) throws InvalidRemoteException, TransportException, GitAPIException{
		PushCommand pushCommand =git.push();
    	CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(email, password);
        pushCommand.setCredentialsProvider(credentialsProvider);
    	pushCommand.setForce(true);
    	pushCommand.setRemote( "origin" );
    	RefSpec refSpec = new RefSpec()
        .setSource("refs/remotes/origin/dev")
        .setDestination("refs/heads/master");
    	pushCommand.setRefSpecs(refSpec).setRemote("origin").call();

	}
	
	public void checkoutCmd(Git git, String newBranchName) throws RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException{
		CheckoutCommand cc = git.checkout();
		cc.setName(newBranchName);
		cc.setCreateBranch(true);
		SetupUpstreamMode mode =  SetupUpstreamMode.SET_UPSTREAM;
		cc.setUpstreamMode(mode );
		cc.call();
	}
	
	public void addCmd(Git git) throws IOException, NoFilepatternException, GitAPIException{
		Repository rep = git.getRepository();
		 File myfile = new File(rep.getDirectory().getParent(), "testfile.txt");
       if(!myfile.createNewFile()) {
//           throw new IOException("Could not create file " + myfile);
           myfile.delete();
       }
       // run the add-call
       git.add()
               .addFilepattern("testfile.txt")
               .call(); 
	}
	
	public void commitCmd(Git git) throws NoHeadException, NoMessageException, UnmergedPathsException, ConcurrentRefUpdateException, WrongRepositoryStateException, AbortedByHookException, GitAPIException{
      CommitCommand commit = git.commit();  
      commit.setCommitter(name, email);  
      commit.setAuthor(name, email);  
      commit.setAll(true);  
      RevCommit revCommit = commit.setMessage("dev to master").call();//git commit -m "use jgit"  
      String commitId = revCommit.getId().name();  
      System.out.println(commitId);
	}
	public static void main(String[] args) throws Exception {
		TestJGit test = new TestJGit();
		Git git = test.cloneGit(); 
		
		test.pushSubRepository(git);
	
	}

}
