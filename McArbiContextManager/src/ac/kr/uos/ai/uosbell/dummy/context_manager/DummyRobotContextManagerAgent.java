package ac.kr.uos.ai.uosbell.dummy.context_manager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.management.StringValueExp;

import ac.kr.uos.ai.uosbell.dummy.context_manager.model.InOutRule;
import ac.kr.uos.ai.uosbell.dummy.context_manager.model.RobotData;
import kr.ac.uos.ai.arbi.Broker;
import kr.ac.uos.ai.arbi.agent.ArbiAgentExecutor;
import kr.ac.uos.ai.arbi.ltm.DataSource;
import kr.ac.uos.ai.arbi.model.Expression;
import kr.ac.uos.ai.arbi.model.GLFactory;
import kr.ac.uos.ai.arbi.model.GeneralizedList;
import kr.ac.uos.ai.arbi.model.parser.GLParser;
import kr.ac.uos.ai.arbi.model.parser.ParseException;

public class DummyRobotContextManagerAgent extends DummyContextManagerAgent {
	DataSource ds;

	RobotData rd = new RobotData();
	String robotID = "";
	Map<String, InOutRule> rules = Stream.of(new Object[][] {
		{"1", new InOutRule(1, "ReqPreciseMove", 1, "ReqStraightBackMove", 206)},
		{"2", new InOutRule(2, "ReqPreciseMove", 2, "ReqStraightBackMove", 207)},
		{"3", new InOutRule(3, "ReqPreciseMove", 3, "ReqStraightBackMove", 208)},
		{"4", new InOutRule(4, "ReqPreciseMove", 4, "ReqStraightBackMove", 209)},
		{"5", new InOutRule(5, "ReqPreciseMove", 5, "ReqStraightBackMove", 210)},
		{"6", new InOutRule(6, "ReqPreciseMove", 6, "ReqStraightBackMove", 211)},
		{"11", new InOutRule(11, "ReqPreciseMove", 11, "ReqStraightBackMove", 218)},
		{"12", new InOutRule(12, "ReqPreciseMove", 12, "ReqStraightBackMove", 219)},
		{"13", new InOutRule(13, "ReqPreciseMove", 13, "ReqStraightBackMove", 220)},
		{"14", new InOutRule(14, "ReqPreciseMove", 14, "ReqStraightBackMove", 221)},
		{"15", new InOutRule(15, "ReqPreciseMove", 15, "ReqStraightBackMove", 222)},
		{"18", new InOutRule(18, "ReqGuideMove", 18, "ReqStraightBackMove", 225)},
		{"19", new InOutRule(19, "ReqGuideMove", 19, "ReqStraightBackMove", 226)},
		{"20", new InOutRule(20, "ReqGuideMove", 20, "ReqGuideMove", 239)},
		{"21", new InOutRule(21, "ReqGuideMove", 21, "ReqGuideMove", 240)},
		{"22", new InOutRule(22, "ReqGuideMove", 22, "ReqStraightBackMove", 228)},
		{"23", new InOutRule(23, "ReqGuideMove", 23, "ReqGuideMove", 229)},
		{"101", new InOutRule(101, "ReqGuideMove", 101, "ReqGuideMove", 201)},
		{"102", new InOutRule(102, "ReqGuideMove", 102, "ReqGuideMove", 202)},
		{"103", new InOutRule(103, "ReqGuideMove", 103, "ReqGuideMove", 203)},
		{"104", new InOutRule(104, "ReqGuideMove", 104, "ReqGuideMove", 204)},
	}).collect(Collectors.toMap(data -> (String) data[0],  data -> (InOutRule) data[1]));

	public DummyRobotContextManagerAgent() {
		System.out.println("Debug Constructor Only! do not execute without purpose.");
	}
	
