package SlayerSlayer;

import simple.hooks.scripts.task.Task;
import simple.hooks.simplebot.Game.Tab;
import simple.hooks.wrappers.SimpleNpc;
import simple.hooks.wrappers.SimpleWidget;
import simple.robot.api.ClientContext;

public class BlockTask extends Task {
	private Main main;
	
	public BlockTask(ClientContext ctx, Main main) {
		super(ctx);
		this.main = main;
	}

	@Override
	public boolean condition() {
		return main.blockedMonster();
	}

	@Override
	public void run() {
		if(rewardScreenOpen()) {
			SimpleWidget rewardscreen = ctx.widgets.getWidget(426, 12);
			if(rewardscreen != null && rewardscreen.visibleOnScreen()) {
				SimpleWidget taskWidget = rewardscreen.getChild(6);
				if(taskWidget != null && taskWidget.visibleOnScreen()) {
					if(taskWidget.click(0)) {
						ctx.sleep(1000);
					}
				}
			}
			
			SimpleWidget blockscreen = ctx.widgets.getWidget(426, 26);
			if(blockscreen != null && blockscreen.visibleOnScreen()) {
				
				if(this.rewardPoints() >= 100) {
					SimpleWidget blockbutton = blockscreen.getChild(2);
					if(blockbutton != null && blockbutton.visibleOnScreen()) {
						if(blockbutton.click(0)) {
							ctx.sleep(1000);
						}
					}
				}else {
					SimpleWidget skipbutton = blockscreen.getChild(1);
					if(skipbutton != null && skipbutton.visibleOnScreen()) {
						if(skipbutton.click(0)) {
							ctx.sleep(1000);
						}
					}
				}
			}
			
			SimpleWidget confirm = ctx.widgets.getWidget(426, 10);
			if(confirm != null && confirm.visibleOnScreen()) {
				if(confirm.click(0)) {
					main.currentMonster = null;
					ctx.sleep(1000);
				}
			}
			
			SimpleWidget close = ctx.widgets.getWidget(426, 1);
			if(close != null && close.visibleOnScreen()) {
				SimpleWidget closebutton = close.getChild(11);
				if(closebutton != null && closebutton.visibleOnScreen()) {
					if(closebutton.click(0)) {
						closebutton.click(0);
						closebutton.click(0);
						ctx.sleep(1000);
					}
				}
			}
			
			getTask();
			
		}else {
			SimpleNpc nieve = ctx.npcs.populate().filter("Nieve").next();
			if(nieve != null && nieve.validateInteractable()) {
				if(nieve.click("Rewards")) {
					ctx.sleep(200);
				}
			}
		}
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
	
	@Override
	public String status() {
		return "Blocking slayer task";
	}
	
	private boolean rewardScreenOpen() {
		SimpleWidget w = ctx.widgets.getWidget(426, 12);
		if(w != null && w.visibleOnScreen()) {
			if(w.getChild(8) != null && w.getChild(8).visibleOnScreen()) {
				return true;
			}
		}
		return false;
	}
	
	private int rewardPoints() {
		SimpleWidget w = ctx.widgets.getWidget(426, 12);
		if(w != null && w.visibleOnScreen()) {
			if(w.getChild(8) != null && w.getChild(8).visibleOnScreen()) {
				return Integer.parseInt(w.getChild(8).getText().split("Reward points: ")[1]);
			}
		}
		return 0;
	}
	
}
