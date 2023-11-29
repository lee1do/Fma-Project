package PortalProject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.geom.Line2D;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.awt.image.*;
import javax.swing.JOptionPane;
import org.json.simple.*;
import java.util.List;
public class Edit extends JFrame
{
	Double[][] flat = {{-1000.,100.},{2000.,100.},{2000.,0.},{-1000.,0.}};
	Double[][] cliff = {{-1000.,100.},{500.,100.},{500.,500.},{2000.,500.},{2000.,0.},{-1000.,0.}};
	Double[][] slope = {{-1000.,100.},{2000.,500.},{2000.,0.},{-1000.,0.}};
	String[] maps = {"flat", "cliff", "slope"};
	String[] backgroundskins = {"default", "morning", "space", "night"};
	String[] skins = {"black", "red", "green", "yellow", "blue"};
	HashMap<String,Color> colormap = new HashMap<String,Color>();
	HashMap<String,Double[][]> mapmap = new HashMap<String,Double[][]>();
	int[][] pointtmp = new int[7][2];
	int focusThingIndex;
    int vertex = 0;
    int count = 0;
    boolean drawing =false;// (count<vertex && vertex !=0);
    boolean full = false;
    JButton[] menubarBtns = new JButton[2];
    JButton[] toolbarBtns = new JButton[4];
    JTextField grav_acc_tf = new JTextField("9.8",8);
	JTextField drag_coef_tf = new JTextField("0.0003",8);
	JTextField fric_fact_tf = new JTextField("1",8);
	JTextField move_fact_tf = new JTextField("600",8);
    JLabel drawing_info = new JLabel("You are now drawing : Nothing    ");
    JLabel shape_max = new JLabel("Thing : 0 / 10");
    JLabel backgroundImg = new JLabel();
    JTextField mass_tf = new JTextField("10", 8);
	JComboBox skin_cb = new JComboBox(skins);
	JComboBox back_cb = new JComboBox(backgroundskins);
	JComboBox map_choice = new JComboBox(maps);
	JButton save_obj_btn = new JButton("save object");
	ShapeList shapelist = new ShapeList();
    class ShapeList{
    	Shape[] listOfShape = new Shape[10];
    	int index;
    	public ShapeList(){
    		index =0;
    	}
    	
    	public void addShape(int numberOfVertex, double[][] vertexs, double mass, Color color) {
    		
    		listOfShape[index] = new Shape(numberOfVertex, vertexs, mass, color);
    		index++;
    	}
    	class Shape{
        	int numberOfVertex;
        	double[] center;
        	double[][] vertexs;
        	double mass;
        	double inertia;
        	Color color;
        	public Shape(int nov, double[][] ver, double mass, Color color) {
        		this.numberOfVertex = nov;
        		this.center = new double[2];
        		this.vertexs = ver;
        		this.mass = mass;
        		this.inertia = mass*1000;
        		this.color = color;
        	}
        }
    }
    
