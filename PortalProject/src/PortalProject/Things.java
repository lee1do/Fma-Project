package PortalProject;
import java.awt.Color;
import java.util.Vector;

public class Things {
	int numberOfVertex;
	
	boolean col = false;
	boolean precol = false;
	double[] push_back_con = new double[2];
	double[] jn = new double[2];
	double[] prejn = new double[2];
	
	double[] center = new double[2];
	double[][] vertexs;
	
	double mass;
	double[] velocity = new double[2];
	double[] force = new double[2];
	
	double inertia;
	double[] total_R = new double[2];;
	double theta;
	double angular_velocity;
	double torque;
	
	double angular_force = 0;
	Color color;
	
	public Things() {
		this.numberOfVertex = 3;
		this.inertia =0.0;
		this.mass = 5.0;
		this.theta =0.0;
		this.angular_velocity =0.0;
		this.torque = 0.0;
		this.force[0] = 0.0;
		this.force[1] = 0.0;
		this.velocity[0] = 0.0;
		this.velocity[1] = 0.0;
		this.center[0]=0.0;
		this.center[1]=0.0;
		this.vertexs = new double[3][2];
		this.vertexs[0][0] = 0.0;
		this.vertexs[0][1] = 0.0;
		this.vertexs[1][0] = 0.0;
		this.vertexs[1][1] = 0.0;
		this.vertexs[2][0] = 0.0;
		this.vertexs[2][1] = 0.0;
		this.push_back_con[0] = 0;
		this.push_back_con[1] = 0;
		this.jn[0] = 0.0;
		this.jn[1] = 0.0;
		this.prejn[0] = 0.0;
		this.prejn[1] = 0.0;
		this.total_R[0]=0.0;
		this.total_R[1]=0.0;
		this.color = Color.BLACK;
	}
	
	public Things(int numberOfVertex, double mass, double[] center, double[][] vertexs, double inertia, Color color) {
		this.numberOfVertex = numberOfVertex;
		this.mass = mass;
		this.center = center;
		this.vertexs = vertexs;
		this.inertia = inertia;

		this.theta =0;
		this.angular_velocity =0;
		this.torque = 0;
		this.force[0] = 0;
		this.force[1] = 0;
		this.velocity[0] = 0;
		this.velocity[1] = 0;
		this.jn[0] = 0.0;
		this.jn[1] = 0.0;
		this.prejn[0] = 0.0;
		this.prejn[1] = 0.0;
		this.total_R[0]=0.0;
		this.total_R[1]=0.0;
		this.push_back_con[0] = 0;
		this.push_back_con[1] = 0;
		this.color = color;
	}

}