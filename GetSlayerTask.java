package SlayerSlayer;

import simple.hooks.scripts.task.Task;
import simple.hooks.simplebot.Game.Tab;
import simple.hooks.wrappers.SimpleItem;
import simple.hooks.wrappers.SimpleNpc;
import simple.hooks.wrappers.SimpleWidget;
import simple.robot.api.ClientContext;

public class GetSlayerTask extends Task {
	private Main main;
	
	public GetSlayerTask(ClientContext ctx, Main main) {
		super(ctx);
		this.main = main;
	}

	@Override
	public boolean condition() {
		return !main.hasTask && !main.shouldRestockFromWild();
	}

	@Override
	public void run() {
		if(!main.hasTask && !main.shouldGetTask) {
			main.status = "Checking task";
			getTask();
		}else if(!main.hasTask && main.shouldGetTask) {
			main.status = "Getting new task";
			getNewTask();
		}
	}
	
	@Override
	public String status() {
		return "Getting slayer task";
	}
	
	private boolean isTabOpen(Tab tab) {
		return ctx.game.tab().equals(tab);
	}

	private void openQuestTab() {
		if(!isTabOpen(Tab.QUESTS)) {
			if(ctx.game.tab(Tab.QUESTS)) {
				ctx.sleep(300);
			}
		}
	}
	
	private boolean above30wild() {
		SimpleWidget w = ctx.widgets.getWidget(90, 59); // wilderness widget
		if(w != null && w.visibleOnScreen() && w.getText().contains("Level")) {
			int wildlvl = Integer.parseInt(w.getText().split("Level: ")[1]);
			if(wildlvl > 30) {
				return true;
			}
		}
		
		return false;
	}
	
	private void getTask() {
		if(!isTabOpen(Tab.QUESTS)) {
			main.status = "Opening quest tab";
			openQuestTab();
		}
		ctx.sleep(1000);
		main.status = "Scrolling down";
		ctx.mouse.click(708, 366, true);
		main.status = "Clicking widget";
		 SimpleWidget w = ctx.widgets.getWidget(805, 23); // the task menu
		  if(w != null && w.visibleOnScreen() && w.getText().contains("Slayer Task") && w.validateInteractable()) {
			  if(w.click(0)) {
				  ctx.sleep(200);
			  }
		  }
	}
	
	private void getNewTask() {
		

		if(isDialogueOpen()) {
			if(ctx.dialogue.canContinue()) {
				ctx.dialogue.clickContinue();
			}else {
				SimpleWidget w = ctx.widgets.getWidget(219, 1);
				if(w != null && w.visibleOnScreen()) {
					if(w.getChild(3) != null && w.getChild(3).getText().contains("Hard")) {
						if(w.getChild(3).click(0)) {
							ctx.sleep(200);
						}
					}
					
					if(w.getChild(1).getText().contains("dangerous")) {
						if(w.getChild(1) != null && w.getChild(1).click(0)) {
							ctx.sleep(200);
						}
					}
					
					
				}
			}
		}else {
			if(!above30wild()) {
				getTask();
				SimpleNpc nieve = ctx.npcs.populate().filter("Nieve").next();
				if(nieve != null && nieve.validateInteractable()) {
					if(nieve.click("Assignment")) {
						ctx.sleep(200);
					}
				}else {
					if(main.inWildy()) {
						SimpleItem glory = ctx.equipment.filter(e -> e.getName().toLowerCase().contains("glory")).next();
						if(glory != null && glory.validateInteractable()) {
							glory.click("Edgeville");
							ctx.sleep(300);
						}
					}else {
						ctx.keyboard.sendKeys("::slayermaster");
						ctx.sleep(1000);
					}
				}
			}else {
				ctx.pathing.walkPath(main.previousPath, true); //run the last path in reverse
			}
		}
	}
	
	private boolean isDialogueOpen() {
		
		
		SimpleWidget widg = ctx.widgets.getWidget(231, 4);
		if(widg != null) {
			if(widg.visibleOnScreen()) {
				if(widg.getText().contains("to kill") && widg.getText().contains(";")) {
					main.currentMonster = widg.getText().split("You're currently assigned to kill ")[1].split(";")[0];
					main.currentMonster = removeLastChar(main.currentMonster);
					ctx.log("npc is: " + main.currentMonster);
					main.shouldGetTask = false;
					main.hasTask = true;
				}
				return true;
			}
		}
		
		SimpleWidget w = ctx.widgets.getWidget(219, 1);
		if(w != null && w.visibleOnScreen()) {
			return true;
		}
		
		
		SimpleWidget chatbox = ctx.widgets.getWidget(162, 44);
		if(chatbox != null) {
			if(chatbox.getText().contains("Enter") && chatbox.visibleOnScreen()) {
				return true;
			}
		}
		
		SimpleWidget npc = ctx.widgets.getWidget(231, 0);
		if(npc != null && npc.visibleOnScreen()) {
			return true;
		}
		
		SimpleWidget player = ctx.widgets.getWidget(217, 0);
		if(player != null && player.visibleOnScreen()) {
			return true;
		}
		
		return false;
	}
	
	private String removeLastChar(String str) {
	    return str.substring(0, str.length());
	}

}
