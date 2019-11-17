package ekstra;

public class Item
{
    private int Id;
    private float Price;
    private String Name, Category, Description;

    public int getId() {
        return Id;
    }

    public float getPrice() {
        return Price;
    }

    public String getName() {
        return Name;
    }

    public String getCategory() {
        return Category;
    }

    public String getDescription() {
        return Description;
    }

    public void setId(int id) {
        Id = id;
    }

    public void setPrice(float price) {
        Price = price;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
