package core;

public class Item {

    public Item(int itemID, String name, ItemDamage itemDamage, String description) {
        this.itemDamage = itemDamage;
        this.itemID = itemID;
        this.description = description;
        this.name = name;
    }

    public enum ItemDamage {
        NEW,
        GOOD,
        COSMETIC,
        HEAVY
    }
    private ItemDamage itemDamage;
    private int itemID;
    private String description;
    private String name;

    public ItemDamage getItemDamage() {
        return itemDamage;
    }

    public String getName() { return name; }

    public int getItemID() {
        return itemID;
    }

    public String getDescription() {
        return description;
    }

}
