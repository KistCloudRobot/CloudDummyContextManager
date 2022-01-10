package ac.kr.uos.ai.uosbell.dummy.demoController;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.UUID;

public class DemoButtonListner implements ActionListener{

	private DemoController controller;
	public DemoButtonListner(DemoController controller) {
		this.controller = controller;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		String callID = UUID.randomUUID().toString();
		if(e.getActionCommand().equals("PrepareStoring at Station18")) {
			controller.assertFact("(context (PersonCall \"" + callID +"\" \"station18\" \"PrepareStoring\"))");
		} else if(e.getActionCommand().equals("PrepareStoring at Station19")) {
			controller.assertFact("(context (PersonCall \"" + callID +"\" \"station19\" \"PrepareStoring\"))");
		} else if(e.getActionCommand().equals("Storing at Station18")) {
			controller.assertFact("(context (PersonCall \"" + callID +"\" \"station18\" \"Storing\"))");
		} else if(e.getActionCommand().equals("Storing at Station19")) {
			controller.assertFact("(context (PersonCall \"" + callID +"\" \"station19\" \"Storing\"))");
		} else if(e.getActionCommand().equals("PrepareUnstoring at Station22")) {
			controller.assertFact("(context (PersonCall \"" + callID +"\" \"station22\" \"PrepareUnstoring\"))");
		} else if(e.getActionCommand().equals("Unstoring at Station22")) {
			controller.assertFact("(context (PersonCall \"" + callID +"\" \"station22\" \"Unstoring\"))");
		} else if(e.getActionCommand().equals("Multi Robot Demo")) {

			controller.assertFact("(context (PersonCall \"call001\" \"station22\" \"Unstoring\"))");
			try {
				Thread.sleep(100);
			} catch (InterruptedException except) {
				// TODO Auto-generated catch block
				except.printStackTrace();
			}
			controller.assertFact("(context (PersonCall \"call002\" \"station18\" \"PrepareStoring\"))");
			try {
				Thread.sleep(100);
			} catch (InterruptedException except) {
				// TODO Auto-generated catch block
				except.printStackTrace();
			}

			controller.assertFact("(context (PersonCall \"call003\" \"station22\" \"PrepareUnstoring\"))");
		}
	}

}
