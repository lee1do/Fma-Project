package PortalProject;
import java.util.*;
public class PhysicsEngine {
	double delta_t = 1;
	double grav_acc;
	double drag_coef;
	double fric_fact;
	double move_factor;
	PhysicsMethod pm;
	
	public PhysicsEngine(double gravitational_acceleration, 
						double drag_coefficient, 
						double friction_factor,
						double move_factor) {
		this.grav_acc = gravitational_acceleration;
		this.drag_coef = drag_coefficient; //항력 계수
		this.fric_fact = friction_factor; //마찰력 계수
		this.move_factor = move_factor;
		this.pm = new PhysicsMethod(this.grav_acc, this.delta_t, this.move_factor, this.drag_coef, this.fric_fact);
	}
	
	//편의를 위해 모든 충돌은 완전탄성충돌이고 두 물체가 충돌시에 각각 받는 힘은 일정하며 delta_t시간동안만 작용한다고 가정한다
	public void renewal_Things_State(Stage stage) { //인자로 받은 stage내의 물체와 맵의 상태를 갱신시킨다.
		
		for(int i =0; i<stage.numberOfThings;i++) {
			Things thing = stage.things[i];
			
			for(int j= 0; j<stage.numberOfThings+1;j++) {	
				if(i == j) continue;
				Things targetThing = (j == stage.numberOfThings)? stage.map.thing_Rep_Map:stage.things[j];
				pm.collision_Apply(pm.collision_Check(thing,targetThing), thing, targetThing);
			}
		}
		
		for(int i =0; i<stage.numberOfThings;i++) {
			Things thing = stage.things[i];

			//pm.reset(thing);
			//pm.renewal_all(thing);
			if(thing.col) {
				pm.push_back(thing);
				
				if(Math.abs(Math.abs(thing.jn[0]))<3.1) {
					double xabs = Math.abs(thing.jn[0]);
					thing.jn[0] *= 1/(1+Math.pow(2,-xabs*100000));
					thing.velocity[0] *= 1/(1+Math.pow(2,-xabs*100000));
					thing.angular_velocity *= 1/(1+Math.pow(2,-xabs*100000));
				}
				if(Math.abs(Math.abs(thing.jn[1]))<3.1) {
					double yabs = Math.abs(thing.jn[1]);
					thing.jn[1] *= 1/(1+Math.pow(2,-yabs*100000));
					thing.velocity[1] *= 1/(1+Math.pow(2,-yabs*100000));
					thing.angular_velocity *= 1/(1+Math.pow(2,-yabs*100000));
				}
			}
			
	
			pm.renewal_Drag_T(thing,this.drag_coef);
			pm.renewal_Drag_F(thing,this.drag_coef);
			
			pm.renewal_velocity(thing);
			pm.renewal_angular_velocity(thing);
			
			pm.renewal_center(thing);
			pm.renewal_vertexs(thing);
			
			thing.force[0] = 0.0;
			thing.force[1] = -thing.mass * this.grav_acc;
			thing.angular_force =0;
			thing.torque = 0;
			thing.jn[0] = 0.0;
			thing.jn[1] = 0.0;
			thing.total_R[0] = 0.0;
			thing.total_R[1] = 0.0;
			thing.precol = thing.col;
			thing.col = false;
			
		}
	}
}
