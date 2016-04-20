package tp3.grupo9.ss;
import java.util.Set;


public class LinearGrid extends Grid{
	
	public LinearGrid(double L, int M,Set<Particle> particles){
		super(L,M,particles);
	}
	
	public void calculateNeighbours(){
		for(int i=0; i<getM(); i++){
			for(int j=0; j<getM(); j++){
				if(i-1>=0){
					getGrid()[i][j].addNeighbour(getGrid()[i-1][j]);
					if(j+1<getM())
						getGrid()[i][j].addNeighbour(getGrid()[i-1][j+1]);
				}
				if(j+1<getM()){
					getGrid()[i][j].addNeighbour(getGrid()[i][j+1]);
					if(i+1<getM())
						getGrid()[i][j].addNeighbour(getGrid()[i+1][j+1]);
				}
			}
		}
	}
}
