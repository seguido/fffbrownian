package tp3.grupo9.ss;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Cell {
	private Set<Particle> particles;
	private List<Cell> neighbours;
	
	public Cell(){
		particles = new HashSet<Particle>();
		neighbours = new ArrayList<Cell>();
	}
	
	public void addNeighbour(Cell c){
		neighbours.add(c);
	}
	
	public List<Cell> getNeighbours(){
		return neighbours;
	}
	
	public Set<Particle> getParticles(){
		return particles;
	}
}
