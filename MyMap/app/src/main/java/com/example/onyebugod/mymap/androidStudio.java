package com.example.onyebugod.mymap;

import java.util.ArrayList;

public class androidStudio {
    private double area(ArrayList<Double> lat, ArrayList<Double> lng)
    {
        double sum=0;
        double prevcolat=0;
        double prevaz=0;
        double colat0=0;
        double az0=0;
        for (int i=0;i<lat.size();i++)
        {
            double colat=2*Math.atan2(Math.sqrt(Math.pow(Math.sin(lat.get(i)*Math.PI/180/2), 2)+ Math.cos(lat.get(i)*Math.PI/180)*Math.pow(Math.sin(lng.get(i)*Math.PI/180/2), 2)),Math.sqrt(1-  Math.pow(Math.sin(lat.get(i)*Math.PI/180/2), 2)- Math.cos(lat.get(i)*Math.PI/180)*Math.pow(Math.sin(lng.get(i)*Math.PI/180/2), 2)));
            double az=0;
            if (lat.get(i)>=90)
            {
                az=0;
            }
            else if (lat.get(i)<=-90)
            {
                az=Math.PI;
            }
            else
            {
                az=Math.atan2(Math.cos(lat.get(i)*Math.PI/180) * Math.sin(lng.get(i)*Math.PI/180),Math.sin(lat.get(i)*Math.PI/180))% (2*Math.PI);
            }
            if(i==0)
            {
                colat0=colat;
                az0=az;
            }
            if(i>0 && i<lat.size())
            {
                sum=sum+(1-Math.cos(prevcolat  + (colat-prevcolat)/2))*Math.PI*((Math.abs(az-prevaz)/Math.PI)-2*Math.ceil(((Math.abs(az-prevaz)/Math.PI)-1)/2))* Math.signum(az-prevaz);
            }
            prevcolat=colat;
            prevaz=az;
        }
        sum=sum+(1-Math.cos(prevcolat  + (colat0-prevcolat)/2))*(az0-prevaz);
        return 5.10072E14* Math.min(Math.abs(sum)/4/Math.PI,1-Math.abs(sum)/4/Math.PI);
    }
}
