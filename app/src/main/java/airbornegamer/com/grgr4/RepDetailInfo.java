package airbornegamer.com.grgr4;

public class RepDetailInfo {

    public String id;
    public String state;
    public String party;
    public String title;
    public String firstName;
    public String lastName;
    public boolean isUserRepresentative;
//    public String email;
//    public int phoneNumber;

    public RepDetailInfo(String id, String state, String party, String title, String firstName, String lastName, boolean isUserRepresentative){
        super();
        this.id = id;
        this.state = state;
        this.party = party;
        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isUserRepresentative = isUserRepresentative;
    }
}
