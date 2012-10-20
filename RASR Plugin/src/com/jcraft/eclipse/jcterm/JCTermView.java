/*******************************************************************************
 * Copyright (c) 2007 JCraft, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     JCraft, Inc. - initial API and implementation
 *******************************************************************************/

package com.jcraft.eclipse.jcterm;

import java.awt.Frame;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JRootPane;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.osehra.eclipse.atfrecorder.ATFRecorderAWT;

import com.jcraft.eclipse.jsch.core.IJSchLocation;
import com.jcraft.eclipse.jsch.core.JSchCoreException;
import com.jcraft.eclipse.jsch.core.JSchLocation;
import com.jcraft.eclipse.jsch.core.JSchLocationAdapter;
import com.jcraft.eclipse.jsch.core.JSchSession;
import com.jcraft.jcterm.Connection;
import com.jcraft.jcterm.JCTermPanelG2D;
import com.jcraft.jcterm.JCTermSWT;
import com.jcraft.jcterm.JCTermSwing;
import com.jcraft.jcterm.Sftp;
import com.jcraft.jcterm.Term;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelShell;

public class JCTermView extends ViewPart{

  private static final String ID="com.jcraft.eclipse.jcterm.view";

  public static final int SHELL=0;
  public static final int SFTP=1;
  public static final int EXEC=2;

  static final int JCTERMSWT=0;
  static final int JCTERMSWING=1;
  static final int JCTERMAWT=2;
  static final int ATFRECORDERAWT=3;

  private int ui=ATFRECORDERAWT; //original value: JCTERMAWT

  private String xhost="127.0.0.1";
  private int xport=0;
  private boolean xforwarding=false;

  private JSchSession jschsession=null;

  private int compression=0;

  private Term term=null;

  private Connection connection=null;
  private String location=null;

  public static final String INFO_PASSWORD="com.jcraft.eclipse.jcterm.password";//$NON-NLS-1$
  public static final String AUTH_SCHEME=""; //$NON-NLS-1$ 
  public static final URL FAKE_URL;

  static{
    URL temp=null;
    try{
      temp=new URL("http://com.jcraft.eclipse.jcterm");//$NON-NLS-1$ 
    }
    catch(MalformedURLException e){
      // Should never fail
    }
    FAKE_URL=temp;
  }

  public JCTermView(){
  }

  Composite container=null;
  Frame frame=null;

