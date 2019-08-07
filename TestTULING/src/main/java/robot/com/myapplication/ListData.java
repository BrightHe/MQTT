package robot.com.myapplication;

import robot.com.myapplication.recorder.Recorder;

public class ListData extends Recorder{
	public static final int SEND = 1;
	public static final int RECEIVE = 2;

	public static final int TEXT = 1;
	public static final int IMAGE = 2;
	public static final int RECORDER = 3;

	private String fromWho;
	private String toUser;
	private String text_content;
	private int flag;
	private String publishTime;
	private int infType;
	private String picPath;
	private String amrFilePath;
	private String localPicPath;

	private int request_type;//聊天消息

	/**
	 * 初始化
	 */
	public ListData(){}

	/**
	 * 语音发送
	 */
	public ListData(float time,String filePath,String fromWho, String toUser, int flag, String publishTime, int infType,int request_type) {
		super(time,filePath);
		this.fromWho = fromWho;
		this.toUser = toUser;
		this.flag = flag;
		this.publishTime = publishTime;
		this.infType = infType;
		this.request_type = request_type;
	}

	/**
	 * 图片发送
	 */
	public ListData(String fromWho, String toUser, int flag, String publishTime,int infType,int request_type) {
		this.fromWho = fromWho;
		this.toUser = toUser;
		this.flag = flag;
		this.publishTime = publishTime;
		this.infType = infType;
		this.request_type = request_type;
	}


	/**
	 * 文本发送
	 */
	public ListData(String fromWho, String toUser, String text_content, int flag, String publishTime, int infType,int request_type) {
		this.fromWho = fromWho;
		this.toUser = toUser;
		this.text_content = text_content;
		this.flag = flag;
		this.publishTime = publishTime;
		this.infType = infType;
		this.request_type = request_type;
	}

	public int getRequest_type() {
		return request_type;
	}

	public void setRequest_type(int request_type) {
		this.request_type = request_type;
	}

	public String getLocalPicPath() {
		return localPicPath;
	}

	public void setLocalPicPath(String localPicPath) {
		this.localPicPath = localPicPath;
	}

	public String getContent() {
		return text_content;
	}

	public void setContent(String content) {
		this.text_content= content;
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

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public String getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}

	public int getInfType() {
		return infType;
	}

	public void setInfType(int infType) {
		this.infType = infType;
	}

	public String getPicPath() {
		return picPath;
	}

	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}

	public String getAmrFilePath() {
		return amrFilePath;
	}

	public void setAmrFilePath(String amrFilePath) {
		this.amrFilePath = amrFilePath;
	}

	@Override
	public String toString() {
		return "ListData{" +
				"fromWho='" + fromWho + '\'' +
				", toUser='" + toUser + '\'' +
				", text_content='" + text_content + '\'' +
				", flag=" + flag +
				", publishTime='" + publishTime + '\'' +
				", infType=" + infType +
				", picPath='" + picPath + '\'' +
				", amrFilePath='" + amrFilePath + '\'' +
				", localPicPath='" + localPicPath + '\'' +
				", request_type=" + request_type +
				'}';
	}
}
