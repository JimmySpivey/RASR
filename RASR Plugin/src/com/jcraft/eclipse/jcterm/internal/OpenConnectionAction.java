package com.jcraft.eclipse.jcterm.internal;

import java.util.LinkedList;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.jcraft.eclipse.jcterm.IUIConstants;
import com.jcraft.eclipse.jcterm.JCTermPlugin;
import com.jcraft.eclipse.jcterm.JCTermView;

public class OpenConnectionAction extends Action implements IMenuCreator{

    private JCTermView term;
    private LinkedList history=new LinkedList();
    private IAction openShellConnection;
    private IAction openSftpConnection;
    private IAction openExecConnection;
    
    public OpenConnectionAction(JCTermView _term){
      super();
      this.term=_term;

      openShellConnection=new Action(){
        public void run(){
          String[] label=new String[1];
          label[0]="Location";
          boolean[] echo=new boolean[1];
          echo[0]=true;

          OpenConnectionDialog dialog=new OpenConnectionDialog(null, "Enter Location(e.g. user@host[:port])", label, echo);
          //dialog.setUsernameMutable(false);
          dialog.open();
          String[] result=dialog.getResult();

          if(result!=null){
            String location=result[0];
            if(isValidLocation(location)){
              term.openConnection(JCTermView.SHELL, location);
              OpenConnectionAction.this.add(location);
              JCTermPlugin.getDefault().saveLocation("LOCATION/SHELL", location);
            }
          }
        }
      };
      openShellConnection.setText("Open Shell Connection...");

      openSftpConnection=new Action(){
        public void run(){
          String[] label=new String[1];
          label[0]="Location";
          boolean[] echo=new boolean[1];
          echo[0]=true;

          OpenConnectionDialog dialog=new OpenConnectionDialog(null, "Enter Location(e.g. user@host[:port])", label, echo);
          //dialog.setUsernameMutable(false);
          dialog.open();
          String[] result=dialog.getResult();

          if(result!=null){
            String location=result[0];
            if(isValidLocation(location)){
              term.openConnection(JCTermView.SFTP, location);
              OpenConnectionAction.this.add(location);
              JCTermPlugin.getDefault().saveLocation("LOCATION/SFTP", location);
            }
          }
        }
      };
      openSftpConnection.setText("Open Sftp Connection...");
      
      openExecConnection=new Action(){
        public void run(){
          String[] label=new String[2];
          label[0]="Location";
          label[1]="Command";
          boolean[] echo=new boolean[2];
          echo[0]=true;
          echo[1]=true;

          OpenConnectionDialog dialog=new OpenConnectionDialog(null, "Enter Location(e.g. user@host[:port])", label, echo);
          dialog.open();
          String[] result=dialog.getResult();

          if(result!=null){
            String location=result[0];
            String command=result[1];
            if(isValidLocation(location) && command.length()>0){
              term.openConnection(JCTermView.EXEC, location+" "+command);
              JCTermPlugin.getDefault().saveLocation("LOCATION/EXEC", location+" "+command);
            }
          }
        }
      };
      openExecConnection.setText("Open Exec Connection...");

      
      setText("Open Connection");
      setToolTipText("Open Connection");
      setImageDescriptor(JCTermPlugin
          .getImageDescriptor(IUIConstants.IMG_TERMINAL16));
      setMenuCreator(this);
    }

    public void run(){
      openShellConnection.run();
    }

    public void dispose(){
    }

    public Menu getMenu(Control parent){
      Menu fMenu=new Menu(parent);
      addActionToMenu(fMenu, openShellConnection);
      addActionToMenu(fMenu, openSftpConnection);
      addActionToMenu(fMenu, openExecConnection);

      new MenuItem(fMenu, SWT.SEPARATOR);

      String[] values=null;

      values=JCTermPlugin.getDefault().getLocation("LOCATION/SHELL");

      for(int i=0; i<values.length; i++){
        final String location=values[i];
        if(location.length()==0)
          continue;

        Action action=new Action(){
          public void run(){
            term.openConnection(JCTermView.SHELL, location);
          }
        };

        action.setText("Shell: "+location+"@");
        addActionToMenu(fMenu, action);
      }

      values=JCTermPlugin.getDefault().getLocation("LOCATION/SFTP");

      for(int i=0; i<values.length; i++){
        final String location=values[i];
        if(location.length()==0)
          continue;
        Action action=new Action(){
          public void run(){
            term.openConnection(JCTermView.SFTP, location);
          }
        };
        action.setText("Sftp: "+location+"@");
        addActionToMenu(fMenu, action);
      }
      
      values=JCTermPlugin.getDefault().getLocation("LOCATION/EXEC");

      for(int i=0; i<values.length; i++){
        final String location=values[i];
        if(location.length()==0)
          continue;
        Action action=new Action(){
          public void run(){
            term.openConnection(JCTermView.EXEC, location);
          }
        };
        action.setText("Exec: "+location+"@");
        addActionToMenu(fMenu, action);
      }

      return fMenu;
    }

    protected void addActionToMenu(Menu parent, IAction action){
      ActionContributionItem item=new ActionContributionItem(action);
      item.fill(parent, -1);
    }

    public void add(final String location){
      history.addFirst(new Action(){
        public void run(){
          term.openConnection(JCTermView.SHELL, location);
        }
      });
      ((Action)history.get(0)).setText(location);
    }

    public void clear(){
      history.clear();
    }

    public Menu getMenu(Menu parent){
      return null;
    }
    
    private boolean isValidLocation(String location){
    if(location==null)
      return false;
    String host=location;
    String user="";
    if(host.indexOf('@')>0){
      user=host.substring(0, host.indexOf('@'));
      host=host.substring(host.indexOf('@')+1);
    }
    if(host.indexOf(':')>0){
      try{
        Integer.parseInt(host.substring(host.indexOf(':')+1));
        host=host.substring(0, host.indexOf(':'));
      }
      catch(NumberFormatException e){
        return false;
      }
    }
    
    if(user.length()>0&&host.length()>0)
      return true;
    return false;
  }
  }
