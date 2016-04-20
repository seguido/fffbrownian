package tp3.grupo9.ss;

import java.awt.Color;

public class Particle {
    private static final double INFINITY = Double.MAX_VALUE;

    static int counter = 1;
    public double x, y;    
    public double vx, vy; 
    public double r;    
    public double m;   
    private Color c;     
    private int collisions;
    public int ID;

    
    public Particle(double rx, double ry, double vx, double vy, double radius, double mass, Color color) {
        this.vx = vx;
        this.vy = vy;
        this.x = rx;
        this.y = ry;
        this.r = radius;
        this.m  = mass;
        this.c  = color;
        this.ID = counter++;
    }
    
    public Particle(double r, double m,  Color c) {
    	 x = (Math.random() * (0.5-2*r)) + r;
         y = (Math.random() * (0.5-2*r)) + r;
         vx = 0.1 * (Math.random()*2 - 1);
         vy = Math.sqrt(0.1*0.1-vx*vx)*(Math.random()<0.5?1:-1);
         this.r = r;
         this.m   = m;
         this.c  = c;
         this.ID = counter++;
  	}

  
    public void move(double dt) {
        x += vx * dt;
        y += vy * dt;
    }

    public int getCollisions(){
    	return collisions;
    }
        
    public double collides(Particle p2) {
        if (this.equals(p2))
        	return INFINITY;
        double dx  = p2.x - this.x;
        double dy  = p2.y - this.y;
        double dvx = p2.vx - this.vx;
        double dvy = p2.vy - this.vy;
        double dvdr = dx*dvx + dy*dvy;
        if (dvdr > 0) return INFINITY;
        double dvdv = dvx*dvx + dvy*dvy;
        double drdr = dx*dx + dy*dy;
        double sigma = this.r + p2.r;
        double d = (dvdr*dvdr) - dvdv * (drdr - sigma*sigma);
        if (d < 0) return INFINITY;
        double ans = -(dvdr + Math.sqrt(d)) / dvdv;
    	return ans >0 ? ans : INFINITY;
    }


    public double collidesX(double L) {
        if(vx > 0)
        	return (L-r-x) / vx;
        else if(vx < 0)
        	return (r-x) / vx;  
        return INFINITY;
    }

    public double collidesY(double L) {
        if (vy > 0)
        	return (L-r-y) / vy;
        else if (vy < 0)
        	return (r-y) / vy;
        return INFINITY;
    }

    public void bounce(Particle b) {
    	this.collisions++;
        b.collisions++;
        double dx  = b.x - this.x;
        double dy  = b.y - this.y;
        double dvx = b.vx - this.vx;
        double dvy = b.vy - this.vy;
        double dvdr = dx*dvx + dy*dvy;            
        double dist = this.r + b.r; 
        double J = 2 * this.m * b.m * dvdr / ((this.m + b.m) * dist);
        double jx = J * dx / dist;
        double jy = J * dy / dist;
        this.vx += jx / this.m;
        this.vy += jy / this.m;
        b.vx -= jx / b.m;
        b.vy -= jy / b.m;
    }

    public void bounceX() {
        vx = -vx;
        collisions++;
    }

    public void bounceY() {
        vy = -vy;
        collisions++;
    }
    
    public int hashCode(){
    	return ID;
    }
    
    public boolean equals(Object o){
    	if(o == null)
    		return false;
    	if(o.getClass() != this.getClass())
    		return false;
    	Particle other = (Particle) o;
    	if(other.ID != this.ID)
    		return false;
    	return true;
    }
    
    @Override
    public String toString() {
    	return "" + ID;
    }
}