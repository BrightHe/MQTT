package robot.com.myapplication.pal;

import java.io.Serializable;
import java.util.Arrays;

public class NewFriend implements Serializable {
    private byte[] userImage;//用户头像
    private String userWrite;//用户签名
    private String userName;//用户名
    private String sex;
    private String beizhu;
    private String wherefrom;
    private int friendImage;//用户头像
    private String fromWho;//谁发来的
    private String toUser; //发给谁的

    //添加好友用到的词条
    private String content;//内容（同意或添加好友）
    private String sigature;//签名
    private int AgreeOrRefuse;//同意或拒绝

    private int request_type;//好友请求

    public NewFriend() {
    }

    //初始化推荐好友
    public NewFriend(String userName,String sigature,String sex,String wherefrom,int friendImage){
        this.userName = userName;
        this.sigature = sigature;
        this.sex = sex;
        this.wherefrom = wherefrom;
        this.friendImage = friendImage;
    }

    //初始化用户信息
    public NewFriend(String userName,String sex,String sigature,String wherefrom,int friendImage,String content) {
        this.userName = userName;
        this.sex = sex;
        this.sigature = sigature;
        this.wherefrom = wherefrom;
        this.friendImage = friendImage;
        this.content = content;
    }

    //初始化发送请求时的用户信息，添加发送者以及接收对象
    public NewFriend(String userName,String sex,String sigature,String wherefrom,int friendImage,String fromWho,String toUser,int request_type) {
        this.userName = userName;
        this.sex = sex;
        this.sigature = sigature;
        this.wherefrom = wherefrom;
        this.friendImage = friendImage;
        this.fromWho = fromWho;
        this.toUser = toUser;
        this.request_type = request_type;
    }

    //初始化用户信息，添加同意或者拒绝标志以及发送者和接收对象
    public NewFriend(String userName,String sex,String sigature,String wherefrom,int friendImage,int AgreeOrRefuse,String fromWho,String toUser,int request_type) {
        this.userName = userName;
        this.sex = sex;
        this.sigature = sigature;
        this.wherefrom = wherefrom;
        this.friendImage = friendImage;
        this.AgreeOrRefuse = AgreeOrRefuse;
        this.fromWho = fromWho;
        this.toUser = toUser;
        this.request_type = request_type;
    }

    public int getRequest_type() {
        return request_type;
    }

    public void setRequest_type(int request_type) {
        this.request_type = request_type;
    }

    public byte[] getUserImage() {
        return userImage;
    }

    public void setUserImage(byte[] userImage) {
        this.userImage = userImage;
    }

    public String getUserWrite() {
        return userWrite;
    }

    public void setUserWrite(String userWrite) {
        this.userWrite = userWrite;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBeizhu() {
        return beizhu;
    }

    public void setBeizhu(String beizhu) {
        this.beizhu = beizhu;
    }

    public String getWherefrom() {
        return wherefrom;
    }

    public void setWherefrom(String wherefrom) {
        this.wherefrom = wherefrom;
    }

    public int getFriendImage() {
        return friendImage;
    }

    public void setFriendImage(int friendImage) {
        this.friendImage = friendImage;
    }

    public String getFromWho() {
        return fromWho;
    }

    public void setFromWho(String fromWho) {
        this.fromWho = fromWho;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUsernName() {
        return userName;
    }

    public void setUsernName(String usernName) {
        this.userName = usernName;
    }

    public String getSigature() {
        return sigature;
    }

    public void setSigature(String sigature) {
        this.sigature = sigature;
    }

    public int getAgreeOrRefuse() {
        return AgreeOrRefuse;
    }

    public void setAgreeOrRefuse(int agreeOrRefuse) {
        AgreeOrRefuse = agreeOrRefuse;
    }

    @Override
    public String toString() {
        return "NewFriend{" +
                "userImage=" + Arrays.toString( userImage ) +
                ", userWrite='" + userWrite + '\'' +
                ", userName='" + userName + '\'' +
                ", sex='" + sex + '\'' +
                ", beizhu='" + beizhu + '\'' +
                ", wherefrom='" + wherefrom + '\'' +
                ", friendImage=" + friendImage +
                ", fromWho='" + fromWho + '\'' +
                ", toUser='" + toUser + '\'' +
                ", content='" + content + '\'' +
                ", sigature='" + sigature + '\'' +
                ", AgreeOrRefuse=" + AgreeOrRefuse +
                ", request_type=" + request_type +
                '}';
    }
}
