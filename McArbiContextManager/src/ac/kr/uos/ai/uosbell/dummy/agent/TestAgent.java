package ac.kr.uos.ai.uosbell.dummy.agent;

import java.net.InetAddress;
import java.net.UnknownHostException;

import kr.ac.uos.ai.arbi.Broker;
import kr.ac.uos.ai.arbi.agent.ArbiAgent;
import kr.ac.uos.ai.arbi.agent.ArbiAgentExecutor;

public class TestAgent extends ArbiAgent {
	
	public TestAgent(String brokerURL, String brokerName) {
		String agentURI = "agent://www.arbi.com/" + brokerName + "/TestAgent";
		ArbiAgentExecutor.execute(brokerURL, agentURI, this, Broker.ZEROMQ);
	}
	
	public static void main(String[] args) {
//		try {
//			String ipr = InetAddress.getLocalHost().getHostAddress();
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		String ip = "127.0.0.1";
		String brokerURL = "tcp://"+ip+":61115";
		String brokerName = "Lift2";
		TestAgent agent = new TestAgent(brokerURL, brokerName);

		String response = agent.query("agent://www.arbi.com/Lift2/ContextManager", "(StationVertex 201 $s)");
		System.out.println(response);
	}

}
