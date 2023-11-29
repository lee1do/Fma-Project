package PortalProject;
import java.awt.Color;
import java.io.*;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class Stage 
{
	int numberOfThings;
	String background_img_name;
	double grav_acc;
	double drag_coef;
	double friction_factor;
	double move_factor;
    Map map;
    Things[] things;
    
    public Stage(String stageName) throws Exception 
    {
    	String projpath = Main.class.getResource("").getPath();
    	String path = projpath+"/stages/";
    	Object ob = new JSONParser().parse(new FileReader(path+stageName));
    	JSONObject js = (JSONObject) ob;
    	numberOfThings = get_not(js);//.get("numberOfThings");
    	background_img_name = get_back_name(js);
    	grav_acc =get_grav(js);
    	drag_coef=get_drag(js);
    	friction_factor=get_fric(js);
    	move_factor=get_move(js);
    	things = new Things[numberOfThings];
    	for(int i=1; i<=numberOfThings;i++) {
    		JSONObject thingJs = (JSONObject)js.get("thing"+Integer.toString(i));
    		int numberOfVertex = get_nov(thingJs);//thingJs.get("numberOfVertex");
    		double mass = get_mass(thingJs);//
    		double[] center = get_center(thingJs);//(double[])thingJs.get("center");
    		double[][] vertexs = get_ver(thingJs);//(double[][])thingJs.get("vertexs");
    		double inertia = get_iner(thingJs);//(double)thingJs.get("inertia");
    		Color color = get_color(thingJs);
    		this.things[i-1] = new Things(numberOfVertex, mass, center ,vertexs, inertia, color);
    	}
    	double[][] ground = get_ground(js);//(double[][])js.get("ground");
    	this.map = new Map(ground);
    }
    
    public int get_not(JSONObject js) {
    	long not = (long)js.get("numberOfThings");
    	return Long.valueOf(not).intValue();
    }
    public String get_back_name(JSONObject js) {
    	String name = (String)js.get("background_img_name");
    	return name;
    }
    public double[][] get_ground(JSONObject js){
    	JSONArray jsarr = (JSONArray)js.get("ground");
    	double[][] ground=new double[jsarr.size()][2];
    	for(int i=0;i<jsarr.size();i++) {
    		double x = (double)(((JSONArray)jsarr.get(i)).get(0));
    		double y = (double)(((JSONArray)jsarr.get(i)).get(1));
    		ground[i][0] = x;
    		ground[i][1] = y;
    	}
    	return ground;
    }
    public int get_nov(JSONObject thingJs) {
    	long nov = (long)thingJs.get("numberOfVertex");
    	return Long.valueOf(nov).intValue();
    }
    public double get_grav(JSONObject thingJs) {
    	double nov = (double) thingJs.get("grav_acc");
    	return nov;
    }
    public double get_drag(JSONObject thingJs) {
    	double nov = (double) thingJs.get("drag_coef");
    	return nov;
    }
    public double get_fric(JSONObject thingJs) {
    	double nov = (double) thingJs.get("friction_factor");
    	return nov;
    }
    public double get_move(JSONObject thingJs) {
    	double nov = (double) thingJs.get("move_factor");
    	return nov;
    }
    public double get_mass(JSONObject thingJs) {
    	double mass = (double)thingJs.get("mass");
    	return mass;
    }
    
    public double[] get_center(JSONObject thingJs) {
    	JSONArray jsarr = (JSONArray)thingJs.get("center");
    	double[] center=new double[jsarr.size()];
    	for(int i=0;i<jsarr.size();i++) {
    		double a = (double)jsarr.get(i);
    		center[i] = a;
    	}
    	return center;
    }
    public double[][] get_ver(JSONObject thingJs) {
    	JSONArray jsarr = (JSONArray)thingJs.get("vertexs");
    	double[][] vertexs=new double[jsarr.size()][2];
    	for(int i=0;i<jsarr.size();i++) {
    		double x = (double)(((JSONArray)jsarr.get(i)).get(0));
    		double y = (double)(((JSONArray)jsarr.get(i)).get(1));
    		vertexs[i][0] = x;
    		vertexs[i][1] = y;
    	}
    	return vertexs;
    }
    
    public double get_iner(JSONObject thingJs) {
    	double iner = (double)(thingJs.get("inertia"));
    	return iner;
    }
    
    public Color get_color(JSONObject thingJs) {
    	HashMap<String, Color> colormap = new HashMap<String,Color>();
    	colormap.put("white",Color.WHITE);
    	colormap.put("red",Color.RED);
    	colormap.put("green",Color.GREEN);
    	colormap.put("yellow",Color.YELLOW);
    	colormap.put("black",Color.BLACK);
    	colormap.put("blue",Color.BLUE);
    	String colorname = (String)thingJs.get("color");
    	Color color = colormap.get(colorname);
    	return color;
    }

}