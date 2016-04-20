package github;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.Map.Entry;

public class BrownianMotionSimulation {
	
    private PriorityQueue<Collision> pq;   
    private double t  = 0.0;      
    private double hz = 0.5;      
    private Particle[] particles; 
    private Map<Double,Integer> collisions = new HashMap<Double, Integer>();
    private Map<Double,Double> trajectory = new HashMap<Double, Double>();
    private ArrayList<Double> velocities = new ArrayList<Double>();

    private BrownianMotionSimulation(Particle[] particles) {
        this.particles = particles;
    }
    
    public static BrownianMotionSimulation makeSimulation(int N) {
    	if ( N < 10 || N > 2000 ) {
    		System.err.println("The number of particles must be between 10 and 2000.");
    		return null;
    	}
		Particle[] particles= new Particle[N];
		particles[0] = new Particle(0.25,0.25,0,0,0.05,100,Color.RED);
	    for (int i = 1; i < N; i++) {
	    	double mass = .1;
	        particles[i] = new Particle(0.005,1,Color.green);
	    }
	    return new BrownianMotionSimulation(particles);
    }

    private void predict(Particle a, double limit) {
        if (a == null) return;
        for (int i = 0; i < particles.length; i++) {
            double dt = a.timeToHit(particles[i]);
            if (t + dt <= limit)
                pq.insert(new Collision(t + dt, a, particles[i]));
        }
        double dtX = a.timeToHitVerticalWall();
        double dtY = a.timeToHitHorizontalWall();
        if (t + dtX <= limit) pq.insert(new Collision(t + dtX, a, null));
        if (t + dtY <= limit) pq.insert(new Collision(t + dtY, null, a));
    }

    public void simulate(double limit) {
    	double lastThird= limit*2.0/3;
    	double lastT=0;
        int colCount=0;
        pq = new PriorityQueue<Collision>();
        for (int i = 0; i < particles.length; i++) {
            predict(particles[i], limit);
        }
        pq.insert(new Collision(0, null, null));       

        collisions.put(Double.valueOf(0), Integer.valueOf(0));
        while (!pq.isEmpty()) { 
        	if (t- lastT >0.2){
        		collisions.put(Double.valueOf(t),Integer.valueOf(colCount) );
        		lastT= t;
        		
        	}
        	
        	
        	System.out.println(t);
            Collision e = pq.delMin();
            if (!e.isValid()) continue;
            Output.getInstace().write(particles, t);
            Particle a = e.a;
            Particle b = e.b;
            for (int i = 0; i < particles.length; i++){
                particles[i].move(e.time - t);
                if (t>= lastThird)
                	velocities.add(Math.sqrt(Math.pow(particles[i].vx, 2)+Math.pow(particles[i].vy, 2)));
                if (particles[i].m ==100 && t-lastT > 0.1){
                	trajectory.put(Double.valueOf(t),Double.valueOf(Math.sqrt(Math.pow(particles[i].x-0.25, 2)+Math.pow(particles[i].y-0.25, 2))));
                }
            }
            t = e.time;
            if (a != null && b != null) a.bounceOff(b);            
            else if (a != null && b == null) a.bounceOffVerticalWall();  
            else if (a == null && b != null) b.bounceOffHorizontalWall(); 
            predict(a, limit);
            predict(b, limit);
            colCount++;
        }
        System.out.println("En tiempo "+ t + " hubo "+ colCount +" colisiones");
    
        try{
			PrintWriter pw = new PrintWriter("colCount.csv");
			pw.close();
		}catch (Exception e){
			e.printStackTrace();
		}
        
        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("colCount.csv", true)))) {
			
        	for (Double e: collisions.keySet()){
        		out.write(e.toString() + "," + collisions.get(e).toString()+"\n" );
        	}
			
			out.close();
		}catch (IOException e) {
		    e.printStackTrace();
		}
        
        try{
			PrintWriter pw = new PrintWriter("velocities.txt");
			pw.close();
		}catch (Exception e){
			e.printStackTrace();
		}
        
        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("velocities.txt", true)))) {
			
        	for (Double e: velocities){
        		out.write(e.toString()+"\n" );
        	}
			
			out.close();
		}catch (IOException e) {
		    e.printStackTrace();
		}
    
    
        try{
			PrintWriter pw = new PrintWriter("trajectory.txt");
			pw.close();
		}catch (Exception e){
			e.printStackTrace();
		}
        
        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("trajectory.txt", true)))) {
			

        	for (Double e: trajectory.keySet()){
        		out.write(e.toString() + "," + trajectory.get(e).toString()+"\n" );
        	}
			
			out.close();
		}catch (IOException e) {
		    e.printStackTrace();
		}
        
    
    }


    private static class Collision implements Comparable<Collision> {
        private final double time;         
        private final Particle a, b;       
        private final int countA, countB;  
                
        public Collision(double t, Particle a, Particle b) {
            this.time = t;
            this.a    = a;
            this.b    = b;
            if (a != null) countA = a.count();
            else           countA = -1;
            if (b != null) countB = b.count();
            else           countB = -1;
        }

        public int compareTo(Collision that) {
            if      (this.time < that.time) return -1;
            else if (this.time > that.time) return +1;
            else                            return  0;
        }
        
        public boolean isValid() {
            if (a != null && a.count() != countA) return false;
            if (b != null && b.count() != countB) return false;
            return true;
        }
   
    }
    
    public static void main(String[] args) {
        	//number of particles
	        int N = 150;
        	makeSimulation(N).simulate(100);
    }
      
}