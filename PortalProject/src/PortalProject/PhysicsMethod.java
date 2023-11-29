package PortalProject;

public class PhysicsMethod {
	double grav_acc;
	double delta_t;
	double move_factor;
	double drag_coef;
	double friction_factor;
	public PhysicsMethod(double grav_acc, double delta_t, double move_factor, double drag_coef, double friction_factor) {
		this.grav_acc = grav_acc;
		this.delta_t = delta_t;
		this.move_factor = move_factor;
		this.drag_coef = drag_coef;
		this.friction_factor = friction_factor;
	}
	
	
	//////////////기본 벡터연산 메소드////////////////////
	public double get_length(double x, double y) {
		return Math.sqrt(Math.pow(x,2)+Math.pow(y,2));
	}
	
	public double[] projection(double[] a, double[] b) { //a를 b에 projection
		double dot = a[0]*b[0]+a[1]*b[1];
		double norm = Math.pow(get_length(b[0],b[1]),2);
		double g = dot/norm;
		double[] res = new double[2];
		res[0] = g*b[0];
		res[1] = g*b[1];
		return res;
	}
	
	public double dotproduct(double[] a, double[] b) {
		return a[0]*b[0]+a[1]*b[1];
	}
	
	
	
	
	//////////////물체 상태 갱신 메소드////////////////////
	
	public void renewal_Drag_T(Things thing, double drag_coefficient) {
		double r = 0;
		for (int i = 0; i < thing.numberOfVertex; i++) {
			r += get_length(thing.vertexs[i][0], thing.vertexs[i][1]);
		}
		thing.torque += thing.angular_velocity * Math.abs(thing.angular_velocity) * drag_coefficient * r;
	}
	
	public void renewal_Drag_F(Things thing, double drag_coefficient) {
		thing.force[0] += -thing.velocity[0] * Math.abs(thing.velocity[0]) * drag_coefficient;
		thing.force[1] += -thing.velocity[1] * Math.abs(thing.velocity[1]) * drag_coefficient;
	}
	
	public void renewal_velocity(Things thing) {
		thing.velocity[0] += thing.jn[0]/thing.mass + thing.force[0]/(thing.mass * this.delta_t);
		thing.velocity[1] += thing.jn[1]/thing.mass + thing.force[1]/(thing.mass * this.delta_t);
	}
	
	public void renewal_angular_velocity(Things thing) {
		double value;
		value = thing.angular_force/thing.inertia + thing.torque/(thing.inertia*this.delta_t);
		thing.angular_velocity += value;
	}
	
	public void renewal_center(Things thing) {
		thing.center[0] += thing.velocity[0]*this.delta_t/move_factor;
		thing.center[1] += thing.velocity[1]*this.delta_t/move_factor;
	}
	
	public void renewal_vertexs(Things thing) {
		double t = thing.angular_velocity*this.delta_t/move_factor;
		for(int j=0;j<thing.numberOfVertex;j++) {
			double befnorm = get_length(thing.vertexs[j][0], thing.vertexs[j][1]);
			thing.vertexs[j][0] = thing.vertexs[j][0]*Math.cos(t)-thing.vertexs[j][1]*Math.sin(t);
			thing.vertexs[j][1] = thing.vertexs[j][0]*Math.sin(t)+thing.vertexs[j][1]*Math.cos(t);
			double afnorm = get_length(thing.vertexs[j][0], thing.vertexs[j][1]);
			
			thing.vertexs[j][0] *=(befnorm/afnorm);
			thing.vertexs[j][1] *=(befnorm/afnorm);
		}
	}
	
	public void renewal_all(Things thing) {
		renewal_Drag_T(thing,this.drag_coef);
		renewal_Drag_F(thing,this.drag_coef);
		renewal_velocity(thing);
		renewal_angular_velocity(thing);
		renewal_center(thing);
		renewal_vertexs(thing);	
	}
	
	
	
	
	//////////////물체 힘 초기화 메소드////////////////////
	
	public void reset(Things thing) {
		if(!thing.col) {
			thing.jn[0] = 0.0;
			thing.jn[1] = 0.0;
			thing.total_R[0] = 0.0;
			thing.total_R[1] = 0.0;
			thing.torque = 0;
		}
	}
	
	
	
	//////////////충돌관련 메소드////////////////////
	
