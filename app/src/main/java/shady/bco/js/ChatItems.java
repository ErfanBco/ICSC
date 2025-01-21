package shady.bco.js;

public class ChatItems {
    private String Message;
    private String Address;
    private String Person;
    private long Date;
    private int IsRead;


    public ChatItems(String message, String address, String person, long date, int isRead) {
        Message = message;
        Address = address;
        Person = person;
        Date = date;
        IsRead = isRead;
    }


    public int getIsRead() {
        return IsRead;
    }

    public void setIsRead(int isRead) {
        IsRead = isRead;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getPerson() {
        return Person;
    }

    public void setPerson(String person) {
        Person = person;
    }

    public long getDate() {
        return Date;
    }

    public void setDate(long date) {
        Date = date;
    }



}