    Edit() {
    	colormap.put("white",Color.WHITE);
    	colormap.put("red",Color.RED);
    	colormap.put("green",Color.GREEN);
    	colormap.put("yellow",Color.YELLOW);
    	colormap.put("black",Color.BLACK);
    	colormap.put("blue",Color.BLUE);
    	mapmap.put("flat",flat);
    	mapmap.put("cliff",cliff);
    	mapmap.put("slope",slope);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mass_tf.setEnabled(false);
		skin_cb.setEnabled(false);
		save_obj_btn.setEnabled(false);
        //변수 초기화
		menubarBtns[0] = new JButton("Save");
	    menubarBtns[1] = new JButton("Help");
       
		toolbarBtns[0] = new JButton("triangle");
		toolbarBtns[1] = new JButton("rectangle");
		toolbarBtns[2] = new JButton("pentagon");
		toolbarBtns[3] = new JButton("hexagon");
		//구조 생성부
		Canvas can = new Canvas();
		can.setSize(1000, 1000);
		can.setBackground(Color.WHITE);
        JToolBar jtb = createToolBar(menubarBtns ,toolbarBtns);
		JPanel top_p = createTop(jtb);
		JPanel east_p = createEast(drawing_info, mass_tf, skin_cb, 
									grav_acc_tf, save_obj_btn, 
									drag_coef_tf, fric_fact_tf, 
									move_fact_tf, shape_max);
		Container contentPane = createPane(top_p, east_p, can);

       
        //이벤트 추가
		save_obj_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	shapelist.listOfShape[focusThingIndex].mass = Double.parseDouble(mass_tf.getText());
            	shapelist.listOfShape[focusThingIndex].inertia = shapelist.listOfShape[focusThingIndex].mass*1000;
            	String c = skin_cb.getSelectedItem().toString();
            	shapelist.listOfShape[focusThingIndex].color = colormap.get(c);
            }
        });
        menubarBtns[0].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	JSONObject jo = new JSONObject();
            	ShapeMethod sm = new ShapeMethod();
            	
            	//맵, 배경, 각종 인자값 제이슨파일에 저장하기
            	String mapChoice = map_choice.getSelectedItem().toString();
            	
            	Double[][] ground = mapmap.get(mapChoice);
            	String background_img_name = back_cb.getSelectedItem().toString();
            	double grav_acc = Double.parseDouble(grav_acc_tf.getText());
            	double drag_coef = Double.parseDouble(drag_coef_tf.getText());
            	double friction_factor = Double.parseDouble(fric_fact_tf.getText());
            	double move_factor = Double.parseDouble(move_fact_tf.getText());
            	
            	List<List<Double>> ground_list = new ArrayList<List<Double>>();
            	for(int i=0;i<ground.length;i++) {
            		List<Double> list = Arrays.asList(ground[i]);
            		ground_list.add(i,list);
            	}
            	jo.put("numberOfThings",shapelist.index);
            	jo.put("grav_acc",grav_acc);
            	jo.put("drag_coef",drag_coef);
            	jo.put("friction_factor",friction_factor);
            	jo.put("move_factor",move_factor);
            	jo.put("background_img_name",background_img_name);
            	jo.put("ground",ground_list);
            	
            	//물체를 제이슨파일에 저장하기
            	for(int i=0;i<shapelist.index;i++) {
            		Edit.ShapeList.Shape thing = shapelist.listOfShape[i];
            		LinkedHashMap m = new LinkedHashMap(5);
            		
            		String color = "black";
            		Set set = colormap.keySet();
            		Iterator iterator = set.iterator();
            		while(iterator.hasNext()) {
            			String key = (String)iterator.next();
            			if(thing.color.equals(colormap.get(key))) {
            				color = key;
            			}
            		}
            		thing.center = sm.get_center(thing.vertexs);
            		thing.vertexs = sm.vertexs_clockwise(thing.vertexs);
            		List<Double> centerlist = new ArrayList<Double>();
            		centerlist.add(0,thing.center[0]);
            		centerlist.add(1,thing.center[1]);
            		List<List<Double>> vertexslist = new ArrayList<List<Double>>();
            		for(int j=0;j<thing.numberOfVertex;j++) {
            			Double verBoxedList[] = Arrays.stream(thing.vertexs[j]).boxed().toArray(Double[]::new);
                		List<Double> list = Arrays.asList(verBoxedList);
                		vertexslist.add(j,list);
                	}
            		
            		
            		m.put("numberOfVertex",thing.numberOfVertex);
            		m.put("mass",thing.mass);
            		m.put("inertia",thing.inertia);
            		m.put("color",color);
            		m.put("center",centerlist);
            		m.put("vertexs",vertexslist);
            		String joKey = "thing"+Integer.toString(i+1);
            		jo.put(joKey, m);
            	}
            	
            	

            	JLabel label = new JLabel();
            	JFrame frame = new JFrame("Output");
				try {
					
					JFileChooser fileChooser = new JFileChooser();
					String path = Main.class.getResource("").getPath();
					fileChooser.setCurrentDirectory(new File(path+"/stages"));
		            int option = fileChooser.showSaveDialog(frame);
		            if(option == JFileChooser.APPROVE_OPTION){
		            	File file = fileChooser.getSelectedFile();
		            	label.setText("File Saved as: " + file.getName());
		            	FileWriter pw = new FileWriter(file + ".json", true);
		                pw.write(jo.toJSONString());
		                pw.flush();
		                pw.close();
		            }
		            else
		            {
		               label.setText("Save command canceled");
		            }
		            dispose();
		            Main main = new Main();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                
            }
        });
        menubarBtns[1].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	String help = "save  -  현재 작성한 파일을 저장합니다. \n\n\n"
            			+ "shape  -  그리고 싶은 도형의 버튼을 클릭하고 화면위에 마우스로 원하는 위치를 클릭하면 클릭한 좌표를 꼭짓점으로 하는 도형이 생성됩니다. \n\n\n"
            			+ "thing  -  현재 만든 도형과 최대로 만들 수 있는 도형의 개수가 표시됩니다. \n\n\n"
            			+ "you are now drawing  -  현재 만들고있는 도형을 알려줍니다. \n\n\n"
            			+ "mass. skin, save object  -  만들어진 도형을 선택하여 그 도형의 질량과 스킨을 설정하고 save object 버튼으로 저장합니다.\n\n\n"
            			+ "grav_acc  -  중력가속도를 설정합니다. 0.0~20.0 정도가 바람직합니다. \n\n\n"
            			+ "drag_coef  -  공기저항 계수를 설정합니다. 0.0003 ~ 0.00003 정도가 바람직합니다. \n\n\n"
            			+ "fric_factor  -  모든 물체의 마찰계수를 설정합니다. 0.8~1.0 정도가 바람직합니다.\n\n\n"
            			+ "move_factor  -  물체가 움직이는 정도를 설정합니다. 500~800 정도가 바람직힙나다.";
            	JOptionPane.showMessageDialog(null,help,"Help",JOptionPane.PLAIN_MESSAGE);
            }
        });
        toolbarBtns[0].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(!full) {
	                drawing_info.setText("You are now drawing : Triangle   ");
	                count = 0;
	                vertex = 3;
	                pointReset();
	                drawing = true;
            	}
            }
        });
        toolbarBtns[1].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(!full) {
	                drawing_info.setText("You are now drawing : Rectangle");
	                count = 0;
	                vertex = 4;
	                pointReset();
	                drawing = true;
            	}
            }
        });
        toolbarBtns[2].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(!full) {
	                drawing_info.setText("You are now drawing : Pentagon ");
	                count = 0;
	                vertex = 5;
	                pointReset();
	                drawing = true;
            	}
            }
        });
        toolbarBtns[3].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(!full) {
	                drawing_info.setText("You are now drawing : Hexagon  ");
	                count = 0;
	                vertex = 6;
	                pointReset();
	                drawing = true;
            	}
            }
        });       
        
        setTitle("Edit");
        setSize(1250,1050);
        setVisible(true);
	}
    

    public void pointReset() {
    	for(int i=0;i<pointtmp.length;i++) {
    		pointtmp[i][0] = 0;
    		pointtmp[i][1] = 0;
    	}
    }
    
    public JToolBar createToolBar(JButton[] menuBtns, JButton[] toolbarBtns) {
    	JToolBar jtb = new JToolBar();
    	jtb.add(new JLabel("  menu : "));
    	jtb.add(menuBtns[0]);
    	jtb.add(menuBtns[1]);
    	jtb.add(new JLabel("  shape : "));
		jtb.add(toolbarBtns[0]);
		jtb.add(toolbarBtns[1]);
		jtb.add(toolbarBtns[2]);
		jtb.add(toolbarBtns[3]);
    	return jtb;
    }
    
    public JPanel createTop(JToolBar jtb) {
    	JPanel top_p = new JPanel();
		top_p.setLayout(new GridLayout(1,1,0,0));
		top_p.add(jtb);
		return top_p;
    }
    
    public JPanel createEast(JLabel drawing_info, JTextField mass_tf, JComboBox skin_cb, JTextField grav_acc_tf, 
    						JButton equilateral_btn, JTextField drag_coef_tf, JTextField fric_fact_tf, JTextField move_fact_tf, JLabel shape_max) {
    	JPanel east_p = new JPanel();
		JPanel empty_p = new JPanel();
		JPanel east_zero_p = new JPanel();
		JPanel east_one_p = new JPanel();
		JPanel east_two_p = new JPanel();
		JPanel east_thr_p = new JPanel();
		
		east_p.setLayout(new GridLayout(5,1,0,0));
		east_zero_p.setLayout(new GridLayout(4,1,0,5));
		east_one_p.setLayout(new GridLayout(7,1,0,5));
		east_two_p.setLayout(new GridLayout(6,1,0,5));
		east_thr_p.setLayout(new GridLayout(9,1,0,5));
		
		empty_p.setBackground(new Color(200, 200, 200));
		east_zero_p.setBackground(new Color(200, 200, 200));
		east_one_p.setBackground(new Color(200, 200, 200));
		east_two_p.setBackground(new Color(200, 200, 200));
		east_thr_p.setBackground(new Color(200, 200, 200));
		east_p.setBackground(new Color(200, 200, 200));
		
		east_zero_p.add(new JLabel("<drawing information>"));
		east_zero_p.add(shape_max);
		east_zero_p.add(drawing_info);
		
		east_one_p.add(new JLabel("<set object>"));
		east_one_p.add(empty_p);
		east_one_p.add(new JLabel("set mass"));
		east_one_p.add(mass_tf);
		east_one_p.add(new JLabel("set skin"));
		east_one_p.add(skin_cb);
		east_one_p.add(equilateral_btn);
		
		east_two_p.add(empty_p);
		east_two_p.add(new JLabel("<choose background and map>"));
		east_two_p.add(back_cb);
		east_two_p.add(map_choice);
		
		east_thr_p.add(new JLabel("<set environment>"));
		east_thr_p.add(new JLabel("set grav_acc"));
		east_thr_p.add(grav_acc_tf);
		east_thr_p.add(new JLabel("set drag_coef"));
		east_thr_p.add(drag_coef_tf);
		east_thr_p.add(new JLabel("set friction_factor"));
		east_thr_p.add(fric_fact_tf);
		east_thr_p.add(new JLabel("set move_factor"));
		east_thr_p.add(move_fact_tf);
		
		east_p.add(east_zero_p);
		east_p.add(east_one_p);
		east_p.add(east_two_p);
		east_p.add(east_thr_p);
		east_p.setPreferredSize(new Dimension(250,1000));
		return east_p;
    }
    
    public JPanel createCenter(Canvas can) {
    	JPanel center_p = new JPanel();
		center_p.setLayout(new FlowLayout(FlowLayout.CENTER));
		center_p.add(can);
		return center_p;
    }
    
    public Container createPane(JPanel top_p, JPanel east_p , JPanel can) {
    	Container contentPane = getContentPane();
		contentPane.setBackground(Color.WHITE);
		contentPane.setLayout(new BorderLayout());
		contentPane.add(top_p,BorderLayout.NORTH);
		contentPane.add(east_p,BorderLayout.EAST);
		contentPane.add(can,BorderLayout.CENTER);
		return contentPane;
    }
    
    public static void main(String[] args) {
		new Edit();
	}
    
    class Canvas extends JPanel{
    	MyMouseListener ml = new MyMouseListener();
		Canvas(){
			addMouseListener(ml);
		}
    	class MyMouseListener extends MouseAdapter{
    		@Override
    		public void mousePressed(MouseEvent e) {
    			int X= e.getX();
				int Y= e.getY();
				Y = -Y + 1000;
				focusThingIndex = Contains_Any_Shape(X,Y);
    			if(drawing && !full) {
 
					if(focusThingIndex ==-1) {
						change_info();
						user_draw(X,Y);
					}
    			}else {
    				if(focusThingIndex!=-1) {
    					mass_tf.setEnabled(true);
    					skin_cb.setEnabled(true);
    					save_obj_btn.setEnabled(true);
    					mass_tf.setText(Double.toString(shapelist.listOfShape[focusThingIndex].mass));
    					skin_cb.setSelectedIndex(0);
    				}else {
    					mass_tf.setEnabled(false);
    					skin_cb.setEnabled(false);
    					save_obj_btn.setEnabled(false);
    				}
    			}
    		}
    	}
    	
    	public int Contains_Any_Shape(int X,int Y) {
    		int[] point = {X,Y};
    		for(int k=0;k<shapelist.index;k++) {
	    		int vnum = shapelist.listOfShape[k].numberOfVertex;
	    		int j;
	    		int count=0;
	    		int eqcount =0;
	    		for(int i = 0;i<vnum;i++) {
	    			j = (i+1)%vnum;
	    			if((point[1]>shapelist.listOfShape[k].vertexs[i][1]) != (point[1]>shapelist.listOfShape[k].vertexs[j][1])) {
	    				double x1 = shapelist.listOfShape[k].vertexs[i][0];
	    				double x2 = shapelist.listOfShape[k].vertexs[j][0];
	    				double y1 = shapelist.listOfShape[k].vertexs[i][1];
	    				double y2 = shapelist.listOfShape[k].vertexs[j][1];
	    				double x_dif = x1-x2;
	    				double y_dif = y1-y2;
	    				double con = (x1*y2-x2*y1)/x_dif;
	    				double res = (point[1]-con)*(x_dif/y_dif);
	    				if(x_dif == 0) {
	    					res = x1;
	    				}
	    				if(point[0] < res) count++;
	    				else if(point[0] == res) eqcount++;
	    			} 
	    		}
	    		if(count%2 != 0 || eqcount>0) {
	    			return k;
	    		}
    		}
    		return -1;
    	}
    	
    	public void change_info() {
    		String info = drawing_info.getText();
			info = info.replaceAll("[0-9\\(\\)\\/]","");
			drawing_info.setText(info+"("+Integer.toString(count+1)+"/"+Integer.toString(vertex)+")");
    	}
    	
    	public void user_draw(int X, int Y) {
    		pointtmp[count][0]= X;
			pointtmp[count][1]= Y;
			count++;
			if(count == vertex) {
				
				double[][] ver = new double [count][2]; 
				for(int i=0;i<count;i++) {
					ver[i][0] = (double)pointtmp[i][0];
					ver[i][1] = (double)pointtmp[i][1];
				}
				Color c = Color.BLACK;
				
				shapelist.addShape(count, ver, 10, c);
				drawing = false;
				drawing_info.setText("You are now drawing : Nothing");
				shape_max.setText("Thing : "+Integer.toString(shapelist.index)+" / 10");
				full = shapelist.index==shapelist.listOfShape.length;
			}
    	}
    	
    	public void drawTmp(Graphics2D g2) {
    		for(int i=0;i<pointtmp.length;i++) {
    			int j = (i+1)%pointtmp.length;
    			if(pointtmp[j][0]==0) break;
    			g2.drawLine(pointtmp[i][0],-pointtmp[i][1]+1000,pointtmp[j][0],-pointtmp[j][1]+1000);
    		}
    	}
    	
    	public void drawShapes(ShapeList shapelist, Graphics2D g2) {
			for(int j=0;j<shapelist.index;j++) {
				int[] xPoints = new int[shapelist.listOfShape[j].numberOfVertex];
				int[] yPoints = new int[shapelist.listOfShape[j].numberOfVertex];
				
				for(int v =0; v<shapelist.listOfShape[j].numberOfVertex;v++) {
					xPoints[v] = (int)shapelist.listOfShape[j].vertexs[v][0];
					yPoints[v] = -(int)shapelist.listOfShape[j].vertexs[v][1]+1000;
				}
				g2.setColor(shapelist.listOfShape[j].color);
				g2.fillPolygon(xPoints, yPoints, shapelist.listOfShape[j].numberOfVertex);
			}
		}
    	
    	public void drawBack(Graphics2D g2) throws IOException {
    		Toolkit toolkit=Toolkit.getDefaultToolkit();
    		String name = back_cb.getSelectedItem().toString();
    		String path = Main.class.getResource("").getPath();
    		String fileName = path+"images/"+name+".jpg";
    		Image img=toolkit.getImage(fileName);
    		g2.drawImage(img, 0, 0, this);
    	}
    	
    	public void drawMap(Graphics2D g2) {
    		String name = map_choice.getSelectedItem().toString();
    		Double[][] ground = mapmap.get(name);
    		int[] xPoints = new int[ground.length];
			int[] yPoints = new int[ground.length];
			
			for(int v =0; v<ground.length;v++) {
				xPoints[v] = ground[v][0].intValue();
				yPoints[v] = -ground[v][1].intValue()+1000;
			}
			g2.setColor(Color.BLACK);
			g2.fillPolygon(xPoints, yPoints, ground.length);
    	}
    	
    	public void paintComponent(Graphics g) {
    		super.paintComponent(g);
    		Graphics2D g2 =(Graphics2D)g;
    		try {
				drawBack(g2);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		drawTmp(g2);
    		drawShapes(shapelist, g2);
    		drawMap(g2);
    		
    		repaint();
    	}
    	
    }
    
}

