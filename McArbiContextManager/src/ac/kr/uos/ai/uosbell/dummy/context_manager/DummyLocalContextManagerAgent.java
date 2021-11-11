package ac.kr.uos.ai.uosbell.dummy.context_manager;

import java.awt.Point;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ac.kr.uos.ai.uosbell.dummy.context_manager.local.DummyLocalContextManagerSocketCommunicator;
import ac.kr.uos.ai.uosbell.dummy.context_manager.model.CargoPose;
import ac.kr.uos.ai.uosbell.dummy.context_manager.model.RackPose;
import kr.ac.uos.ai.arbi.Broker;
import kr.ac.uos.ai.arbi.ltm.DataSource;
import kr.ac.uos.ai.arbi.model.GeneralizedList;
import kr.ac.uos.ai.arbi.model.parser.GLParser;
import kr.ac.uos.ai.arbi.model.parser.ParseException;

public class DummyLocalContextManagerAgent extends DummyContextManagerAgent {

	DataSource ds;

	public DataSource getDataSource() {
		return ds;
	}

	public void setDataSource(DataSource ds) {
		this.ds = ds;
	}

	private boolean isStation18Storing = false;
	private UUID personCallID = null;

	private HashMap<String, CargoPose> cargo;
	private HashMap<String, RackPose> rack;
	private HashMap<String, Integer> stationVertexMap;
	private HashMap<Integer, String> reverseStationVertexMap;

