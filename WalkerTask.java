package SlayerSlayer;

import net.runelite.api.coords.WorldPoint;
import simple.hooks.scripts.task.Task;
import simple.hooks.wrappers.SimpleNpc;
import simple.hooks.wrappers.SimpleObject;
import simple.hooks.wrappers.SimpleWidget;
import simple.robot.api.ClientContext;
import simple.robot.utils.WorldArea;

public class WalkerTask extends Task {
	private Main main;
	private WorldArea graves = new WorldArea(new WorldPoint(3126, 3705, 0), new WorldPoint(3210, 3649, 0));
	private WorldArea graves_mammoth = new WorldArea(new WorldPoint(3123, 3683, 0), new WorldPoint(3177, 3610, 0));
	private WorldArea rev_cave_entrance = new WorldArea(new WorldPoint(3058, 3661, 0), new WorldPoint(3092, 3633, 0));
	private WorldArea rev_cave = new WorldArea(new WorldPoint(3122, 10247, 0), new WorldPoint(3278, 10047, 0));
	private WorldArea edge = new WorldArea(new WorldPoint(3073, 3516, 0), new WorldPoint(3108, 3471, 0));
	private WorldArea entArea = new WorldArea(new WorldPoint(3182, 3715, 0), new WorldPoint(3240, 3651, 0));
	private WorldArea edge_dungeon = new WorldArea(new WorldPoint(3055, 10013, 0), new WorldPoint(3163, 9813, 0));
	
	private Paths paths = new Paths();
	
	
	public WalkerTask(ClientContext ctx, Main main) {
		super(ctx);
		this.main = main;
	}


	@Override
	public boolean condition() {
		return main.currentMonster != null && !main.shouldRestock() && !main.shouldRestockFromWild() && !main.blockedMonster() && main.isGeared() && !slayerMonsterPresent();
	}

	@Override
	public void run() {
		main.status = "Searching npc path";

		if(nameEquals("ent") && !ctx.pathing.inArea(entArea)) {
			walkEnt();
		}
		
		if(nameContains("greater demon")) {
			this.walkGreater();
		}
		
		if(nameContains("lesser demon")) {
			this.walkLesser();
		}
		
		if(nameEquals("ankou")) {
			this.walkAnkou();
		}
		
		if(nameEquals("black dragon")) {
			this.walkBlackdragon();
		}
		
		if(nameEquals("hellhound")) {
			this.walkHellhound();
		}
		
		if(nameEquals("black demon")) {
			this.walkBlackDemon();
		}
		
		if(nameEquals("green dragon")) {
			this.walkGreenDragon();
		}
		
		if(nameEquals("ice giant")) {
			this.walkIceGiant();
		}
		
		if(nameEquals("mammoth")) {
			this.walkMammoth();
		}
	}
	   
	@Override
	public String status() {
		return "Walking to slayer task";
	}
	
	private boolean nameEquals(String s) {
		return main.currentMonster.toLowerCase().equals(s.toLowerCase());
	}
	
	private boolean nameContains(String s) {
		return main.currentMonster.toLowerCase().contains(s.toLowerCase());
	}
	
	private boolean above20wild() {
		SimpleWidget w = ctx.widgets.getWidget(90, 59); // wilderness widget
		if(w != null && w.visibleOnScreen() && w.getText().contains("Level")) {
			int wildlvl = Integer.parseInt(w.getText().split("Level: ")[1]);
			if(wildlvl > 20) {
				return true;
			}
		}
		
		return false;
	}
	
	private void walkMammoth() {
		main.status = "Walking to mammoth";
		if (!ctx.pathing.running() && ctx.pathing.energyLevel() >= 30 ) {
			ctx.updateStatus("Turning run on");
			ctx.pathing.running(true);
		}
		
		if(main.isGeared()) {
			if(ctx.pathing.inArea(graves_mammoth)) {
				main.previousPath = paths.getMammothPath();
				ctx.pathing.walkPath(paths.getMammothPath());
			}else if(!above20wild()){
				ctx.keyboard.sendKeys("::graves");
				ctx.sleep(300);
				ctx.keyboard.sendKeys(" ");
				ctx.sleep(300);
				ctx.keyboard.sendKeys("1");
				ctx.sleep(300);
			}
		}
	}
	
