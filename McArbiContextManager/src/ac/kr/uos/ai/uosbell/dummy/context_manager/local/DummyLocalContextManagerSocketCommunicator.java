package ac.kr.uos.ai.uosbell.dummy.context_manager.local;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import ac.kr.uos.ai.uosbell.dummy.context_manager.DummyLocalContextManagerAgent;

public class DummyLocalContextManagerSocketCommunicator implements Runnable {
	DummyLocalContextManagerAgent agent;
	public DummyLocalContextManagerSocketCommunicator(DummyLocalContextManagerAgent agent) {
		this.agent = agent;
	}
	
	public void run() {
		Socket socket = null;
		try {
			socket = new Socket("ec2-3-38-102-222.ap-northeast-2.compute.amazonaws.com", 5151);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		InputStream input = null;
		OutputStream output = null;
		try {
			input = socket.getInputStream();
			output = socket.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		PrintWriter writer = new PrintWriter(output, true);

		String str = "(TaskPrepared)";
		writer.println(str);

		String msg;

		while (true) {
			try {
				if ((msg = reader.readLine()) != null) {
					System.out.println(msg);
					agent.getDataSource().assertFact(msg);
					Thread.sleep(10);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
