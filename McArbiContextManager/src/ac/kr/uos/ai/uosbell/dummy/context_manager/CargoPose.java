package ac.kr.uos.ai.uosbell.dummy.context_manager;


public class CargoPose {
	private int vertex1;
	private int vertex2;
	private String robotID;
	private String rackID;
	
	
	
	public CargoPose(int vertex1, int vertex2, String robotID, String rackID) {
		super();
		this.vertex1 = vertex1;
		this.vertex2 = vertex2;
		this.robotID = robotID;
		this.rackID = rackID;
	}
	
	public int getVertex1() {
		return vertex1;
	}
	public void setVertex1(int vertex1) {
		this.vertex1 = vertex1;
	}
	public int getVertex2() {
		return vertex2;
	}
	public void setVertex2(int vertex2) {
		this.vertex2 = vertex2;
	}
	public String getRobotID() {
		return robotID;
	}
	public void setRobotID(String robotID) {
		this.robotID = robotID;
	}
	public String getRackID() {
		return rackID;
	}
	public void setRackID(String rackID) {
		this.rackID = rackID;
	}
	
	
}