	private void walkGreenDragon() {
		main.status = "Walking to green dragon";
		if (!ctx.pathing.running() && ctx.pathing.energyLevel() >= 30 ) {
			ctx.updateStatus("Turning run on");
			ctx.pathing.running(true);
		}
		
		if(main.isGeared()) {
			if(ctx.pathing.inArea(graves)) {
				main.previousPath = paths.getGreendragonPath2();
				ctx.pathing.walkPath(paths.getGreendragonPath2());
			}else if(!above20wild()){
				ctx.keyboard.sendKeys("::graves");
				ctx.sleep(300);
				ctx.keyboard.sendKeys(" ");
				ctx.sleep(300);
				ctx.keyboard.sendKeys("1");
				ctx.sleep(300);
			}
		}
	}
	
	
	private void walkEnt() {
		main.status = "Walking to ent";
		if (!ctx.pathing.running() && ctx.pathing.energyLevel() >= 30 ) {
			ctx.updateStatus("Turning run on");
			ctx.pathing.running(true);
		}
		
		if(main.isGeared()) {
			if(ctx.pathing.inArea(graves)) {
				main.previousPath = paths.getEntPath();
				ctx.pathing.walkPath(paths.getEntPath());
			}else if(!above20wild()){
				ctx.keyboard.sendKeys("::graves");
				ctx.sleep(300);
				ctx.keyboard.sendKeys(" ");
				ctx.sleep(300);
				ctx.keyboard.sendKeys("1");
				ctx.sleep(300);
			}
		}
	}
	
	private void walkBlackDemon() {
		main.status = "Walking to Black Demon";
		if (!ctx.pathing.running() && ctx.pathing.energyLevel() >= 30 ) {
			ctx.updateStatus("Turning run on");
			ctx.pathing.running(true);
		}
		
		if(main.isGeared()) {
			if(!ctx.pathing.inArea(edge_dungeon)) {
				if(!isTeleportScreenOpen()) {
				SimpleNpc wizard = ctx.npcs.populate().filter("Vitality wizard").nearest().next();
	
					if(wizard != null && wizard.validateInteractable()) {
						if(wizard.click("Teleport")) {
							ctx.onCondition(() -> !isTeleportScreenOpen(), 5000);
						}
					}else {
						teleportToEdge();
					}
				}else {
					teleportToEdgeDung();
				}
			}else {
				main.previousPath = paths.getBlackdemonPath2();
				ctx.pathing.walkPath(paths.getBlackdemonPath2());
			}
			
			/*
			if(ctx.pathing.inArea(this.rev_cave_entrance)) {
				SimpleObject cave = ctx.objects.populate().filter(31555).filter("Cavern").next();
				if(cave != null && cave.validateInteractable()) {
					if(cave.click("Enter")) {
						ctx.sleep(300);
					}
				}
			}else {
				if(ctx.pathing.inArea(this.rev_cave)) {
					main.previousPath = paths.getBlackdemonPath();
					ctx.pathing.walkPath(paths.getBlackdemonPath());
				}else if(!above20wild()){
					ctx.keyboard.sendKeys("::revs");
					ctx.sleep(300);
					ctx.keyboard.sendKeys(" ");
					ctx.sleep(300);
					ctx.keyboard.sendKeys("1");
					ctx.sleep(300);
				}
			}*/
		}
	}
	
