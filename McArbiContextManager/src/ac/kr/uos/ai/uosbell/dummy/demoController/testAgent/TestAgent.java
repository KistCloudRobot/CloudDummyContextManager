package ac.kr.uos.ai.uosbell.dummy.demoController.testAgent;

import java.util.Scanner;

import kr.ac.uos.ai.arbi.Broker;
import kr.ac.uos.ai.arbi.agent.ArbiAgent;
import kr.ac.uos.ai.arbi.agent.ArbiAgentExecutor;
import kr.ac.uos.ai.arbi.ltm.DataSource;

public class TestAgent extends ArbiAgent {
	public void onStop(){}
	public String onRequest(String sender, String request){
		System.out.println(sender);
		System.out.println(request);
		return "Ignored";
	}
	
	public void onData(String sender, String data){
		System.out.println(data + " received!");
	}
	public String onSubscribe(String sender, String subscribe){return "Ignored";}
	public void onUnsubscribe(String sender, String subID){}
	public void onNotify(String sender, String notification){}
	
	public TestAgent(){
	}
	
	
	
	
	public void onStart(){
		System.out.println("here");
		//this.request("agent://www.arbi.com/Lift2/BehaviorInterface", "(unload (actionID \"11\") 19)");
		DataSource ds = new DataSource();
		ds.connect("tcp://172.16.165.106:61313", "ds://www.arbi.com/Local/TestAgent2",Broker.ZEROMQ);
		
		Scanner in = new Scanner(System.in);
		
		//String result = this.query("noReceiver", "anyquery");
		//System.out.println("result : " + result);
		
		System.out.println("start next person call");

		ds.assertFact("(context (PersonCall \"call01\" \"station18\" \"PrepareStoring\"))");
		//in.nextLine();
		
		try {
			Thread.sleep(8000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("start next person call");
		
		ds.assertFact("(context (PersonCall \"call02\" \"station19\" \"PrepareStoring\"))");
		in.nextLine();
		/*
		System.out.println("start next person call");
		
		ds.assertFact("(context (PersonCall \"call03\" \"station22\" \"PrepareUnstoring\"))");
		in.nextLine();
		System.out.println("start next person call");
		
		ds.assertFact("(context (PersonCall \"call04\" \"station22\" \"Unstoring\"))");
		
		/*
		ds.assertFact("(context (PersonCall \"call001\" \"station22\" \"Unstoring\"))");
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ds.assertFact("(context (PersonCall \"call002\" \"station18\" \"PrepareStoring\"))");
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ds.assertFact("(context (PersonCall \"call003\" \"station22\" \"PrepareUnstoring\"))");
		*/
		System.out.println("asserted");
		//this.send("agent://www.arbi.com/Local/TaskAllocator", "(testing)");
	}
	public String onQuery(String sender, String query){
		return "(ok)";
		
	}
	public static void main(String[] args) {
		ArbiAgent agent = new TestAgent();
		ArbiAgentExecutor.execute("tcp://172.16.165.106:61313", "agent://www.arbi.com/Local/TestAgent2", agent, 2);
		//System.out.println(agent.request("agent://www.arbi.com/Local/NavigationController", "(Move (actionID \"action1\") \"AMR_LIFT1\" 201 15)"));
		System.out.println("requested");
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//System.out.println(agent.request("agent://www.arbi.com/Local/TaskAllocator", "(TaskAllocation (PalletTransported \"local1\" \"station13\"))"));
	}
}
