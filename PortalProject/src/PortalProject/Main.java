package PortalProject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.filechooser.*;

public class Main extends JFrame 
{
    String path = Main.class.getResource("").getPath();
    
    ImageIcon Play_Default = new ImageIcon(path+"/images/Play1.png");
	ImageIcon Play_Click = new ImageIcon(path+"/images/Play2.png");
    ImageIcon Edit_Default = new ImageIcon(path+"/images/Edit1.png");
	ImageIcon Edit_Click = new ImageIcon(path+"/images/Edit2.png");
    ImageIcon Exit_Default = new ImageIcon(path+"/images/Exit1.png");
	ImageIcon Exit_Click = new ImageIcon(path+"/images/Exit2.png");
    ImageIcon LOGO = new ImageIcon(path+"/images/logo.jpg");
    JButton play;
    JButton edit;
    JButton exit;
    JLabel label;

    public Main() 
    {
        draw();
    }

    public void draw()
    {
        //프레임 선언
        JPanel jPanel = new JPanel();
        setTitle("Main");
        setSize(500, 700);
        add(jPanel);
        jPanel.setBackground(Color.WHITE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //logo -> LOGO -> logo2 -> Main_logo resizing
        Image logo = LOGO.getImage();
		Image logo2 = logo.getScaledInstance(500, 500, Image.SCALE_DEFAULT);
        ImageIcon Main_logo = new ImageIcon(logo2);

        JLabel img = new JLabel(Main_logo);
        jPanel.add(img);

        play = new JButton(Play_Default);
		play.setRolloverIcon(Play_Click);
		play.setBorderPainted(false);
		play.setBounds(200, 200, 122, 30);
		play.setBorder(BorderFactory.createEmptyBorder());
		
        edit = new JButton(Edit_Default);
		edit.setRolloverIcon(Edit_Click);
		edit.setBorderPainted(false);
		edit.setBounds(30, 170, 122, 30);
		edit.setBorder(BorderFactory.createEmptyBorder());
		
        exit = new JButton(Exit_Default);
		exit.setRolloverIcon(Exit_Click);
		exit.setBorderPainted(false);
		exit.setBounds(30, 170, 122, 30);
		exit.setBorder(BorderFactory.createEmptyBorder());


        jPanel.add(play);
        jPanel.add(edit);
        jPanel.add(exit);
		
		add(jPanel);
		
		setVisible(true);
 
        play.addActionListener(new ActionListener() {
            @Override
            
            
            
            
            public void actionPerformed(ActionEvent e) {
                dispose();
				Play play = new Play();
            }
        });
        
        edit.addActionListener(new ActionListener() {
        	@Override
            public void actionPerformed(ActionEvent e) {
        		dispose();
        		Edit editor = new Edit();
            }
        });

        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
                setVisible(false);
            }
        });
    }

    public static void main(String[] args) {
        new Main();
    }
}