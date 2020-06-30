package SlayerSlayer;

import net.runelite.api.coords.WorldPoint;
import simple.hooks.filters.SimpleSkills;
import simple.hooks.filters.SimplePrayers.Prayers;
import simple.hooks.queries.SimpleEntityQuery;
import simple.hooks.scripts.task.Task;
import simple.hooks.wrappers.SimpleGroundItem;
import simple.hooks.wrappers.SimpleItem;
import simple.hooks.wrappers.SimpleNpc;
import simple.hooks.wrappers.SimplePlayer;
import simple.robot.api.ClientContext;

public class NpcFighterTask extends Task {
	private Main main;

	public NpcFighterTask(ClientContext ctx, Main main) {
		super(ctx);
		this.main = main;
	}

	@Override
	public boolean condition() {
		return  !main.shouldRestockFromWild() && main.currentMonster != null && !ctx.npcs.populate().filter(n -> n.getName().toLowerCase().equals(main.currentMonster)).filter(n -> !n.isDead()).isEmpty();
	}

	@Override
	public void run() {
        SimplePlayer p = ctx.players.getLocal();
        SimpleEntityQuery<SimpleNpc> npcs = ctx.npcs.populate().filter(n -> n.getName().toLowerCase().equals(main.currentMonster)).filter(n -> !n.isDead());
        main.slayerlvl = Integer.valueOf(this.ctx.skills.realLevel(SimpleSkills.Skills.SLAYER));
        
        if(!isPrayerRight() && (Integer.valueOf(this.ctx.skills.level(SimpleSkills.Skills.PRAYER))) > 0) {
			main.status = "Setting prayer";
			setPrayer();
		}
        
        if(!ctx.combat.inMultiCombat()) {
        	npcs = ctx.npcs.populate().filter(n -> n.getName().toLowerCase().equals(main.currentMonster)).filter(n -> !n.isDead()).filter(n -> n.getInteracting() != null && n.getInteracting().getName().equals(ctx.players.getLocal().getName()));
        	if(npcs.size() > 0) {
        	}else {
        	npcs = ctx.npcs.populate().filter(n -> n.getName().toLowerCase().equals(main.currentMonster)).filter(n -> !n.isDead()).filter(n -> !n.inCombat());
        	}
        }
        
        SimpleNpc npcToAttack = npcs.nearest().next();

        if(p.isAnimating()) {
    		ctx.onCondition(() -> !p.isAnimating(), 1000);
    	}
        
        checkAntiFire();
        drinkPrayerPot();
        openCasket();
        antiStuck();
        
        int strlvl = Integer.valueOf(this.ctx.skills.level(SimpleSkills.Skills.STRENGTH));
		if(strlvl <= 112) {
			if(containsItem("combat")) {
				prepot();
			}
		}
		
		int rangelvl = Integer.valueOf(this.ctx.skills.level(SimpleSkills.Skills.RANGED));
		if(rangelvl <= 108) {
			if(containsItem("Bastion")) {
				prepot();
			}
		}
        
        if(lootOnGround() && ctx.inventory.populate().population() < 28) {
        	main.status = "looting";
        	lootItem();
        }else {
	        if(npcToAttack != null && !p.inCombat()) {
	        	if(npcToAttack.validateInteractable() && npcToAttack.turnTo()) {
		        	if(npcToAttack.click("Attack")) {
		        		main.status = "fighting";
		        		ctx.onCondition(() -> !p.inCombat(), 10000);
		        	}
	        	}
	        }
        }

        ctx.sleep(5);
	}
	
	@Override
	public String status() {
		return "Fighting slayer task";
	}

	private boolean containsItem(String itemName) {
		return !ctx.inventory.populate().filter(p -> p.getName().contains(itemName)).isEmpty();
	}
	
	private void antiStuck() {
		if(main.antiStuck) {
			ctx.log("antistuck activated");
			// anti stuck
			SimplePlayer p = ctx.players.getLocal();
			WorldPoint w = new WorldPoint(p.getLocation().getX(), p.getLocation().getY() - 4 ,0);
			if(ctx.pathing.reachable(w)) {
				ctx.pathing.step(w);
			}else {
				w = new WorldPoint(p.getLocation().getX() + 4, p.getLocation().getY() ,0);
				if(ctx.pathing.reachable(w)) {
					ctx.pathing.step(w);
				}
			}
			main.antiStuck = false;
		}
	}
	
