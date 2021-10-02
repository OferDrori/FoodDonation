package dts.boundaries;

public class ItemIdBoundary {
	
	private String space;
    private String id;


    public ItemIdBoundary() {
    }

    public ItemIdBoundary(String space, String id) {
        this.space = space;
        this.id = id;
    }

    public String getSpace() {
        return space;
    }

    public void setSpace(String space) {
        this.space = space;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ItemId{" +
                "space='" + space + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