	public DummyRobotContextManagerAgent(String brokerName, String brokerURL) {
		if (brokerName.contains("Lift1")) {
			robotID = "AMR_LIFT1";
		} else if (brokerName.contains("Lift2")) {
			robotID = "AMR_LIFT2";
		} else if (brokerName.contains("Tow1")) {
			robotID = "AMR_TOW1";
		} else if (brokerName.contains("Tow2")) {
			robotID = "AMR_TOW2";
		}
		
		ds = new DataSource() {
			@Override
			public void onNotify(String content) {
//				System.out.println("ONNOTIFY on " + brokerName + "/ContextManager //" + content);
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
//					System.out.println("Status Updated " + rd.getStatus());
				} else if (notifiedGLName.contentEquals("RobotSpeed")) {
					rd.setSpeed(notifiedGL.getExpression(1).asValue().intValue());
					this.updateFact("(update (context (RobotVelocity \""+robotID+"\" $v)) (context (RobotVelocity \""+robotID+"\" " + rd.getSpeed() +")))");
//					System.out.println("Speed Updated" + rd.getSpeed());
				} else if (notifiedGLName.contentEquals("RobotBattery")) {
					rd.setBattery(notifiedGL.getExpression(1).asValue().intValue());
					this.updateFact("(update (context (BatteryRemain \""+robotID+"\" $v)) (context (BatteryRemain \""+robotID+"\" " + rd.getBattery() +")))");
//					System.out.println("battery Updated" + rd.getBattery());
				}
				
			}
		};
		ds.connect(brokerURL, "ds://www.arbi.com/" + brokerName + "/ContextManager", Broker.ZEROMQ);
		ds.subscribe("(rule (fact (CurrentRobotPosition $robotID $x $y)) --> (notify (CurrentRobotPosition $robotID $x $y)))");
		ds.subscribe("(rule (fact (RobotLoading $robotID $loadStatus)) --> (notify (RobotLoading $robotID $loadStatus)))");
		ds.subscribe("(rule (fact (RobotStatus $robotID $status)) --> (notify (RobotStatus $robotID $status)))");
		ds.subscribe("(rule (fact (RobotSpeed $robotID $speed)) --> (notify (RobotSpeed $robotID $speed)))");
		ds.subscribe("(rule (fact (RobotBattery $robotID $battery)) --> (notify (RobotBattery $robotID $battery)))");
		
		ds.assertFact("(context (RobotVelocity \""+robotID+"\" 0))");
//		System.out.println("velocity sent");
		sleep();
		ds.assertFact("(context (BatteryRemain \""+robotID+"\" 0))");
//		System.out.println("battery sent");
		sleep();
		ds.assertFact("(context (OnRobotTaskStatus \""+robotID+"\" \"dummy\"))");
//		System.out.println("robottaskstatus sent");
		sleep();
		ds.assertFact("(context (RobotAt \""+robotID+"\" 0 0))");
//		System.out.println("robotat sent");
		
