package ac.kr.uos.ai.uosbell.dummy.context_manager.model;


public class RackPose {
	private int vertex1;
	private int vertex2;
	private String robotID;
	private String cargoID;
	private String station;
	private boolean preserved;
	
	public RackPose(int vertex1, int vertex2, String robotID, String cargoID, String station) {
		super();
		this.vertex1 = vertex1;
		this.vertex2 = vertex2;
		this.robotID = robotID;
		this.cargoID = cargoID;
		this.station = station;
		this.preserved = false;
	}
	
	public boolean isPreserved() {
		return preserved;
	}
	public void setPreserved(boolean flag) {
		this.preserved = flag;
	}
	
	public int getVertex1() {
		return vertex1;
	}
	public String getStation() {
		return station;
	}

	public void setStation(String station) {
		this.station = station;
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
	public String getCargoID() {
		return cargoID;
	}
	public void setCargoID(String cargoID) {
		this.cargoID = cargoID;
	}
	
}
