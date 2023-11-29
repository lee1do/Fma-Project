package PortalProject;

import java.awt.Color;

public class Map {
	Things thing_Rep_Map;
	double[][] ground;
	public Map(double[][] groundLine) {
		this.ground = groundLine;
		int nOV = groundLine.length;
		double posInf = Double.POSITIVE_INFINITY;
		double[] origin = {0.0,0.0};
		thing_Rep_Map = new Things(nOV, posInf, origin, groundLine, posInf, Color.BLACK);
	}
}