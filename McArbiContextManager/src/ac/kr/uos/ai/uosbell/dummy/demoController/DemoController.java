package ac.kr.uos.ai.uosbell.dummy.demoController;

import ac.kr.uos.ai.uosbell.dummy.demoController.view.ViewManager;
import kr.ac.uos.ai.arbi.Broker;
import kr.ac.uos.ai.arbi.agent.ArbiAgent;
import kr.ac.uos.ai.arbi.agent.ArbiAgentExecutor;
import kr.ac.uos.ai.arbi.ltm.DataSource;

public class DemoController extends ArbiAgent{

	private DataSource ds;
	private static final String BROKER_URL = "tcp://172.16.165.106:61313";
	private static String myAddress = "agent://www.arbi.com/Local/DemoController";
	private static String dataSourceAddress = "ds://www.arbi.com/Local/DemoController";
	
	private ViewManager viewManager;
	private DemoButtonListner listner;
	
	@Override
	public void onData(String sender, String data) {
		System.out.println("onData Sender : " + sender);
		System.out.println("onData Data : " + data);
	}
	
	@Override
	public String onRequest(String sender, String request) {
		System.out.println("onRequest Sender : " + sender);
		System.out.println("onRequest request : " + request);
		// TODO Auto-generated method stub
		return "(fail)";
	}
	
	@Override
	public void send(String receiver, String data) {
		System.out.println("send receiver : " + receiver);
		System.out.println("send data : " + data);
		super.send(receiver, data);
	}
	
	public DemoController() {
		
		ds = new DataSource() {
			@Override
			public void assertFact(String fact) {
				System.out.println("assert fact : " + fact);
				super.assertFact(fact);
			}
		};
		
		listner = new DemoButtonListner(this);
		viewManager = new ViewManager(listner);
	}
	
	public void assertFact(String fact) {
		ds.assertFact(fact);
	}
	
	@Override
	public void onStart() {
		ds.connect(BROKER_URL, dataSourceAddress, Broker.ZEROMQ);
		viewManager.showFrame();
	}
	
	public static void main(String[] args) {
		ArbiAgent agent = new DemoController();
		ArbiAgentExecutor.execute(BROKER_URL, myAddress, agent, Broker.ZEROMQ);
	}
}
