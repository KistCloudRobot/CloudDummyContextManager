package ac.kr.uos.ai.uosbell.dummy.context_manager.model;

public class InOutRule {
	private int station;
	private String inMoveType;
	private int inStation;
	private String outMoveType;
	private int outStation;

	public int getStation() {
		return station;
	}

	public void setStation(int station) {
		this.station = station;
	}

	public String getInMoveType() {
		return inMoveType;
	}

	public void setInMoveType(String inMoveType) {
		this.inMoveType = inMoveType;
	}

	public int getInStation() {
		return inStation;
	}

	public void setInStation(int inStation) {
		this.inStation = inStation;
	}

	public String getOutMoveType() {
		return outMoveType;
	}

	public void setOutMoveType(String outMoveType) {
		this.outMoveType = outMoveType;
	}

	public int getOutStation() {
		return outStation;
	}

	public void setOutStation(int outStation) {
		this.outStation = outStation;
	}

	public InOutRule(int station, String inMoveType, int inStation, String outMoveType, int outStation) {
		super();
		this.station = station;
		this.inMoveType = inMoveType;
		this.inStation = inStation;
		this.outMoveType = outMoveType;
		this.outStation = outStation;
	}

}
