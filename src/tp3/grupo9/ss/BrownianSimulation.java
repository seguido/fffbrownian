package tp3.grupo9.ss;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class BrownianSimulation {
	
	private static final double L = 0.5;
	private static final int bruteForceRuns = 1000;
	private boolean brute;
	private Grid grid;
    private PriorityQueue<Event> pq;   
    private double t  = 0;      
    private Set<Particle> particles; 
    private Map<Double,Integer> collisions = new HashMap<Double, Integer>();
    private Map<Double,Double> trajectory = new HashMap<Double, Double>();
    private ArrayList<Double> velocities = new ArrayList<Double>();

    public BrownianSimulation(int N) {
    	particles = new HashSet<>();
		particles.add(new Particle(0.25,0.25,0,0,0.05,100,Color.RED));
	    for (int i = 1; i < N; i++) {
	        particles.add(new Particle(0.005,1,Color.CYAN));
	    }
	    brute = true;
    }
    
    private void predictAvg(Particle p, double maxTime) {
    	for(Cell cell: grid.getCell(p).getNeighbours()){
    		for(Particle p2: cell.getParticles()){
    			double dt = p.collides(p2);
    			if(t + dt<=maxTime)
    				pq.offer(new Event(t + dt, p, p2));
    		}
    	}
    	queueWallCollisions(p, maxTime);
    }
    
    private void predictCollisions(Particle p, double maxTime){
    	if(p == null)
    		return;
    	if(brute)
    		predictBrute(p,maxTime);
    	else
    		predictAvg(p,maxTime);
    	queueWallCollisions(p, maxTime);
    }
    
    private void predictBrute(Particle p, double maxTime) {
        for (Particle p2: particles) {
            double dt = p.collides(p2);
            if (t + dt <= maxTime)
                pq.offer(new Event(t + dt, p, p2));
        }
    }
    
    public void queueWallCollisions(Particle p, double maxTime){
    	double dtX = p.collidesX(L);
        double dtY = p.collidesY(L);
        if (t + dtX <= maxTime)
        	pq.offer(new Event(t + dtX, p, null));
        if (t + dtY <= maxTime)
        	pq.offer(new Event(t + dtY, null, p));
    }
    
    public void run(double maxTime) {
    	double lastThird= maxTime*2.0/3;
    	double lastT=0;
        int colCount=0;
        pq = new PriorityQueue<Event>();
        for (Particle p: particles) {
            predictCollisions(p, maxTime);
        }

        collisions.put(Double.valueOf(0), Integer.valueOf(0));
        
        while (!pq.isEmpty()) {
        	if(colCount == bruteForceRuns && brute){
        		System.out.println("SWITCH");
        		brute = false;
        		grid = new LinearGrid(L, (int)Math.floor(L/(0.1*(t/bruteForceRuns))), particles);
        	}
        	if (t- lastT >0.2){
        		collisions.put(Double.valueOf(t),Integer.valueOf(colCount) );
        		lastT= t;
        	}      	
            Event impendingEvent = pq.poll();
            if (impendingEvent.wasSuperveningEvent()) 
            	continue;
            if(colCount%50==0)
        		System.out.println(t);
            Output.getInstace().write(particles, t);
            Particle p1 = impendingEvent.p1;
            Particle p2 = impendingEvent.p2;
            for (Particle each: particles){
                updatePos(each,impendingEvent.time - t);
                
                if (t>= lastThird)
                	velocities.add(Math.sqrt(Math.pow(each.vx, 2)+Math.pow(each.vy, 2)));
                if (each.m ==100 && t-lastT > 0.1){
                	trajectory.put(Double.valueOf(t),Double.valueOf(Math.sqrt(Math.pow(each.x-0.25, 2)+Math.pow(each.y-0.25, 2))));
                }
            }
            t = impendingEvent.time;
            if(p1 != null && p2 != null)
            	p1.bounce(p2);            
            else if (p1 != null && p2 == null)
            	p1.bounceX();  
            else if (p1 == null && p2 != null)
            	p2.bounceY();
            predictCollisions(p1, maxTime);
            predictCollisions(p2, maxTime);
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
    
    private void updatePos(Particle p, double time){
    	// If brute we only have to update pos
    	if(brute){
    		p.move(time);
    		return;
    	}
    	// Else we also have to update cells
		double cellLength = grid.getL()/grid.getM();
		double x = p.x;
		double y = p.y;
		int cellX = (int) Math.floor(x/cellLength);
		int cellY = (int) Math.floor(y/cellLength);
		p.move(time);
		int newCellX = (int)Math.floor(p.x/cellLength);
		int newCellY = (int)Math.floor(p.y/cellLength);
		if(newCellX < 0 || newCellX >= grid.getM() || newCellY < 0 || newCellY >= grid.getM())
			throw new RuntimeException("Particle out of bounds");
		if(newCellX != cellX ||newCellY != cellY){
			grid.getCell(cellX, cellY).getParticles().remove(p);
			grid.insert(p);
		}
	}


    private static class Event implements Comparable<Event> {
        private final double time;         
        private final Particle p1, p2;       
        private final int p1Collisions, p2Collisions;  
                
        public Event(double t, Particle p1, Particle p2) {
            this.time = t;
            this.p1    = p1;
            this.p2    = p2;
            if (p1 != null) 
            	p1Collisions = p1.getCollisions();
            else
            	p1Collisions = -1;
            if (p2 != null)
            	p2Collisions = p2.getCollisions();
            else
            	p2Collisions = -1;
        }

        public int compareTo(Event other) {
            if(this.time < other.time)
            	return -1;
            else if(this.time > other.time)
            	return +1;
            else
            	return  0;
        }
        
        public boolean wasSuperveningEvent() {
            if (p1 != null && p1.getCollisions() != p1Collisions)
            	return true;
            if (p2 != null && p2.getCollisions() != p2Collisions)
            	return true;
            return false;
        }
        
        public String toString(){
        	return "p1:" + p1 + " p2:" + p2 + " " + time + " ";
        }
    }
}