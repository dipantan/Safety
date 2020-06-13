package apollo.safety.Models;

/**
 * Created by Apollo on 5/14/2020.
 */

public class Contacts {
    private String name;
    private String number;
    private String id;
    public Contacts(){

    }
    public Contacts(String name, String number, String id){
        this.name=name;
        this.number=number;
        this.id=id;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getId(){
        return id;
    }
}