	public DummyLocalContextManagerAgent(String brokerName, String brokerURL) {
		cargo = new HashMap<String, CargoPose>();
		rack = new HashMap<String, RackPose>();
		stationVertexMap = new HashMap<String, Integer>(Stream
				.of(new Object[][] { { "station1", 1 }, { "station2", 2 }, { "station3", 3 }, { "station4", 4 },
						{ "station5", 5 }, { "station6", 6 }, { "station7", 7 }, { "station8", 8 }, { "station9", 9 },
						{ "station10", 10 }, { "station11", 11 }, { "station12", 12 }, { "station13", 13 },
						{ "station14", 14 }, { "station15", 15 }, { "station16", 16 }, { "station17", 17 },
						{ "station18", 18 }, { "station19", 19 }, { "station20", 20 }, { "station21", 21 },
						{ "station22", 22 }, { "station23", 23 } })
				.collect(Collectors.toMap(data -> (String) data[0], data -> (Integer) data[1])));
		reverseStationVertexMap = new HashMap(stationVertexMap.entrySet().stream().collect(Collectors.toMap(HashMap.Entry::getValue, HashMap.Entry::getKey)));
		
		ds = new DataSource() {
			@Override
			public void onNotify(String content) {
//				System.out.println("ONNOTIFY on " + brokerName + "/contextManager //" + content);
				GLParser parser = new GLParser();
				GeneralizedList contentGL = null;
				try {
					contentGL = parser.parseGL(content);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				String name = contentGL.getName();
				switch (name) {
				case "MosPersonCall": {
					int firstValue = contentGL.getExpression(0).asValue().intValue();
					int secondValue = contentGL.getExpression(1).asValue().intValue();
					personCallID = UUID.randomUUID();
					System.out.println("We've got MosPersonCall!");
					if (firstValue == 0 && secondValue == 0) {
						if (!isStation18Storing) {
							ds.assertFact(
									"(context (PersonCall \"" + personCallID + "\" \"station18\" \"PrepareStoring\"))");
							isStation18Storing = true;
						} else {
							ds.assertFact(
									"(context (PersonCall \"" + personCallID + "\" \"station18\" \"Storing\"))");
							isStation18Storing = false;
						}
					} else if (firstValue == 0 && secondValue == 1) {
						ds.assertFact(
								"(context (PersonCall \"" + personCallID + "\" \"station19\" \"Storing\"))");
					} else if (firstValue == 1 && secondValue == 2) {
						ds.assertFact(
								"(context (PersonCall \"" + personCallID + "\" \"station22\" \"PrepareUnstoring\"))");
					} else if (firstValue == 1 && secondValue == 3) {
						ds.assertFact(
								"(context (PersonCall \"" + personCallID + "\" \"station22\" \"Unstoring\"))");
					}
					break;
				}
				}
				
			}
		};
		ds.connect(brokerURL, "ds://www.arbi.com/" + brokerName + "/ContextManager", Broker.ZEROMQ);

		ds.subscribe("(rule (fact (Collidable $collidableList)) --> (notify (Collidable $collidableList)))");
		ds.subscribe("(rule (fact (context $context)) --> (notify (context $context)))");
		ds.subscribe("(rule (fact (MosPersonCall $a $b)) --> (notify (MosPersonCall $a $b)))");
		
		String[] ids = new String[] {
				"AMR_LIFT1", "AMR_LIFT2", "AMR_TOW1", "AMR_TOW2"
		};
		
		for (int i = 0; i < ids.length; i++) {
			String id = ids[i];
			System.out.println("ID SET TO " + id);
			ds.assertFact("(context (RobotVelocity \""+id+"\" 0))");
			System.out.println("RoboVelocity Asserted");
			sleep();
			ds.assertFact("(context (BatteryRemain \""+id+"\" 0))");
			System.out.println("BatteryRemain Asserted");
			sleep();
			ds.assertFact("(context (OnRobotTaskStatus \""+id+"\" \"dummy\"))");
			System.out.println("OnRobotTaskStatus Asserted");
			sleep();
			ds.assertFact("(context (RobotAt \""+id+"\" 0 0))");
			System.out.println("RoboVelocity Asserted");
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
	public void onNotify(String sender, String notification) {
//		System.out.println("LOCAL" + notification);
		GLParser parser = new GLParser();

		GeneralizedList gl = null;
		try {
			gl = parser.parseGL(notification);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String name = gl.getName();
		if (name.contains("Pose")) {
			GeneralizedList vertexGL = gl.getExpression(1).asGeneralizedList();
			int vertex1 = vertexGL.getExpression(0).asValue().intValue();
			int vertex2 = vertexGL.getExpression(1).asValue().intValue();

			GeneralizedList onGL = gl.getExpression(2).asGeneralizedList();
			String robotID = onGL.getExpression(0).asValue().stringValue();

			if (name.contentEquals("CargoPose")) {
				String cargoID = gl.getExpression(0).asValue().stringValue();
				String rackID = onGL.getExpression(1).asValue().stringValue();
				String stationName = ""; 
				if (vertex1 == vertex2) {
				stationName = reverseStationVertexMap.get(vertex1);
				}
				if (cargo.containsKey(cargoID)) {
					CargoPose cp = cargo.get(cargoID);
					cp.setVertex1(vertex1);
					cp.setVertex2(vertex2);
					cp.setRobotID(robotID);
					cp.setRackID(rackID);
					cp.setStation(stationName);
				} else {
					cargo.put(cargoID, new CargoPose(vertex1, vertex2, robotID, rackID, stationName));
				}

			} else if (name.contentEquals("RackPose")) {
				String rackID = gl.getExpression(0).asValue().stringValue();
				String cargoID = onGL.getExpression(1).asValue().stringValue();
				String stationName = ""; 
				if (vertex1 == vertex2) {
					stationName = reverseStationVertexMap.get(vertex1);
					}
				if (rack.containsKey(rackID)) {
					RackPose rp = rack.get(rackID);
					rp.setVertex1(vertex1);
					rp.setVertex2(vertex2);
					rp.setRobotID(robotID);
					rp.setCargoID(rackID);
					rp.setStation(stationName);
				} else {
					rack.put(rackID, new RackPose(vertex1, vertex2, robotID, cargoID, stationName));
				}
			}
			

		}

		super.onNotify(sender, notification);
	}

	@Override
	public void onData(String sender, String data) {
		GLParser parser = new GLParser();
		GeneralizedList gl = null;
		try {
			gl = parser.parseGL(data);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String name = gl.getName();
		switch (name) {

		case "initialStationInfo":
		case "initialCargoInfo":
		case "initialRackInfo":
		default:
			System.out.println("I got " + name + " data and I have no idea what I have to do");
			break;
		}

	}

	private String responseString = "";
	private String queryObject = "";
	boolean emptyStationFlag = false;
	
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
		if (name.contentEquals("context")) {
			GeneralizedList contextGL = queryGL.getExpression(0).asGeneralizedList();
			String contextName = contextGL.getName();
			System.out.println("ContextGL = " + contextGL.toString());
			if (contextName.toLowerCase().contentEquals("navigationvertex")) {
				int station = contextGL.getExpression(0).asValue().intValue(); 
				switch (station) {
					case 20 :
						return "(context (NavigationVertex 20 239))";
					case 21 :
						return "(context (NavigationVertex 21 240))";
					case 22 :
						return "(context (NavigationVertex 22 228))";
					case 23 :
						return "(context (NavigationVertex 23 229))";
					case 103 :
						return "(context (NavigationVertex 103 203))";
					case 104 :
						return "(context (NavigationVertex 104 204))";
					case 101 :
						return "(context (NavigationVertex 101 201))";
					case 102 :
						return "(context (NavigationVertex 102 202))";
					case 1 :
						return "(context (NavigationVertex 1 206))";
					case 2 :
						return "(context (NavigationVertex 2 207))";
					case 3 :
						return "(context (NavigationVertex 3 208))";
					case 4 :
						return "(context (NavigationVertex 4 209))";
					case 5 :
						return "(context (NavigationVertex 5 210))";
					case 6 :
						return "(context (NavigationVertex 6 211))";
					case 11 :
						return "(context (NavigationVertex 11 218))";
					case 12 :
						return "(context (NavigationVertex 12 219))";
					case 13 :
						return "(context (NavigationVertex 13 220))";
					case 14 :
						return "(context (NavigationVertex 14 221))";
					case 15 :
						return "(context (NavigationVertex 15 222))";
					case 18 :
						return "(context (NavigationVertex 18 225))";
					case 19 :
						return "(context (NavigationVertex 19 226))";
				}
			}
			if (contextName.contentEquals("IdleLiftRack")) {
				return "(context (IdleLiftRack \"RACK_LIFT0\"))";
			} 
			else if (contextName.contentEquals("IdleMovingRack")) {
				return "(context (IdleMovingRack ))";
			}
			else if (contextName.contentEquals("OnStation")) {
				
				if (contextGL.getExpression(0).isVariable()) {
					queryObject = contextGL.getExpression(1).asValue().stringValue();
					if (stationVertexMap.containsKey(queryObject)) {
						rack.forEach((k, v) -> {
							if (v.getVertex1() == v.getVertex2()) {
								if (stationVertexMap.get(queryObject).equals(v.getVertex1())) {
									responseString = queryObject;
								}
							}
						});
						//TODO tmp code
						return "(context (OnStation \"rack001\" \"station20\"))";
//						return "(context (OnStation \"" + responseString + "\" \"" + queryObject + "\"))";
					}
					
					queryObject = contextGL.getExpression(1).asValue().stringValue();
					if (queryObject.contentEquals("station1")) {
						return "(context (OnStation \"RACK_LIFT1\" \"station1\"))";
					} else if (queryObject.contentEquals("station18")) {
						return "(context (OnStation \"RACK_LIFT1\" \"station18\"))";
					} else if (queryObject.contentEquals("station23")) {
						return "(context (OnStation \"RACK_LIFT1\" \"station1\"))";
					}
				} else if (contextGL.getExpression(1).isVariable()) {
					String queryObject = contextGL.getExpression(0).asValue().stringValue();
					if (rack.containsKey(queryObject)) {
						RackPose rp = rack.get(queryObject);
						if (rp.getVertex1() == rp.getVertex2()) {
							if (reverseStationVertexMap.containsKey(rp.getVertex1())) {
								String station = reverseStationVertexMap.get(rp.getVertex1());
								return "(context (OnStation \"" + queryObject + "\" \"" + station + "\"))";
							}
						}
					}
					return "(context (OnStation \"RACK_LIFT5\" \"station1\"))";
				}
			} else if (contextName.contentEquals("OnRack")) {
				return "(context (OnRack \"cargo001\" \"RACK_LIFT1\"))";
			} else if (contextName.contentEquals("EmptyStation")) {
				if (contextGL.getExpression(0).isValue()) {
					return "(true)";
				} else {
					emptyStationFlag = !emptyStationFlag;
					if (emptyStationFlag) {
						return "(context (EmptyStation \"station1\"))";
					} else {
						return "(context (EmptyStation \"station13\"))";
					}
				}
			} else if (contextName.contentEquals("RackType")) {
				return "(context (RackType \"RACK_LIFT1\" \"lift\"))";
			} else if (contextName.contentEquals("StationAvailability")) {
				return "(context (OnStation \"rack010\" \"station20\"))";
			} 
		} else if (name.contentEquals("StationVertex")) {
			String stationName = queryGL.getExpression(0).asValue().stringValue();
			try {
				return "(StationVertex \"" + stationName + "\" " + stationVertexMap.get(stationName) + ")";
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "(Error)";
	}

	public static void main(String[] args) {
		try {
			String ipr = InetAddress.getLocalHost().getHostAddress();
			String ip = "127.0.0.1";
			String brokerURL = "tcp://"+ip+":61313";
			String brokerName = "Local";
			
			DummyLocalContextManagerAgent agent = new DummyLocalContextManagerAgent(brokerName, brokerURL);
			agent.execute(brokerName, brokerURL, agent);

			Thread t = new Thread(new DummyLocalContextManagerSocketCommunicator(agent));
			t.start();

			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
