package ac.kr.uos.ai.uosbell.dummy.context_manager;

import kr.ac.uos.ai.arbi.Broker;
import kr.ac.uos.ai.arbi.agent.ArbiAgent;
import kr.ac.uos.ai.arbi.agent.ArbiAgentExecutor;

public class DummyContextManagerAgent extends ArbiAgent {

	public DummyContextManagerAgent() {
		
	}
	
	
	
	public void execute(String brokerName, String brokerURL, ArbiAgent agent) {
		String agentURI = "agent://www.arbi.com/" + brokerName + "/ContextManager";
		System.out.println("trying to connect " + brokerURL + " as " + agentURI);
		ArbiAgentExecutor.execute(brokerURL, agentURI, agent, Broker.ZEROMQ);
	}
	
	@Override
	public String onQuery(String sender, String query) {
		System.out.println(query);
		return "Called";
	}
	
	@Override
	public void onNotify(String sender, String notification) {
		// TODO Auto-generated method stubD
		super.onNotify(sender, notification);
	}
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		class RobotThread implements Runnable {

			@Override
			public void run() {
				DummyRobotContextManagerAgent rAgent = new DummyRobotContextManagerAgent("Lift1/ContextManager", ":61116");
			}
			
		}
		
		class LocalThread implements Runnable {

			@Override
			public void run() {
				DummyLocalContextManagerAgent cAgent = new DummyLocalContextManagerAgent("Local/ContextManager", ":61316");
				
			}
			
		}
		
		RobotThread r = new RobotThread();
		LocalThread l = new LocalThread();
		Thread rt = new Thread(r, "RobotThread");
		Thread lt = new Thread(l, "LocalThread");
		lt.run();
		rt.run();
		
//		agent.notify(Configuration.AGENT_NAME_PREFIX + "AMR_LIFT1/ContextManager", "(context (doorStatus \"closed\"))");
	}

}