	private void walkIceGiant() {
		main.status = "Walking to ice giants";
		if (!ctx.pathing.running() && ctx.pathing.energyLevel() >= 30 ) {
			ctx.updateStatus("Turning run on");
			ctx.pathing.running(true);
		}
		
		if(main.isGeared()) {
			if(ctx.pathing.inArea(this.rev_cave_entrance)) {
				SimpleObject cave = ctx.objects.populate().filter(31555).filter("Cavern").next();
				if(cave != null && cave.validateInteractable()) {
					if(cave.click("Enter")) {
						ctx.sleep(300);
					}
				}
			}else {
				if(ctx.pathing.inArea(this.rev_cave)) {
					main.previousPath = paths.getIceGiantPath();
					ctx.pathing.walkPath(paths.getIceGiantPath());
				}else if(!above20wild()){
					ctx.keyboard.sendKeys("::revs");
					ctx.sleep(300);
					ctx.keyboard.sendKeys(" ");
					ctx.sleep(300);
					ctx.keyboard.sendKeys("1");
					ctx.sleep(300);
				}
			}
		}
	}
	
	private void walkHellhound() {
		main.status = "Walking to Hellhounds";
		if (!ctx.pathing.running() && ctx.pathing.energyLevel() >= 30 ) {
			ctx.updateStatus("Turning run on");
			ctx.pathing.running(true);
		}
		
		if(main.isGeared()) {
			if(ctx.pathing.inArea(this.rev_cave_entrance)) {
				SimpleObject cave = ctx.objects.populate().filter(31555).filter("Cavern").next();
				if(cave != null && cave.validateInteractable()) {
					if(cave.click("Enter")) {
						ctx.sleep(300);
					}
				}
			}else {
				if(ctx.pathing.inArea(this.rev_cave)) {
					main.previousPath = paths.getHellhoundPath();
					ctx.pathing.walkPath(paths.getHellhoundPath());
				}else if(!above20wild()){
					ctx.keyboard.sendKeys("::revs");
					ctx.sleep(300);
					ctx.keyboard.sendKeys(" ");
					ctx.sleep(300);
					ctx.keyboard.sendKeys("1");
					ctx.sleep(300);
				}
			}
		}
	}
	
	private void walkBlackdragon() {
		main.status = "Walking to black dragon";
		if (!ctx.pathing.running() && ctx.pathing.energyLevel() >= 30 ) {
			ctx.updateStatus("Turning run on");
			ctx.pathing.running(true);
		}
		
		if(main.isGeared()) {
			if(ctx.pathing.inArea(this.rev_cave_entrance)) {
				SimpleObject cave = ctx.objects.populate().filter(31555).filter("Cavern").next();
				if(cave != null && cave.validateInteractable()) {
					if(cave.click("Enter")) {
						ctx.sleep(300);
					}
				}
			}else {
				if(ctx.pathing.inArea(this.rev_cave)) {
					main.previousPath = paths.getBlackDragonPath();
					ctx.pathing.walkPath(paths.getBlackDragonPath());
				}else if(!above20wild()){
					ctx.keyboard.sendKeys("::revs");
					ctx.sleep(300);
					ctx.keyboard.sendKeys(" ");
					ctx.sleep(300);
					ctx.keyboard.sendKeys("1");
					ctx.sleep(300);
				}
			}
		}
	}
	
