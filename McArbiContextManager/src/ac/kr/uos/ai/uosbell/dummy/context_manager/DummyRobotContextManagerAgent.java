package ac.kr.uos.ai.uosbell.dummy.context_manager;

import kr.ac.uos.ai.arbi.Broker;
import kr.ac.uos.ai.arbi.agent.ArbiAgentExecutor;
import kr.ac.uos.ai.arbi.ltm.DataSource;
import kr.ac.uos.ai.arbi.model.GeneralizedList;
import kr.ac.uos.ai.arbi.model.parser.GLParser;
import kr.ac.uos.ai.arbi.model.parser.ParseException;

public class DummyRobotContextManagerAgent extends DummyContextManagerAgent {
	DataSource ds;

	RobotData rd = new RobotData();
	String robotID = "";

	public DummyRobotContextManagerAgent(String uri, String serverURI) {
		super(uri, Configuration.ROBOT_SERVER_URI + serverURI);
		if (uri.contains("Lift1")) {
			robotID = "AMR_LIFT1";
		} else if (uri.contains("Lift2")) {
			robotID = "AMR_LIFT2";
		} else if (uri.contains("Tow1")) {
			robotID = "AMR_TOW1";
		} else if (uri.contains("Tow2")) {
			robotID = "AMR_TOW2";
		}
		ds = new DataSource() {
			@Override
			public void onNotify(String content) {
				System.out.println("ONNOTIFY on " + uri + "//" + content);
				GLParser parser = new GLParser();
				GeneralizedList notifiedGL = null;
				try {
					notifiedGL = parser.parseGL(content);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String notifiedGLName = notifiedGL.getName();
				
				
				sleep();
				
				if(notifiedGLName.contentEquals("CurrentRobotPosition")) {
					rd.setPosX(notifiedGL.getExpression(1).asValue().intValue());
					rd.setPosY(notifiedGL.getExpression(2).asValue().intValue());
				} else if (notifiedGLName.contentEquals("RobotLoading")) {
					rd.setLoading(notifiedGL.getExpression(1).asValue().stringValue());
				} else if (notifiedGLName.contentEquals("RobotStatus")) {
					rd.setStatus(notifiedGL.getExpression(1).asValue().stringValue());
					this.updateFact("(update (context (OnRobotTaskStatus \""+robotID+"\" $status)) (context (OnRobotTaskStatus \""+robotID+"\" \"" + rd.getStatus() +"\")))");
					System.out.println("Status Updated " + rd.getStatus());
				} else if (notifiedGLName.contentEquals("RobotSpeed")) {
					rd.setSpeed(notifiedGL.getExpression(1).asValue().intValue());
					this.updateFact("(update (context (RobotVelocity \""+robotID+"\" $v)) (context (RobotVelocity \""+robotID+"\" " + rd.getSpeed() +")))");
					System.out.println("Speed Updated" + rd.getSpeed());
				} else if (notifiedGLName.contentEquals("RobotBattery")) {
					rd.setBattery(notifiedGL.getExpression(1).asValue().intValue());
					this.updateFact("(update (context (BatteryRemain \""+robotID+"\" $v)) (context (BatteryRemain \""+robotID+"\" " + rd.getBattery() +")))");
					System.out.println("battery Updated" + rd.getBattery());
				}
				
			}
		};
		ds.connect(Configuration.ROBOT_SERVER_URI + serverURI, "ds://www.arbi.com/" + uri, Broker.ZEROMQ);
		ds.subscribe("(rule (fact (CurrentRobotPosition $robotID $x $y)) --> (notify (CurrentRobotPosition $robotID $x $y)))");
		ds.subscribe("(rule (fact (RobotLoading $robotID $loadStatus)) --> (notify (RobotLoading $robotID $loadStatus)))");
		ds.subscribe("(rule (fact (RobotStatus $robotID $status)) --> (notify (RobotStatus $robotID $status)))");
		ds.subscribe("(rule (fact (RobotSpeed $robotID $speed)) --> (notify (RobotSpeed $robotID $speed)))");
		ds.subscribe("(rule (fact (RobotBattery $robotID $battery)) --> (notify (RobotBattery $robotID $battery)))");
		
		ds.assertFact("(context (RobotVelocity \""+robotID+"\" 0))");
		System.out.println("velocity sent");
		sleep();
		ds.assertFact("(context (BatteryRemain \""+robotID+"\" 0))");
		System.out.println("battery sent");
		sleep();
		ds.assertFact("(context (OnRobotTaskStatus \""+robotID+"\" \"dummy\"))");
		System.out.println("robottaskstatus sent");
		sleep();
		ds.assertFact("(context (RobotAt \""+robotID+"\" 0 0))");
		System.out.println("robotat sent");
		
		
		if(uri.contains("LIFT1")) {
			ds.subscribe("(rule (fact (context (OnAgentTaskStatus $agentID $goal $status))) --> (notify (context (OnAgentTaskStatus $agentID $goal $status))))");
			ds.subscribe("(rule (fact (context (OnRobotTaskStatus $robotID $status))) --> (notify (context (OnRobotTaskStatus $robotID $status))))");			
		} else if (uri.contains("TOW")) {
			ds.subscribe("(rule (fact (DoorStatus $status)) --> (notify (DoorStatus $status)))");
		}
	}
	
	private void sleep() {
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String onQuery(String sender, String query) {
		GLParser parser = new GLParser();
		GeneralizedList queryGL = null;
		try {
			queryGL = parser.parseGL(query);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String name = queryGL.getName();
		if (name.contentEquals("PreparationVertex")) {
			String vertexName = queryGL.getExpression(0).asValue().stringValue();
			return "(PreparationVertex \"" + vertexName + "\" " + preparationVertex(vertexName) + ")";
		} else if (name.contentEquals("StationVertex")) {
			String vertexName = queryGL.getExpression(0).asValue().stringValue();
			return "(StationVertex \"" + vertexName + "\" " + stationVertex(vertexName) + ")";
		}
		return null;
	}

	private int stationVertex(String vertexName) {
		switch (vertexName) {
		case "station1":
			return 1;
		case "station18":
			return 18;
		case "station22":
			return 22;
		}
		return -1;
	}

	private int preparationVertex(String vertexName) {
		switch (vertexName) {
		case "station1":
			return 206;
		case "station18":
			return 225;
		case "station22":
			return 228;
		}
		return -1;
	}
	
	@Override
	public void onData(String sender, String data) {
		System.out.println("ROBOT ONDATA" + data);
	}

	@Override
	public void onNotify(String sender, String notification) {
		System.out.println("ROBOT//" + notification);
		GLParser parser = new GLParser();
		GeneralizedList notificationGL = null;
		try {
			notificationGL = parser.parseGL(notification);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String name = notificationGL.getName();
		System.out.println("onNotify Called");
		System.out.println(name);
		switch (name) {
		case "RobotAt": {
			int x = notificationGL.getExpression(1).asValue().intValue();
			int y = notificationGL.getExpression(2).asValue().intValue();
			rd.setPosX(x);
			rd.setPosY(y);
			
			ds.updateFact("(update (context (RobotAt \""+robotID+"\" $v1 $v2)) (context (RobotAt \""+robotID+"\" "+(int)rd.getPosX()+" "+(int)rd.getPosY()+")))");
			System.out.println("(update (context (RobotAt \"" + robotID + "\" $v1 $v2)) (context (RobotAt \""+robotID+"\" "+(int)rd.getPosX()+" "+(int)rd.getPosY()+")))");

			break;
		}
		case "RackAt": {
			String stationName = notificationGL.getExpression(0).asValue().stringValue();
			float x = notificationGL.getExpression(1).asValue().floatValue();
			float y = notificationGL.getExpression(1).asValue().floatValue();
			String before = "(RackAt \"" + stationName + "\" $v1 $v2) ";
			String after = "(RackAt \"" + stationName + "\" " + x + " " + y + ")";
			ds.updateFact("(update (context " + before + ") (context " + after + "))");
			System.out.println("(update (context " + before + ") (context " + after + "))");
			break;
		}
		case "OnStation": {
			ds.updateFact("(update (context (RackSeperatedAt $rack $stationID) (context (RackSeperatedAt \"rack001\" \"station1\")))");
			System.out.println("(update (context (RackSeperatedAt $rack $stationID) (context (RackSeperatedAt \"rack001\" \"station1\")))");
			break;
		}
		case "context": {
			System.out.println("door is "
					+ notificationGL.getExpression(0).asGeneralizedList().getExpression(0).asValue().stringValue());
			break;
		}
		}
		
		super.onNotify(sender, notification);

	}
	
	public static void main(String[] args) {
		DummyRobotContextManagerAgent rAgent = new DummyRobotContextManagerAgent("Lift2/ContextManager", ":61115");
	}
}
