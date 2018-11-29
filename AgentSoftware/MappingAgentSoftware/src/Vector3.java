
public class Vector3 {
	private double x;
	private double y;
	private double z;
	
	public Vector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void setVector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}
	
	public double getDistance(Vector3 otherVector) {
		double xDistance = Math.pow((x - otherVector.x), 2);
		double yDistance = Math.pow((y - otherVector.y), 2);
		double zDistance = Math.pow((z - otherVector.z), 2);
		
		return Math.sqrt(xDistance + yDistance + zDistance);
	}
	
	public Vector3 getRelativePosition(Vector3 referantVector) {
		return new Vector3(
			x - referantVector.x,
			y - referantVector.y,
			z - referantVector.z
		);
	}
	
	public String toString() {
		return "(" + getX() + "," + getY() + "," + getZ() + ")";
 	}
}