  public void createPartControl(Composite parent){

    switch(ui){
      case ATFRECORDERAWT:
    	  
    	  System.out.println("Using new ATF Recorder Part Control");
    	  
          container=new Composite(parent, SWT.EMBEDDED|SWT.NO_BACKGROUND);
          frame=org.eclipse.swt.awt.SWT_AWT.new_Frame(container);

          container.addControlListener(new ControlListener(){
            public void controlMoved(ControlEvent e){
            }

            public void controlResized(ControlEvent e){
              Rectangle bounds=container.getBounds();
              if(bounds.width==0||bounds.height==0)
                return;
              if(term!=null&&term instanceof ATFRecorderAWT)
                ((ATFRecorderAWT)term).setSize(bounds.width, bounds.height);
            }

          });
          container.addKeyListener(new KeyListener(){
            public void keyPressed(KeyEvent e){
              //System.out.println("keyPressed: "+e);
              int code=-1;
              switch(e.keyCode){
                case 9: // TAB
                  code=9;
                  break;
                case SWT.ARROW_DOWN:
                  code=java.awt.event.KeyEvent.VK_DOWN;
                  break;
                case SWT.ARROW_UP:
                  code=java.awt.event.KeyEvent.VK_UP;
                case SWT.ARROW_LEFT:
                  code=java.awt.event.KeyEvent.VK_LEFT;
                  break;
                case SWT.ARROW_RIGHT:
                  code=java.awt.event.KeyEvent.VK_RIGHT;
                  break;
              }
              if(code!=-1){
                if(term instanceof ATFRecorderAWT){
                  ((ATFRecorderAWT)term).keyTypedCode(code);
                  ((ATFRecorderAWT)term).requestFocusInWindow();
                }
              }
            }

            public void keyReleased(KeyEvent e){
            }
          });

          container.addFocusListener(new FocusListener(){
            public void focusGained(FocusEvent e){
              // System.out.println("focusGained: "+e);
              frame.requestFocus();
              if(term instanceof ATFRecorderAWT){
                ((ATFRecorderAWT)term).requestFocusInWindow();
              }
            }

            public void focusLost(FocusEvent e){
            }
          });

          frame.setFocusable(true);
          frame.setFocusableWindowState(true);

          term=new ATFRecorderAWT();
          frame.add((ATFRecorderAWT)term);
          frame.pack();

          frame.addKeyListener((java.awt.event.KeyListener)term);

          break;
    	  
    	  
      //ORIGINAL JCTERM CODE BELOW
//      case JCTERMAWT:
//    	  
//        container=new Composite(parent, SWT.EMBEDDED|SWT.NO_BACKGROUND);
//        frame=org.eclipse.swt.awt.SWT_AWT.new_Frame(container);
//
//        container.addControlListener(new ControlListener(){
//          public void controlMoved(ControlEvent e){
//          }
//
//          public void controlResized(ControlEvent e){
//            Rectangle bounds=container.getBounds();
//            if(bounds.width==0||bounds.height==0)
//              return;
//            if(term!=null&&term instanceof JCTermPanelG2D)
//              ((JCTermPanelG2D)term).setSize(bounds.width, bounds.height);
//          }
//
//        });
//        container.addKeyListener(new KeyListener(){
//          public void keyPressed(KeyEvent e){
//            //System.out.println("keyPressed: "+e);
//            int code=-1;
//            switch(e.keyCode){
//              case 9: // TAB
//                code=9;
//                break;
//              case SWT.ARROW_DOWN:
//                code=java.awt.event.KeyEvent.VK_DOWN;
//                break;
//              case SWT.ARROW_UP:
//                code=java.awt.event.KeyEvent.VK_UP;
//              case SWT.ARROW_LEFT:
//                code=java.awt.event.KeyEvent.VK_LEFT;
//                break;
//              case SWT.ARROW_RIGHT:
//                code=java.awt.event.KeyEvent.VK_RIGHT;
//                break;
//            }
//            if(code!=-1){
//              if(term instanceof JCTermPanelG2D){
//                ((JCTermPanelG2D)term).keyTypedCode(code);
//                ((JCTermPanelG2D)term).requestFocusInWindow();
//              }
//            }
//          }
//
//          public void keyReleased(KeyEvent e){
//          }
//        });
//
//        container.addFocusListener(new FocusListener(){
//          public void focusGained(FocusEvent e){
//            // System.out.println("focusGained: "+e);
//            frame.requestFocus();
//            if(term instanceof JCTermPanelG2D){
//              ((JCTermPanelG2D)term).requestFocusInWindow();
//            }
//          }
//
//          public void focusLost(FocusEvent e){
//          }
//        });
//
//        frame.setFocusable(true);
//        frame.setFocusableWindowState(true);
//
//        term=new JCTermPanelG2D();
//        frame.add((JCTermPanelG2D)term);
//        frame.pack();
//
//        frame.addKeyListener((java.awt.event.KeyListener)term);
//
//        break;
//
//      case JCTERMSWING:
//
//        JRootPane root=new JRootPane();
//        frame.add(root);
//        term=new JCTermSwing();
//        root.getContentPane().add((JPanel)term);
//
//        break;
//
//      case JCTERMSWT:
//      default:
//        term=new JCTermSWT(parent);
    }

    makeAction();
    setPartName("RAS Recorder"); //$NON-NLS-1$
  }

  private void makeAction(){
    IActionBars bars=getViewSite().getActionBars();
    com.jcraft.eclipse.jcterm.internal.OpenConnectionAction openConnection=new com.jcraft.eclipse.jcterm.internal.OpenConnectionAction(
        this);
    bars.getToolBarManager().add(openConnection);
  }

