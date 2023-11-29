package PortalProject;

import java.util.*;
import java.math.*;
import java.util.Map.*;

public class ShapeMethod {// The outer and inner sides of a triangle.
	double x,y,x1,x2,x3,x4,y1,y2,y3,y4,Gx1,Gx2,Gy1,Gy2;

    public double[] get_center(double[][] vertexs)
    {
        int numberOfVertex = vertexs.length;
        Gx1 = (vertexs[0][0] + vertexs[1][0] + vertexs[2][0])/3;

        Gy1 = (vertexs[0][1] + vertexs[1][1] + vertexs[2][1])/3;

        double [] center = new double [2];
        switch(numberOfVertex)
        {
            case 3:
                Gx1 = (vertexs[0][0] + vertexs[1][0] + vertexs[2][0])/3;

                Gy1 = (vertexs[0][1] + vertexs[1][1] + vertexs[2][1])/3;
                center[0] = Gx1;
                center[1] = Gy1;
                return center;

            case 4:

                x1 = vertexs[0][0];
                x2 = vertexs[1][0];
                x3 = vertexs[2][0];
                x4 = vertexs[3][0];
                
                y1 = vertexs[0][1];
                y2 = vertexs[1][1];
                y3 = vertexs[2][1];
                y4 = vertexs[3][1];

                Gx2=((x1-x3)*((y2-y4)*(x1+x3)+y2*x2-y4*x4)-(x2-x4)*((y1-y3)*(x2+x4)+y1*x1-y3*x3))/(3*(y2-y4)*(x1-x3)-(3*(y1-y3)*(x2-x4)));

                Gy2=((y2-y4)*((x1-x3)*(y2+y4)+x1*y1-x3*y3)-(y1-y3)*((x2-x4)*(y1+y3)+x2*y2-x4*y4))/(3*(x1-x3)*(y2-y4)-(3*(x2-x4)*(y1-y3)));
                
                center[0] = Gx2;
                center[1] = Gy2;
                return center;
                
            case 5,6:
                int sumx = 0;
                int sumy = 0;
                for(int i=0; i<numberOfVertex; i++)
                {
                    sumx += vertexs[i][0];
                    sumy += vertexs[i][1];
                }
                center [0] = sumx/numberOfVertex;
                center [1] = sumy/numberOfVertex;
                return center;

            default:
                
                return null;
        }
    }

    public double[][] get_relative_vertexs(double[][] vertexs, double[] center)
    {
        int numberOfVertex = vertexs.length;

        double[][] point = new double [numberOfVertex][2];

        for (int i=0; i<numberOfVertex; i++)
        {
            point [i][0] = vertexs[i][0] - center[0];
            point [i][1] = vertexs[i][1] - center[1];
        }

        return point;
        
    }

    public double [][] vertexs_clockwise(double[][] vertexs)
    {
        double[] thetas = new double[vertexs.length];
        int numberOfvertexs = vertexs.length;

        double [] center = new double [2];
        center = get_center(vertexs);
        vertexs = get_relative_vertexs(vertexs, center);


        for (int i=0; i<vertexs.length; i++)
        {
            x = vertexs[i][0];
            y = vertexs[i][1];

            if ((double)x == 0)
            {
                if(y>0)
                {
                    thetas[i] = Math.PI*(1/2);
                }

                else
                {
                    thetas[i] = Math.PI*(3/2);
                }
            }

            else
            {
                double temp = y/x;
                thetas[i] = Math.atan(temp);

                if(x<0)
                {
                    thetas[i] += Math.PI;
                }
            }
        }

        for(int j=0; j<thetas.length; j++)
        {
            for(int k=0; k<thetas.length-1; k++)
            {
                if(thetas[k]<thetas[k+1])
                {
                    double tmp = thetas[k];
                    double[] vtmp = vertexs[k];
                    thetas[k] = thetas[k+1];
                    thetas[k+1] = tmp;
                    vertexs[k] = vertexs[k+1];
                    vertexs[k+1] = vtmp;
                }
            }
        }
        return vertexs;
        // for(int l=0; l<thetas.length; l++)
        // {
        //     System.out.println(thetas[l]);
        //     System.out.println("dd = " + vertexs[l][0] + "," + vertexs[l][1]);
        // }
    }

}