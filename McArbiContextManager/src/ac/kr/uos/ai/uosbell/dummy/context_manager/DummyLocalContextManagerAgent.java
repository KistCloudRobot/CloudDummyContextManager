package ac.kr.uos.ai.uosbell.dummy.context_manager;

import java.awt.Point;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import kr.ac.uos.ai.arbi.Broker;
import kr.ac.uos.ai.arbi.agent.ArbiAgentExecutor;
import kr.ac.uos.ai.arbi.ltm.DataSource;
import kr.ac.uos.ai.arbi.model.GeneralizedList;
import kr.ac.uos.ai.arbi.model.parser.GLParser;
import kr.ac.uos.ai.arbi.model.parser.ParseException;

public class DummyLocalContextManagerAgent extends DummyContextManagerAgent {

	DataSource ds;

	private boolean isStation18Storing = false;
	private UUID personCallID = null;

	private HashMap<String, CargoPose> cargo;
	private HashMap<String, RackPose> rack;
	private HashMap<String, Integer> stationVertexMap;

	public DummyLocalContextManagerAgent(String uri, String serverURI) {
		super(uri, Configuration.LOCAL_SERVER_URI + serverURI);
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

		ds = new DataSource() {
			@Override
			public void onNotify(String content) {
				System.out.println("ONNOTIFY on " + uri + "//" + content);
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
					if (firstValue == 0 && secondValue == 0) {
						if (isStation18Storing) {
							ds.assertFact(
									"(context (PersonCall \"station18\" \"PrepareStoring\" \"" + personCallID + "\"))");
							isStation18Storing = true;
						} else {
							ds.assertFact("(context (PersonCall \"station18\" \"Storing\" \"" + personCallID + "\"))");
							isStation18Storing = false;
						}
					} else if (firstValue == 0 && secondValue == 1) {
						ds.assertFact("(context (PersonCall \"station19\" \"Storing\" \"" + personCallID + "\"))");
					} else if (firstValue == 1 && secondValue == 2) {
						ds.assertFact(
								"(context (PersonCall \"station22\" \"PrepareUnstoring\" \"" + personCallID + "\"))");
					} else if (firstValue == 1 && secondValue == 3) {
						ds.assertFact("(context (PersonCall \"station22\" \"Unstoring\" \"" + personCallID + "\"))");
					}
					break;
				}
				}
				
			}
		};
		ds.connect(Configuration.LOCAL_SERVER_URI + serverURI, "ds://www.arbi.com/" + uri, Broker.ZEROMQ);

		ds.subscribe("(rule (fact (Collidable $collidableList)) --> (notify (Collidable $collidableList)))");
		ds.subscribe("(rule (fact (context $context)) --> (notify (context $context)))");
		
		ds.assertFact("(context (RobotVelocity \"AMR_LIFT1\" 0))");
		sleep();
		ds.assertFact("(context (BatteryRemain \"AMR_LIFT1\" 0))");
		sleep();
		ds.assertFact("(context (OnRobotTaskStatus \"AMR_LIFT1\" \"dummy\"))");
		
		sleep();
		ds.assertFact("(context (RobotAt \"AMR_LIFT1\" 0 0))");
		System.out.println("robotat sent");

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
		System.out.println("LOCAL" + notification);
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
				if (cargo.containsKey(cargoID)) {
					CargoPose cp = cargo.get(cargoID);
					cp.setVertex1(vertex1);
					cp.setVertex2(vertex2);
					cp.setRobotID(robotID);
					cp.setRackID(rackID);
				} else {
					cargo.put(cargoID, new CargoPose(vertex1, vertex2, robotID, rackID));
				}
			} else if (name.contentEquals("RackPose")) {
				String rackID = gl.getExpression(0).asValue().stringValue();
				String cargoID = onGL.getExpression(1).asValue().stringValue();
				if (rack.containsKey(rackID)) {
					RackPose rp = rack.get(rackID);
					rp.setVertex1(vertex1);
					rp.setVertex2(vertex2);
					rp.setRobotID(robotID);
					rp.setCargoID(rackID);
				} else {
					rack.put(rackID, new RackPose(vertex1, vertex2, robotID, cargoID));
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
			if (contextName.contentEquals("IdleLiftRack")) {
				return "(context (IdleLiftRack \"rack001\"))";
			} else if (contextName.contentEquals("OnStation")) {
				if (contextGL.getExpression(0).isVariable()) {
					String queryObject = contextGL.getExpression(1).asValue().stringValue();
					if (stationVertexMap.containsKey(queryObject)) {
						rack.forEach((k, v) -> {
							if (v.getVertex1() == v.getVertex2()) {
								if (stationVertexMap.get(queryObject).equals(v.getVertex1())) {
									responseString = queryObject;
								}
							}
						});
						return "(context (OnStation \""+responseString+"\" \"" + queryObject + "\"))";
					}
				}

				String queryObject = contextGL.getExpression(1).asValue().stringValue();
				if (queryObject.contentEquals("station1")) {
					return "(context (OnStation \"rack001\" \"station1\"))";
				} else if (queryObject.contentEquals("station18")) {
					return "(context (OnStation \"rack001\" \"station18\"))";
				} else if (queryObject.contentEquals("station23")) {
					return "(context (OnStation \"rack010\" \"station23\"))";
				}

			} else if (contextName.contentEquals("OnRack")) {
				return "(context (OnRack \"cargo001\" \"rack001\"))";
			} else if (contextName.contentEquals("EmptyStation")) {
				if (contextGL.getExpression(0).isValue()) {
					return "(true)";
				} else {
					return "(context (EmptyStation \"station1\"))";
				}
			} else if (contextName.contentEquals("RackType")) {
				return "(context (RackType \"rack001\" \"lift\"))";
			} else if (contextName.contentEquals("StationAvailability")) {
				return "(context (OnStation \"rack010\" \"station20\"))";
			}
		}
		return "(Error)";
	}

	public static void main(String[] args) {
		DummyLocalContextManagerAgent cAgent = new DummyLocalContextManagerAgent("Local/ContextManager", ":61316");
	}
}
