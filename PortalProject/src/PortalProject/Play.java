package PortalProject;

import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;

import javax.swing.*;

import PortalProject.Edit.ShapeList;
import PortalProject.Edit.Canvas.MyMouseListener;

import java.awt.event.*;

public class Play extends JFrame
{
	public final int X = 1000;
	public final int Y = 1000;
	public final String title = "Play";
	public BufferStrategy buf;
	public GraphicsEnvironment ge;
	public GraphicsDevice gd;
	public GraphicsConfiguration gc;
	public BufferedImage buffer;
	public Graphics graphics;
	public Graphics2D g2d;
	public boolean Run = true;
	public String Filename;
	
	class myCanvas extends Canvas{
    	MyMouseListener ml = new MyMouseListener();
		myCanvas(){
			addMouseListener(ml);
		}
    	class MyMouseListener extends MouseAdapter{
    		@Override
    		public void mousePressed(MouseEvent e) {
	            	int X = e.getX();
	            	int Y = e.getY();
	            	if(850<X && X<950 && 10<Y && Y<50) {
	            		dispose();
	            		Main main = new Main();
	            		//System.exit(0);
	            	}
    		}
    	}
	}
    	
	
	Play(){
		Stage stage = null;
		String path = Main.class.getResource("").getPath();
		try {
            JFileChooser jfc = new JFileChooser();
            jfc.setCurrentDirectory(new File(path+"/stages"));
            int returnVal = jfc.showSaveDialog(null);
            File file = jfc.getSelectedFile();
            this.Filename = file.getName();
            stage = new Stage(Filename);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
		
		myCanvas canvas = new myCanvas();
        
		PhysicsEngine pe = new PhysicsEngine(stage.grav_acc, 
											stage.drag_coef, 
											stage.friction_factor,
											stage.move_factor);
 
        //Canvas 선언
        canvas.setIgnoreRepaint(true);
        canvas.setSize(X, Y);

        //Canvas 출력
        add(canvas);
        pack();

        //윈도우를 컴포넌트값에 따라 위치 지정 null로 초기화
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Play");
        setSize(1000,1000);
        setVisible(true);
        

		PhysicsEnginePrint(stage, pe, canvas);
    }
	
	public void PhysicsEnginePrint(Stage stage, PhysicsEngine pe, Canvas canvas)
	{
		//더블버퍼링을 통해 화면 깜빡임 해결
        canvas.createBufferStrategy(2);
        buf = canvas.getBufferStrategy();

        //Graphic2D 랜더링 객체 리턴
        ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        gd = ge.getDefaultScreenDevice();
        gc = gd.getDefaultConfiguration();
        buffer = gc.createCompatibleImage(X, Y);

		//True 로 놓고 무한루프
        int frame =0;
		while (frame++<1800)
		{
			//화면에 객체와 맵을 그리는 부분
			g2d = buffer.createGraphics();
			g2d.setColor(Color.WHITE);
			g2d.fillRect(0, 0, X, Y);
			
			drawBack(stage,g2d);
			drawMap(stage, g2d);
			drawThings(stage,g2d);
			drawExit(frame);
			repaint();
			pe.renewal_Things_State(stage);
			
			//화면에 그래픽 출력 후 값 초기화
			graphics = buf.getDrawGraphics();
			graphics.drawImage(buffer, 0, 0, null);
			if (!buf.contentsLost()) buf.show();
			//도형을 그리기 위한 버퍼값 null로 초기화
			//if (graphics != null) graphics.dispose();
			//if (g2d != null) g2d.dispose();
			try {Thread.sleep(3);}catch (InterruptedException e) { } 
			setVisible(true);
		}
	}
	
	public void drawExit(int frame) {
		g2d.setColor(Color.RED);
		g2d.fillRoundRect(850, 10, (int)(frame/18), 40, 10, 10);
		g2d.setColor(Color.BLACK);
		g2d.setFont(new Font("Arial", Font.BOLD, 30));
		g2d.drawString("EXIT",868, 42);
	}
	
	public void drawBack(Stage stage, Graphics2D g2){
		Toolkit toolkit=Toolkit.getDefaultToolkit();
		String name = stage.background_img_name;
		String path = Main.class.getResource("").getPath();
		String fileName = path+"images/"+name+".jpg";
		Image img=toolkit.getImage(fileName);
		g2.drawImage(img, 0, 0, this);
		
	}
	
	public void drawThings(Stage stage, Graphics2D g2) {
		for(int j=0;j<stage.numberOfThings;j++) {
			
			int[] xPoints = new int[stage.things[j].numberOfVertex];
			int[] yPoints = new int[stage.things[j].numberOfVertex];
			
			for(int v =0; v<stage.things[j].numberOfVertex;v++) {
				xPoints[v] = (int)(stage.things[j].vertexs[v][0]+stage.things[j].center[0]);
				yPoints[v] = -(int)(stage.things[j].vertexs[v][1]+stage.things[j].center[1])+1000;
			}
			g2.setColor(stage.things[j].color);
			g2.fillPolygon(xPoints, yPoints, stage.things[j].numberOfVertex);
			
		}
		
	}
	
	public void drawMap(Stage stage, Graphics2D g2) {
		
		double[][] ground = stage.map.ground;
		int[] xPoints = new int[ground.length];
		int[] yPoints = new int[ground.length];
		
		for(int v =0; v<ground.length;v++) {
			xPoints[v] = (int)ground[v][0];
			yPoints[v] = -(int)ground[v][1]+1000;
		}
		g2.setColor(Color.BLACK);
		g2.fillPolygon(xPoints, yPoints, ground.length);
		
	}
	
	public static void main(String[] args){
		new Play();
	}
	
}