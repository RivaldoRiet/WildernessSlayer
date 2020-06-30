package SlayerSlayer;

import net.runelite.api.coords.WorldPoint;
import simple.hooks.scripts.task.Task;
import simple.hooks.wrappers.SimpleItem;
import simple.hooks.wrappers.SimpleWidget;
import simple.robot.api.ClientContext;
import simple.robot.utils.WorldArea;

public class BankTask extends Task {
	private Main main;
	private WorldArea edge = new WorldArea(new WorldPoint(3073, 3516, 0), new WorldPoint(3108, 3471, 0));
	
	public BankTask(ClientContext ctx, Main main) {
		super(ctx);
		this.main = main;
	}

	@Override
	public boolean condition() {
		return main.shouldRestock() || main.shouldRestockFromWild();
	}

	@Override
	public void run() {
		if(main.shouldRestockFromWild()) {
			main.status = "Restocking from wild";
			// run back to edge and use preset
			if(ctx.pathing.inArea(edge)) {
					// use preset
					usePreset();
				}else {
					if(inWildy()) {
						if(!above30wild()) {
							main.status = "Searching for glory";
							SimpleItem glory = ctx.equipment.populate().filter(e -> e.getName().toLowerCase().contains("glory")).next();
							
							if(glory != null && glory.validateInteractable()) {
								main.status = "Teleporting edge";
								glory.click("Edgeville");
								ctx.sleep(300);
							}
						}else {
							if (!ctx.pathing.running() && ctx.pathing.energyLevel() >= 30 ) {
								ctx.updateStatus("Turning run on");
								ctx.pathing.running(true);
							}
							
							ctx.pathing.walkPath(main.previousPath, true);
						}
					}else {
						main.status = "Teleporting slayermaster";
						ctx.keyboard.sendKeys("::slayermaster");
						ctx.sleep(1000);
					}
				}
			}
		
		if(main.shouldRestock()) {
			// just use preset we in edge
			main.status = "Restocking";
			usePreset();
		}
	}
	
	private void usePreset() {
		ctx.keyboard.sendKeys("::presets");
		ctx.sleep(1000);
		if(isPresetOpen()) {
			SimpleWidget w = ctx.widgets.getWidget(803, 129);
			if(w != null && w.visibleOnScreen()) {
				if(w.click(0)) {
					w.click(0);
					w.click(0); // the preset button kinda cringe, so hopefully it get it in 3 tries
					ctx.sleep(1000);
				}
			}
		
		
			SimpleWidget closeButton = ctx.widgets.getWidget(803, 90);
			if(closeButton != null && closeButton.visibleOnScreen()) {
				if(closeButton.click(0)) {
					ctx.sleep(300);
				}
			}
		}
	}
	
	private boolean isPresetOpen() {
		SimpleWidget w = ctx.widgets.getWidget(803, 75);
		if(w != null & w.visibleOnScreen() && w.getText().equals("Vitality Item Presets")) {
			return true;
		}
		return false;
	}

	private boolean inWildy() {
		SimpleWidget w = ctx.widgets.getWidget(90, 59); // wilderness widget
		if(w != null && w.visibleOnScreen() && w.getText().contains("Level")) {
			int wildlvl = Integer.parseInt(w.getText().split("Level: ")[1]);
			if(wildlvl > 0) {
				return true;
			}
		}
		
		return false;
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
	
	@Override
	public String status() {
		return "Restocking";
	}
	
}
