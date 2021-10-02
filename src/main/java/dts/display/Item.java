package dts.display;

public class Item {
    private ItemId itemId;

    public Item() {
     
    }
    
    public Item (String space, String id) {
    	this.itemId = new ItemId(space, id);
    }
    
    public Item(dts.display.ItemId itemId) {
		super();
		this.itemId = itemId;
	}

    public ItemId getItemId() {
        return itemId;
    }

    public void setItemId(ItemId itemId) {
        this.itemId = itemId;
    }
    
}
