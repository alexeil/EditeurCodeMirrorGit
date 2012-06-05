package org.kevoree.library.javase.fileSystemGitRepository; /**
 * Created with IntelliJ IDEA.
 * User: pdespagn
 * Date: 5/15/12
 * Time: 4:53 PM
 * To change this template use File | Settings | File Templates.
 */

import org.eclipse.jgit.api.Git;
import org.kevoree.annotation.*;
import org.kevoree.framework.AbstractComponentType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.io.File;


@Requires(value = @RequiredPort(name = "createRepo", type = PortType.SERVICE, className = GitRepositoryActions.class, optional = true, needCheckDependency = true))
@ComponentType
public class FileSystemGitRepositoryRequest extends AbstractComponentType {

    private MyFrame frame = null;

    @Start
    public void start() {
        frame = new MyFrame();
        frame.setVisible(true);
    }

    @Stop
    public void stop() {
        frame.dispose();
        frame = null;
    }

    private class MyFrame extends JFrame {

        private JButton createRepo, cloneRepo, pushRepo;
        private String login;
        private String password;
        private String nameRepository;


        TextField loginField = new TextField();
        JPasswordField passwordField = new JPasswordField(10);
        TextField nameRepositoryField = new TextField();
        Label errorLabel = new Label();



        TextArea editorField = new TextArea();

        Git git;
        File file;

        public MyFrame() {

            loginField.setText("AccountTest");
            passwordField.setText("AccountTest1");
            nameRepositoryField.setText("createRepositoryTest"+System.currentTimeMillis());

            createRepo = new JButton("createRepo");
            createRepo.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if(isPortBinded("createRepo"))            {
                        errorLabel.setText(getPortByName("createRepo", GitRepositoryActions.class)
                                .createRepository(loginField.getText(), passwordField.getText(), nameRepositoryField.getText())+"");

                    }
                }
            });

            cloneRepo = new JButton("cloneRepo");
            cloneRepo.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(isPortBinded("createRepo"))            {
                        errorLabel.setText("https://"+loginField.getText()+"@github.com/"+loginField.getText()
                                +"/"+nameRepositoryField.getText()+".git");
                        git = getPortByName("createRepo", GitRepositoryActions.class)
                                .cloneRepository("https://"+loginField.getText()+"@github.com/"+loginField.getText()
                                        +"/"+nameRepositoryField.getText()+".git"
                                        ,nameRepositoryField.getText());


                        file =  getPortByName("createRepo", GitRepositoryActions.class)
                                .createFileAndAddToClonedRepository("https://"+loginField.getText()
                                        +"@github.com/"+loginField.getText()+"/"+nameRepositoryField.getText()+".git",git,nameRepositoryField.getText());
                    }
                }
            });


            editorField.addTextListener(new TextListener() {
                @Override
                public void textValueChanged(TextEvent e) {
                    errorLabel.setText(getPortByName("createRepo", GitRepositoryActions.class)
                            .updateContentFileAndCommit(editorField.getText().getBytes(), file, git,loginField.getText())+"");
                }
            });

            pushRepo = new JButton("pushRepo");
            pushRepo.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(isPortBinded("createRepo"))            {
                        errorLabel.setText(getPortByName("createRepo", GitRepositoryActions.class)
                                .pushRepository(git, loginField.getText(), passwordField.getText())+"");

                    }
                }
            });



            Label loginLabel = new Label("Login : ");
            Label passwordLabel = new Label("Password : ");
            Label nameRepositoryLabel = new Label("Nom du repo : ");


            setLayout(new FlowLayout());
            add(loginLabel);
            add(loginField);
            add(passwordLabel);
            add(passwordField);
            add(nameRepositoryLabel);
            add(nameRepositoryField);
            add(createRepo);
            add(cloneRepo);
            add(editorField);
            add(pushRepo);
            add(errorLabel);

            this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

            pack();
            setVisible(true);
        }
    }
}

