package stream.model;

public class User {
    private String userName;
    private int age;
    private String phone;
    private long time;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public User() {
    }

    public User(String userName, int age, String phone, long time) {


        this.userName = userName;
        this.age = age;
        this.phone = phone;
        this.time = time;
    }
}