  public void openConnection(int mode, String location){

    if(connection!=null){
      JCTermView view=getUnusedView();
      if(view!=this){
        view.openConnection(mode, location);
        return;
      }
      // TODO
      return;
    }

    this.location=location;

    if(mode==EXEC){
      this.location=location.substring(0, location.indexOf(' '));
    }

    try{
      IProgressMonitor monitor=new NullProgressMonitor();
      jschsession=JSchSession.getSession(getJSchLocation(this.location),
          monitor);
      if(jschsession==null){
        // TODO
        this.location=null;
        return;
      }

      jschsession.getSession().setServerAliveInterval(60000);
      setCompression(compression);
    }
    catch(Exception e){
      // System.out.println(e);
      // break;
      return;
    }

    try{
      Channel channel=null;
      OutputStream out=null;
      InputStream in=null;

      if(mode==SHELL){
        channel=jschsession.getSession().openChannel("shell"); //$NON-NLS-1$

        if(xforwarding){
          jschsession.getSession().setX11Host(xhost);
          jschsession.getSession().setX11Port(xport+6000);
          channel.setXForwarding(true);
        }

        ((ChannelShell)channel).setAgentForwarding(true);
        
        out=channel.getOutputStream();
        in=channel.getInputStream();
        channel.connect();
        ((ChannelShell)channel).setPtySize(term.getColumnCount(), term
            .getRowCount(), term.getTermWidth(), term.getTermHeight());
      }
      else if(mode==SFTP){

        out=new PipedOutputStream();
        in=new PipedInputStream();

        channel=jschsession.getSession().openChannel("sftp"); //$NON-NLS-1$

        channel.connect();

        (new Sftp((ChannelSftp)channel, (InputStream)(new PipedInputStream(
            (PipedOutputStream)out)), new PipedOutputStream(
            (PipedInputStream)in))).kick();
      }
      else if(mode==EXEC){
        String command=location.substring(location.indexOf(' ')+1);
        /*
               channel=jschsession.getSession().openChannel("exec");

               if(xforwarding){
                 jschsession.getSession().setX11Host(xhost);
                 jschsession.getSession().setX11Port(xport+6000);
                 channel.setXForwarding(true);
               }
               
               ((ChannelExec)channel).setCommand(command);
               ((ChannelExec)channel).setPty(true);

               out=channel.getOutputStream();
               in=channel.getInputStream();
               channel.connect();
               
               ((ChannelExec)channel).setPtySize(term.getColumnCount(), term
                   .getRowCount(), term.getTermWidth(), term.getTermHeight());
        */

        channel=jschsession.getSession().openChannel("shell"); //$NON-NLS-1$

        if(xforwarding){
          jschsession.getSession().setX11Host(xhost);
          jschsession.getSession().setX11Port(xport+6000);
          channel.setXForwarding(true);
        }

        out=channel.getOutputStream();
        in=channel.getInputStream();
        channel.connect();

        ((ChannelShell)channel).setPtySize(term.getColumnCount(), term
            .getRowCount(), term.getTermWidth(), term.getTermHeight());

        out.write(("exec "+command).getBytes()); //$NON-NLS-1$
        byte[] cr=new byte[1];
        cr[0]=0x0a;
        out.write(cr);
        out.flush();
      }

      final OutputStream fout=out;
      final InputStream fin=in;
      final Channel fchannel=channel;

      Connection connection=new Connection(){
        public InputStream getInputStream(){
          return fin;
        }

        public OutputStream getOutputStream(){
          return fout;
        }

        public void requestResize(Term term){
          if(fchannel instanceof ChannelShell){
            int c=term.getColumnCount();
            int r=term.getRowCount();
            ((ChannelShell)fchannel).setPtySize(c, r, c*term.getCharWidth(), r
                *term.getCharHeight());
          }
        }

        public void close(){
          fchannel.disconnect();
        }
      };

      start(connection);
    }
    catch(Exception e){
      // System.out.println(e);
      // break;
    }
  }

