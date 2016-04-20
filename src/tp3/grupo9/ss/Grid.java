package tp3.grupo9.ss;

import java.util.Set;

public abstract class Grid {
	
	private Cell[][] cells;
	private double L;
	private int M;
	
	public Grid(double L, int M, Set<Particle> particles){
		this.L = L;
		this.M = M;
		cells = new Cell[M][M];
		for(int i=0; i<M; i++){
			for(int j=0; j<M; j++){
				cells[i][j] = new Cell();
			}
		}
		insertParticles(particles);
		calculateNeighbours();
	}
	
	public abstract void calculateNeighbours();
	
	public void insertParticles(Set<Particle> particles){
		for(Particle p: particles){
			insert(p);
		}
	}
	
	public void insert(Particle p){
		int x = (int) (Math.floor(p.x/(L/M)));
		int y = (int) (Math.floor(p.x/(L/M)));
		cells[x][y].getParticles().add(p);
	}
	
	public Cell getCell(int x, int y){
		return cells[x][y];
	}
	
	public Cell getCell(Particle p){
		int x = (int) (Math.floor(p.x/(L/M)));
		int y = (int) (Math.floor(p.x/(L/M)));
		return getCell(x,y);
	}
	
	public Cell[][] getGrid(){
		return cells;
	}
	
	public int getM(){
		return M;
	}
	
	public double getL() {
		return L;
	}
}