	public double[][] collision_Check(Things thing1, Things thing2) {
		// 두 물체 중 vertex가 안쪽에 들어와있는 물체를 찾고 안쪽에 들어온 vertex가 힘점이 된다.
		double[][] hitPoint = { { 0 }, { 0, 0 } }; // 이 점은 절대좌표여야 한다
		double t1maxv = 0.0;
		double t2maxv = 0.0;
		double t1res = 0.0;
		double t2res = 0.0;
		int max_i = Math.max(thing1.numberOfVertex, thing2.numberOfVertex);

		for (int i = 0; i < max_i; i++) {
			if (i < thing1.numberOfVertex) t1res = get_length(thing1.vertexs[i][0], thing1.vertexs[i][1]);
			if (i < thing2.numberOfVertex) t2res = get_length(thing2.vertexs[i][0], thing2.vertexs[i][1]);
			if (t1res > t1maxv) t1maxv = t1res;
			if (t2res > t2maxv) t2maxv = t2res;
		}
		if (get_length(thing1.center[0] - thing2.center[0], thing1.center[1] - thing2.center[1]) <= t1maxv + t2maxv) {																									// 포함되었다면
			double[] point = new double[2];
			int c=0;
			for (int i = 0; i < thing1.numberOfVertex; i++) {
				point[0] = thing1.vertexs[i][0] + thing1.center[0];
				point[1] = thing1.vertexs[i][1] + thing1.center[1];
				if (contains(point, thing2)) {
					hitPoint[0][0] = 1;
					hitPoint[1][0] += thing1.center[0] + thing1.vertexs[i][0];
					hitPoint[1][1] += thing1.center[1] + thing1.vertexs[i][1];
					c++;
					
				}

			}
			for (int i = 0; i < thing2.numberOfVertex; i++) {
				point[0] = thing2.vertexs[i][0] + thing2.center[0];
				point[1] = thing2.vertexs[i][1] + thing2.center[1];
				if (contains(point, thing1)) {
					hitPoint[0][0] = 2;
					hitPoint[1][0] += thing2.center[0] + thing2.vertexs[i][0];
					hitPoint[1][1] += thing2.center[1] + thing2.vertexs[i][1];
					c++;
				
				}
			}
			if(c!=0) {
				hitPoint[1][0]/=c;
				hitPoint[1][1]/=c;
			}
		}
		//충돌을 안했으면 첫 요소가 0
		return hitPoint;
	}
	