  private void start(Connection connection){
    this.connection=connection;
    Thread termThread=new Thread(new Runnable(){
      public void run(){
        setPartName(location);
        term.start(JCTermView.this.connection);
        if(JCTermView.this.connection!=null)
          JCTermView.this.connection.close();
        JCTermView.this.connection=null;
        setPartName("JCTerm");
      }
    });
    setFocus();
    termThread.start();
  }

  public void setPartName(final String name){
    final Display display=Display.getDefault();
    if(display==null){
      return;
    }
    display.asyncExec(new Runnable(){
      public void run(){
        JCTermView.super.setPartName(name);
      }
    });
  }

  private void setCompression(int compression){
    this.compression=compression;
    if(jschsession==null||!jschsession.getSession().isConnected()){
      return;
    }
    java.util.Properties config=new java.util.Properties();
    if(compression==0){
      config.put("compression.s2c", "none"); //$NON-NLS-1$ //$NON-NLS-2$
      config.put("compression.c2s", "none"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    else{
      config.put("compression.s2c", "zlib,none"); //$NON-NLS-1$ //$NON-NLS-2$
      config.put("compression.c2s", "zlib,none"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    try{
      jschsession.getSession().setConfig(config);
      jschsession.getSession().rekey();
    }
    catch(Exception e){
    }
  }

  public void setFocus(){
    container.setFocus();

    if(term instanceof JCTermSWT)
      ((JCTermSWT)term).setFocus();
    else if(term instanceof JCTermPanelG2D){
      ((JCTermPanelG2D)term).requestFocusInWindow();
    }
  }

  private JCTermView getUnusedView(){
    JCTermView view=this;
    if(view.connection!=null){
      try{
        IWorkbench workbench=PlatformUI.getWorkbench();
        IWorkbenchWindow window=workbench.getActiveWorkbenchWindow();
        IWorkbenchPage page=window.getActivePage();
        IViewPart v=page.showView(JCTermView.ID);
        if(!(v instanceof JCTermView)||((JCTermView)v).connection!=null){
          for(int i=0; i<10; i++){
            v=page.showView(JCTermView.ID, new Integer(i).toString(),
                IWorkbenchPage.VIEW_ACTIVATE);
            if((v instanceof JCTermView)&&((JCTermView)v).connection==null){
              break;
            }
            v=null;
          }
        }

        if(v==null)
          return null;

        if(v instanceof JCTermView)
          view=(JCTermView)v;
      }
      catch(PartInitException e){
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return view;
  }

  private IJSchLocation getJSchLocation(final String location){
    IJSchLocation _location=null;
    try{
      _location=JSchLocation.fromString(location);
    }
    catch(JSchCoreException e1){
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }

    _location=new JSchLocationAdapter(_location){
      public void flushUserInfo(){
        flushCache();
      }

      private void flushCache(){
        try{
          Platform.flushAuthorizationInfo(FAKE_URL, location, AUTH_SCHEME);
        }
        catch(CoreException e){
        }
      }

      public void setAllowCaching(boolean value){
        if(value)
          updateCache();
        else
          flushCache();
      }

      private boolean updateCache(){
        // put the password into the Platform map
        Map map=Platform.getAuthorizationInfo(FAKE_URL, location, AUTH_SCHEME);
        if(map==null){
          map=new java.util.HashMap(10);
        }
        if(getPassword()!=null)
          map.put(INFO_PASSWORD, getPassword());
        try{
          Platform.addAuthorizationInfo(FAKE_URL, location, AUTH_SCHEME, map);
        }
        catch(CoreException e){
          // We should probably wrap the CoreException here!
          return false;
        }
        return true;
      }
    };

    String password=null;
    Map map=Platform.getAuthorizationInfo(FAKE_URL, location, AUTH_SCHEME);
    if(map!=null){
      password=(String)map.get(INFO_PASSWORD);
    }
    if(password!=null)
      _location.setPassword(password);
    return _location;
  }
}