	private void walkAnkou() {
		main.status = "Walking to Ankou";
		if (!ctx.pathing.running() && ctx.pathing.energyLevel() >= 30 ) {
			ctx.updateStatus("Turning run on");
			ctx.pathing.running(true);
		}
		
		if(main.isGeared()) {
			if(ctx.pathing.inArea(this.rev_cave_entrance)) {
				SimpleObject cave = ctx.objects.populate().filter(31555).filter("Cavern").next();
				if(cave != null && cave.validateInteractable()) {
					if(cave.click("Enter")) {
						ctx.sleep(300);
					}
				}
			}else {
				if(ctx.pathing.inArea(this.rev_cave)) {
					main.previousPath = paths.getAnkouPath();
					ctx.pathing.walkPath(paths.getAnkouPath());
				}else if(!above20wild()){
					ctx.keyboard.sendKeys("::revs");
					ctx.sleep(300);
					ctx.keyboard.sendKeys(" ");
					ctx.sleep(300);
					ctx.keyboard.sendKeys("1");
					ctx.sleep(300);
				}
			}
		}
	}
	
	
	private void walkGreater() {
		main.status = "Walking to greater demon";
		if (!ctx.pathing.running() && ctx.pathing.energyLevel() >= 30 ) {
			ctx.updateStatus("Turning run on");
			ctx.pathing.running(true);
		}
		
		if(main.isGeared()) {
			if(ctx.pathing.inArea(this.rev_cave_entrance)) {
				SimpleObject cave = ctx.objects.populate().filter(31555).filter("Cavern").next();
				if(cave != null && cave.validateInteractable()) {
					if(cave.click("Enter")) {
						ctx.sleep(300);
					}
				}
			}else {
				if(ctx.pathing.inArea(this.rev_cave)) {
					main.previousPath = paths.getGreaterDemonPath();
					ctx.pathing.walkPath(paths.getGreaterDemonPath());
				}else if(!above20wild()){
					ctx.keyboard.sendKeys("::revs");
					ctx.sleep(300);
					ctx.keyboard.sendKeys(" ");
					ctx.sleep(300);
					ctx.keyboard.sendKeys("1");
					ctx.sleep(300);
				}
			}
		}
	}
	
	
	private void walkLesser() {
		main.status = "Walking to lesser demon";
		if (!ctx.pathing.running() && ctx.pathing.energyLevel() >= 30 ) {
			ctx.updateStatus("Turning run on");
			ctx.pathing.running(true);
		}
		
		if(main.isGeared()) {
			if(ctx.pathing.inArea(this.rev_cave_entrance)) {
				SimpleObject cave = ctx.objects.populate().filter(31555).filter("Cavern").next();
				if(cave != null && cave.validateInteractable()) {
					if(cave.click("Enter")) {
						ctx.sleep(300);
					}
				}
			}else {
				if(ctx.pathing.inArea(this.rev_cave)) {
					main.previousPath = paths.getLesserdemonPath();
					ctx.pathing.walkPath(paths.getLesserdemonPath());
				}else if(!above20wild()){
					ctx.keyboard.sendKeys("::revs");
					ctx.sleep(300);
					ctx.keyboard.sendKeys(" ");
					ctx.sleep(300);
					ctx.keyboard.sendKeys("1");
					ctx.sleep(300);
				}
			}
		}
	}
	
	private boolean isTeleportScreenOpen() {
		SimpleWidget w = ctx.widgets.getWidget(804, 2);
		if(w != null && w.visibleOnScreen() && w.getText().contains("Vitality Teleportation")) {
			return true;
		}
		return false;
	}
	

private void teleportToEdge() {
	ctx.keyboard.sendKeys("::home");
	ctx.sleep(600);
	ctx.onCondition(() -> edge.containsPoint(ctx.players.getLocal().getLocation()), 5000);

	if(!this.edge.containsPoint(ctx.players.getLocal().getLocation())) {
		ctx.magic.castSpellOnce("Lumbridge Home Teleport");
		ctx.sleep(2000);
	}
}
	
	private boolean slayerMonsterPresent() {
		return !ctx.npcs.populate().filter(n -> n.getName().toLowerCase().equals(main.currentMonster)).filter(n -> !n.isDead()).isEmpty();
	}

	private void teleportToEdgeDung() {
		if(this.isTeleportScreenOpen()) {
			SimpleWidget w = ctx.widgets.getWidget(804, 10);
			if(w != null && w.visibleOnScreen()) {
				SimpleWidget scrollList = w.getChild(4);
				if(scrollList != null && scrollList.visibleOnScreen()) {
					if(scrollList.click(0)) {
						ctx.sleep(1000);
					}
				}
			}
			
			SimpleWidget d = ctx.widgets.getWidget(804, 14);
			if(d != null && d.visibleOnScreen()) {
				SimpleWidget dungList = d.getChild(12);
				if(dungList != null && dungList.visibleOnScreen()) {
					if(dungList.click(0)) {
						ctx.sleep(1000);
					}
				}
			}
			
		}
	}
}