	private void checkAntiFire() {
		if(main.currentMonster.contains("dragon") && main.shouldDrinkAntifire) {
			if(main.antifireTimer == 0 || main.antifireTimer != 0 && (System.currentTimeMillis() - main.antifireTimer) > 150000 ) {
				drinkAntiFire();
				main.antifireTimer = System.currentTimeMillis();
				main.shouldDrinkAntifire = false;
			}
		}
	}
	
	public void drinkAntiFire() {
		SimpleItem pot = ctx.inventory.populate().filter(p -> p.getName().contains("antifire")).next();
		if(pot != null && pot.click(0)) {
			ctx.sleep(500);
		}
	}
	
	
	private void prepot() {
		SimpleItem pot = ctx.inventory.populate().filter(p -> p.getName().contains("combat")).next();
		if(pot != null && pot.click(0)) {
			ctx.sleep(500);
		}
		
		SimpleItem bpot = ctx.inventory.populate().filter(p -> p.getName().contains("Bastion")).next();
		if(bpot != null && bpot.click(0)) {
			ctx.sleep(500);
		}
	}
	
	private void drinkPrayerPot() {
		int prayerlvl =  Integer.valueOf(this.ctx.skills.realLevel(SimpleSkills.Skills.PRAYER)) - (Integer.valueOf(this.ctx.skills.level(SimpleSkills.Skills.PRAYER)));
		
		if(prayerlvl > 30) {
			SimpleItem prayerpot = ctx.inventory.populate().filter(p -> p.getName().contains("Sanfew")).next();
			if(prayerpot != null && prayerpot.click(0)) {
				ctx.sleep(500);
				ctx.onCondition(() -> Integer.valueOf(this.ctx.skills.realLevel(SimpleSkills.Skills.PRAYER)) - (Integer.valueOf(this.ctx.skills.level(SimpleSkills.Skills.PRAYER))) <= 30, 1000);
			}else {
				SimpleItem restorepot = ctx.inventory.populate().filter(p -> p.getName().contains("restore")).next();
				if(restorepot != null && restorepot.click(0)) {
					ctx.sleep(500);
					ctx.onCondition(() -> Integer.valueOf(this.ctx.skills.realLevel(SimpleSkills.Skills.PRAYER)) - (Integer.valueOf(this.ctx.skills.level(SimpleSkills.Skills.PRAYER))) <= 30, 1000);
				}
			}
		}
	}

	private void setPrayer() {
		if(main.useOffensivePrayer) {
			if(main.usePrayer > 0) {
				if(main.usePrayer == 1) {
					setPrayer(Prayers.PIETY);
				}
				if(main.usePrayer == 2) {
					setPrayer(Prayers.RIGOUR);
				}
				if(main.usePrayer == 3) {
					setPrayer(Prayers.EAGLE_EYE);
				}
			}
		}
		setPrayer(Prayers.PROTECT_FROM_MELEE);
	}
	
	private void setPrayer(Prayers p) {
		if(!isPrayerOn(p)) {
			ctx.prayers.prayer(p);
		}
	}
	
	private boolean isPrayerOn(Prayers p) {
		return ctx.prayers.prayerActive(p);
	}
	
	private boolean isPrayerRight() {
		if(this.isPrayerOn(Prayers.PROTECT_FROM_MELEE)) {
			return true;
		}
		return false;
	}
	
	private void openCasket() {
		SimpleItem casket = ctx.inventory.populate().filter(p -> p.getName().contains("casket")).next();
		if(casket != null && casket.click(0)) {
			ctx.sleep(500);
		}
	}

	private boolean lootOnGround() {
		SimpleEntityQuery<SimpleGroundItem> lootation = ctx.groundItems.populate().filter(main.lootName.stream().toArray(String[]::new));
		if(lootation.size() > 0) {
			return true;
		}
		return false;
	}

	private void lootItem() {
		SimpleEntityQuery<SimpleGroundItem> lootation = ctx.groundItems.populate().filter(main.lootName.stream().toArray(String[]::new));
		if(lootation.size() > 0) {
			SimpleGroundItem item = lootation.nearest().next();
	    	if(item != null) {
	    		if(item.validateInteractable() && item.turnTo()) {
		        	if(item.click("Take")) {
		        		ctx.sleep(1000);
		        	}
	    		}
	    	}
		}
	}


	
}