	public boolean contains(double[] point, Things thing) {
		int vnum = thing.numberOfVertex;
		int j;
		int count=0;
		int eqcount =0;
		for(int i = 0;i<vnum;i++) {
			j = (i+1)%vnum;
			if((point[1]>thing.vertexs[i][1]+thing.center[1]) != (point[1]>thing.vertexs[j][1]+thing.center[1])) {
				double x1 = thing.vertexs[i][0]+thing.center[0];
				double x2 = thing.vertexs[j][0]+thing.center[0];
				double y1 = thing.vertexs[i][1]+thing.center[1];
				double y2 = thing.vertexs[j][1]+thing.center[1];
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
			return true;
		}
		else {
			return false;
		}
	}
	
	public void collision_Apply(double[][] cc, Things thing, Things targetThing) {
		if(cc[0][0]!=0) { //충돌했다면
			double[] hitPoint = cc[1];
			double[][] colVecInfo = get_colVec(hitPoint, thing, targetThing, cc[0][0]);
			double[] colVec = colVecInfo[0];
			double[] per_r_AH = {thing.center[1]-hitPoint[1], hitPoint[0]-thing.center[0]};
			double[] per_r_BH = {targetThing.center[1]-hitPoint[1], hitPoint[0]-targetThing.center[0]};
			
			double[] Jn = get_Jn(thing, targetThing, per_r_AH, per_r_BH, colVec);
			if(!thing.precol) {
				apply_fric(thing, Jn);
			}
			push_back_store(thing, colVec, colVecInfo[1][0]);
			thing.jn[0] += Jn[0];
			thing.jn[1] += Jn[1];
			//thing.force[0] +=Jn[0];
			//thing.force[1] +=Jn[1];
			thing.total_R[0] = per_r_AH[0];
			thing.total_R[1] = per_r_AH[1];
			apply_reaction(thing, colVec, thing.total_R);
			thing.angular_force += dotproduct(thing.total_R,thing.jn)/2;
			thing.col = true;
		}
	}
	
	public double[][] get_colVec(double[] hitPoint, Things thing1, Things thing2, double contain) { //contain이 1이면 1번째 물체의 꼭짓점이 상대 물체에 포함 2이면 반대, 뭐가 되었든 반환값은 1번 물체를 향한 벡터
		Things conThing = (contain == 1)? thing2:thing1;
		double[][] colVec = new double[2][2];
		
		double[][] hitPointToVertex = new double[conThing.numberOfVertex][2];
		double[] hitPointToT1Center = new double[2];
		double[][] hitPointToSide = new double[conThing.numberOfVertex][3]; //마지막은 길이
		
		for(int i=0;i<conThing.numberOfVertex;i++) {
			hitPointToVertex[i][0] = conThing.vertexs[i][0]-(hitPoint[0]-conThing.center[0]);
			hitPointToVertex[i][1] = conThing.vertexs[i][1]-(hitPoint[1]-conThing.center[1]);
		}
		
		for(int i=0;i<conThing.numberOfVertex;i++) {
			double[] projVec = new double[2];
			projVec[0] = hitPointToVertex[(i+1)%(conThing.numberOfVertex)][0] - hitPointToVertex[i][0];
			projVec[1] = hitPointToVertex[(i+1)%(conThing.numberOfVertex)][1] - hitPointToVertex[i][1];
			projVec = projection(hitPointToVertex[i], projVec);
			hitPointToSide[i][0] = hitPointToVertex[i][0] - projVec[0];
			hitPointToSide[i][1] = hitPointToVertex[i][1] - projVec[1];
			hitPointToSide[i][2] = get_length(hitPointToSide[i][0],hitPointToSide[i][1]);
		}
		
		colVec[0] = hitPointToSide[0];
		for(int i=1;i<conThing.numberOfVertex;i++) {
			colVec[1][0] = get_length(colVec[0][0],colVec[0][1]);
			if(colVec[1][0] == 0) {
				colVec[0][0] = hitPointToSide[i][1];
				colVec[0][1] = -hitPointToSide[i][0];
				break;
			}
			if(colVec[1][0] > hitPointToSide[i][2]) {
				colVec[0] = hitPointToSide[i];
			}
		}
		colVec[1][0] = get_length(colVec[0][0],colVec[0][1]);
		colVec[0][0] /=colVec[1][0];
		colVec[0][1] /=colVec[1][0];
		
		hitPointToT1Center[0] = thing1.center[0]-hitPoint[0];
		hitPointToT1Center[1] = thing1.center[1]-hitPoint[1];
		if(dotproduct(colVec[0], hitPointToT1Center) <=0) {
			colVec[0][0] = -colVec[0][0];
			colVec[0][1] = -colVec[0][1];
		}
		return colVec;
	}
	
	public void apply_reaction(Things thing, double[] colVec, double[] total_R) {
		if(dotproduct(thing.force, colVec)<0) {
			double[] normalForce =  projection(thing.force, colVec);
			normalForce[0] *=-1;
			normalForce[1] *=-1;
			double[] torVec = projection(normalForce, total_R);
			double[] cenVec = new double[2];
			cenVec[0] = normalForce[0] - torVec[0];
			cenVec[1] = normalForce[1] - torVec[1];
			thing.force[0] += cenVec[0];
			thing.force[1] += cenVec[1];
			thing.angular_force += dotproduct(torVec,total_R)/2;
		}
	}
	
	public void push_back_store(Things thing, double[] colVec, double norm) {
		thing.push_back_con[0] =colVec[0]*norm*1.0;
		thing.push_back_con[1] =colVec[1]*norm*1.0;
	}
	
	public void push_back(Things thing) {
		thing.center[0] += thing.push_back_con[0];
		thing.center[1] += thing.push_back_con[1];
	}
	
	public void apply_fric(Things thing, double[] Jn) {
		double xsign = (Jn[0]/Math.abs(Jn[0]));
		double ysign = (Jn[1]/Math.abs(Jn[1]));
		double Jnxf = xsign*this.friction_factor;
		double Jnyf = ysign*this.friction_factor;
		boolean xfwinjn = Math.abs(Jn[0])>Math.abs(Jnxf);
		boolean yfwinjn = Math.abs(Jn[1])>Math.abs(Jnyf);
		if(Math.abs(thing.force[0])<this.friction_factor) thing.force[0]=0;
		if(Math.abs(thing.force[1])<this.friction_factor) thing.force[1]=0;
		if(xfwinjn) {Jn[0] = Jn[0] - Jnxf;} else {Jn[0]=0; thing.velocity[0] /=1.1;}
		if(yfwinjn) {Jn[1] = Jn[1] - Jnyf;} else {Jn[1]=0; thing.velocity[1] /=1.1;}
	}

	public double[] get_Jn(Things thing1, Things thing2, double[] per_r_AH, double[] per_r_BH, double colVec[]) {
		double[] v_AB = new double[2];
		v_AB[0] = (thing1.velocity[0] + thing1.angular_velocity*per_r_AH[0]) - (thing2.velocity[0] + thing2.angular_velocity*per_r_BH[0]);
		v_AB[1] = (thing1.velocity[1] + thing1.angular_velocity*per_r_AH[1]) - (thing2.velocity[1] + thing2.angular_velocity*per_r_BH[1]);
		double revMassSum = (1/thing1.mass)+(1/thing2.mass);
		double a = Math.pow(dotproduct(per_r_AH,colVec),2)/thing1.inertia;
		double b = Math.pow(dotproduct(per_r_BH,colVec),2)/thing2.inertia;
		double c = dotproduct(v_AB,colVec);
		double j = 1.8*(-1*c)/(revMassSum + a + b);
		double[] jn= {j*colVec[0], j*colVec[1]};
		return jn;
	}
	
}