package in.wolfpack.notifybits;

public class Product {
    private String code;
    private String number;
    private String title;
    private int lecture;
    private int tutorial;
    private int practical;
    private String sec;
    private String inst;
    private String room;
    private String days;
    private String hour;
    private String ch;
    private String compre;

    public Product()
    {

    }
    public Product(String code, String number, String title, int lecture, int practical, int tutorial, String sec, String inst, String room, String days, String hour, String ch, String compre) {
        this.code = code;
        this.number = number;
        this.title = title;
        this.lecture = lecture;
        this.tutorial = tutorial;
        this.practical = practical;
        this.sec = sec;
        this.inst = inst;
        this.room = room;
        this.days = days;
        this.hour = hour;
        this.ch = ch;
        this.compre = compre;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getLecture() {
        return lecture;
    }

    public void setLecture(int lecture) {
        this.lecture = lecture;
    }

    public int getTutorial() {
        return tutorial;
    }

    public void setTutorial(int tutorial) {
        this.tutorial = tutorial;
    }

    public int getPractical() {
        return practical;
    }

    public void setPractical(int practical) {
        this.practical = practical;
    }

    public String getSec() {
        return sec;
    }

    public void setSec(String sec) {
        this.sec = sec;
    }

    public String getInst() {
        return inst;
    }

    public void setInst(String inst) {
        this.inst = inst;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getCh() {
        return ch;
    }

    public void setCh(String ch) {
        this.ch = ch;
    }

    public String getCompre() {
        return compre;
    }

    public void setCompre(String compre) {
        this.compre = compre;
    }

    public String toString()
    {
        String s="";
        if(this.getLecture()==1)
        {
            s+=this.getNumber()+";" +this.getRoom() +";"+this.getDays()+";"+this.getHour()+";"+this.getTitle()+";L";
        }
        if(this.getTutorial()==1)
        {
            s+=this.getNumber()+";" +this.getRoom()+";"+this.getDays()+";"+this.getHour()+";"+this.getTitle()+";T";
        }
        if(this.getPractical()==1)
        {
            s+=this.getNumber()+";" +this.getRoom()+";"+this.getDays()+";"+this.getHour()+";"+this.getTitle()+";P";
        }
        return s;
    }
}
