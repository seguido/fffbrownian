package github;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;


public class Output {
	private static Output instance = null;
	private static int count = 0;
	
	public static Output getInstace(){
		if(instance == null)
			instance = new Output();
		return instance;
	}

	public void write(Particle[] p, double time){
		if(time == 0){
			try{
				PrintWriter pw = new PrintWriter("output.xyz");
				pw.close();
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("output.xyz", true)))) {
			out.write((p.length+4) + "\n");
			//comment line
			//System.out.println("Frame : " + count++);
			out.write("Comment line\n");
			out.write(25000 + "\t" + 0 + "\t" + 0 + "\t" + 0.005 + "\t0\t0\t0" + "\n");
			out.write(25001 + "\t" + 0 + "\t" + 0.5 + "\t" + 0.005 + "\t0\t0\t0" + "\n");
			out.write(25002 + "\t" + 0.5 + "\t" + 0 + "\t" + 0.005 + "\t0\t0\t0" + "\n");
			out.write(25004+ "\t" + 0.5 + "\t" + 0.5 + "\t" + 0.005 + "\t0\t0\t0" + "\n");
			for(int i=0; i<p.length; i++){
				out.write(p[i].ID + "\t" + p[i].x + "\t" + p[i].y + "\t" + p[i].r + "\t" + (p[i].r<0.05?"255":"0") + "\t" + (p[i].r<0.05?"255":"255") + "\t" + (p[i].r<0.05?"255":"255")  + "\n");
			}
			out.close();
		}catch (IOException e) {
		    e.printStackTrace();
		}
	}
	
}