		if(brokerName.contains("Lift")) {
			ds.subscribe("(rule (fact (context (OnAgentTaskStatus $agentID $goal $status))) --> (notify (context (OnAgentTaskStatus $agentID $goal $status))))");
			ds.subscribe("(rule (fact (context (OnRobotTaskStatus $robotID $status))) --> (notify (context (OnRobotTaskStatus $robotID $status))))");			
		} else if (brokerName.contains("Tow")) {
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
		} else if (name.contentEquals("StationMoveType")) {
			return getStationMoveType(queryGL);
			
		}
		return null;
	}

	private String getStationMoveType(GeneralizedList queryGL) {
		String station = queryGL.getExpression(0).asValue().stringValue();
		String direction = queryGL.getExpression(1).asValue().stringValue();
		InOutRule rule = rules.get(station);
		String resultString = "(StationMoveType \"" + station + "\" \""+direction+"\" ";
		if (direction.contentEquals("in")) {
			int directionCode = 0;
			if (station.contentEquals("101") || station.contentEquals("102"))
				directionCode = 1;
			resultString += "(" + rule.getInMoveType() + " " + rule.getInStation();
			if (rule.getInMoveType().contentEquals("ReqGuideMove")) {
				resultString += " " + directionCode;
			}
			resultString += ")";
			
		} else if (direction.contentEquals("out")) {
			int directionCode = 1;
			if (station.contentEquals("101") || station.contentEquals("102"))
				directionCode = 0;
			resultString += "(" + rule.getOutMoveType() + " " + rule.getOutStation();
			if (rule.getOutMoveType().contentEquals("ReqGuideMove")) {
				resultString += " " + directionCode;
			}
			resultString += ")";
		}
		resultString += ")";
		return resultString;
	}

	private int stationVertex(String vertexName) {
		switch (vertexName) {
		case "station1":
			return 1;
		case "station18":
			return 18;
		case "station19":
			return 19;
		case "station2":
			return 2;
		case "station3":
			return 3;
		case "station4":
			return 4;
		case "station5":
			return 5;
		case "station6":
			return 6;
		case "station11":
			return 11;
		case "station12":
			return 12;
		case "station13":
			return 13;
		case "station14":
			return 14;
		case "station15":
			return 15;
		case "station20":
			return 20;
		case "station21":
			return 21;
		case "station23":
			return 23;
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
		case "station19":
			return 226;
		case "station2":
			return 207;
		case "station3":
			return 208;
		case "station4":
			return 209;
		case "station5":
			return 210;
		case "station6":
			return 211;
		case "station11":
			return 218;
		case "station12":
			return 219;
		case "station13":
			return 220;
		case "station14":
			return 221;
		case "station15":
			return 222;
		case "station20":
			return 239;
		case "station21":
			return 240;
		case "station23":
			return 229;
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
//		System.out.println("ROBOT//" + notification);
		GLParser parser = new GLParser();
		GeneralizedList notificationGL = null;
		try {
			notificationGL = parser.parseGL(notification);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String name = notificationGL.getName();
//		System.out.println("onNotify Called");
//		System.out.println(name);
		switch (name) {
		case "RobotAt": {
			int x = notificationGL.getExpression(1).asValue().intValue();
			int y = notificationGL.getExpression(2).asValue().intValue();
			rd.setPosX(x);
			rd.setPosY(y);
			
			ds.updateFact("(update (context (RobotAt \""+robotID+"\" $v1 $v2)) (context (RobotAt \""+robotID+"\" "+(int)rd.getPosX()+" "+(int)rd.getPosY()+")))");
//			System.out.println("(update (context (RobotAt \"" + robotID + "\" $v1 $v2)) (context (RobotAt \""+robotID+"\" "+(int)rd.getPosX()+" "+(int)rd.getPosY()+")))");

			break;
		}
		case "RackAt": {
			String stationName = notificationGL.getExpression(0).asValue().stringValue();
			float x = notificationGL.getExpression(1).asValue().floatValue();
			float y = notificationGL.getExpression(1).asValue().floatValue();
			String before = "(RackAt \"" + stationName + "\" $v1 $v2) ";
			String after = "(RackAt \"" + stationName + "\" " + x + " " + y + ")";
			ds.updateFact("(update (context " + before + ") (context " + after + "))");
//			System.out.println("(update (context " + before + ") (context " + after + "))");
			break;
		}
		case "OnStation": {
			ds.updateFact("(update (context (RackSeperatedAt $rack $stationID) (context (RackSeperatedAt \"rack001\" \"station1\")))");
//			System.out.println("(update (context (RackSeperatedAt $rack $stationID) (context (RackSeperatedAt \"rack001\" \"station1\")))");
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
//		DummyRobotContextManagerAgent a = new DummyRobotContextManagerAgent();
//		try {
//			String result = a.getStationMoveType(GLFactory.newGLFromGLString("(StationMoveType 18 \"in\" $result)"));
//			System.out.println(result);
//		} catch (ParseException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
		try {
			String ipr = InetAddress.getLocalHost().getHostAddress();
			String ip = "127.0.0.1";
			String brokerURL = "tcp://" + ip + ":61316";
			String brokerName = System.getenv("AGENT");
			String ContextManagerURI = "agent://www.arbi.com/" + brokerName + "/ContextManager";
			
			Thread l1 = new Thread(new Runnable() {
				@Override
				public void run() {
					DummyRobotContextManagerAgent a = new DummyRobotContextManagerAgent("Lift1", "tcp://"+ip+":61116");
					a.execute("Lift1", "tcp://"+ip+":61116", a);
				}
			});
			
			
			l1.start();
			
			Thread l2 = new Thread(new Runnable() {
				@Override
				public void run() {
					DummyRobotContextManagerAgent a = new DummyRobotContextManagerAgent("Lift2", "tcp://"+ip+":61115");
					a.execute("Lift2", "tcp://"+ip+":61115", a);
				}
			});
			
			
			l2.start();
			
			Thread t1 = new Thread(new Runnable() {
				@Override
				public void run() {
					DummyRobotContextManagerAgent a = new DummyRobotContextManagerAgent("Tow1", "tcp://"+ip+":61114");
					a.execute("Tow1", "tcp://"+ip+":61114", a);
				}
			});
			
			t1.start();
			
			Thread t2 = new Thread(new Runnable() {
				@Override
				public void run() {
					DummyRobotContextManagerAgent a = new DummyRobotContextManagerAgent("Tow2", "tcp://"+ip+":61412");
					a.execute("Tow2", "tcp://"+ip+":61412", a);
				}
			});
			t2.start();
			
			
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
