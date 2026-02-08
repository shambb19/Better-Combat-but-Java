package combatants;

public class NPC extends Combatant{

    protected String name;

    protected int initiative;

    protected int hpMax;
    protected int hpCurrent;
    protected LifeStatus lifeStatus = new LifeStatus();

    protected int armorClass;

    protected boolean isEnemy;

    public NPC(String name, int hpMax, int armorClass, final boolean isEnemy) {
        super(name, hpMax, armorClass, isEnemy);
    }
}
