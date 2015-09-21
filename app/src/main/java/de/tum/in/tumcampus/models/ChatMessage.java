package de.tum.in.tumcampus.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.tum.in.tumcampus.R;
import de.tum.in.tumcampus.auxiliary.Utils;


@SuppressWarnings("UnusedDeclaration")
public class ChatMessage {

    public static final int STATUS_SENDING = 1;
    public static final int STATUS_SENT = 0;
    public static final int STATUS_SENDING_FAILED = -1;

    private int id;
	private String text;
	private ChatMember member;
	private String timestamp;
	private String signature;
    private int sendingStatus;
    private int previous;
    private int room;
    private boolean read;
    public int internalID;

    /**
     * Default constructor: called by gson when parsing an element
     */
    public ChatMessage(){
        this.sendingStatus=STATUS_SENT;
    }
	
	public ChatMessage(String text) {
		super();
		this.text = text;
	}

    /**
     * Called when creating a new chat message
     * @param text Chat message text
     * @param member Member who sent the message
     */
    public ChatMessage(String text, ChatMember member) {
        super();
        this.text = text;
        this.member = member;
        this.sendingStatus = STATUS_SENDING;
        this.previous = 0;
        this.setNow();
    }

    public int getRoom() {
        return room;
    }

    public void setRoom(int room) {
        this.room = room;
    }

    public int getStatus() {
        return sendingStatus;
    }

    public void setStatus(int status) {
        sendingStatus = status;
    }

	public ChatMessage(int id, String text, ChatMember member, String timestamp, int previous) {
		super();
        this.id = id;
		this.text = text;
		this.member = member;
		this.timestamp = timestamp;
        this.sendingStatus = STATUS_SENT;
        this.previous = previous;
	}

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getPrevious() {
        return previous;
    }
    public void setPrevious(int previous) {
        this.previous = previous;
    }
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public ChatMember getMember() {
		return member;
	}
	public void setMember(ChatMember member) {
		this.member = member;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
    public Date getTimestampDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        Date time = new Date();
        try {
            time = formatter.parse(this.getTimestamp());
        } catch (ParseException e) {
            Utils.log(e);
        }
        return time;
    }


	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	
	private static boolean isToday(Date date) {
		Calendar passedDate = Calendar.getInstance();
		passedDate.setTime(date); // your date
		
		Calendar today = Calendar.getInstance(); // today

        return today.get(Calendar.YEAR) == passedDate.get(Calendar.YEAR) && today.get(Calendar.DAY_OF_YEAR) == passedDate.get(Calendar.DAY_OF_YEAR);
    }
	
	private static boolean isYesterday(Date date) {
		Calendar passedDate = Calendar.getInstance();
		passedDate.setTime(date);
		
		Calendar yesterday = Calendar.getInstance(); // today
		yesterday.add(Calendar.DAY_OF_YEAR, -1); // yesterday

        return yesterday.get(Calendar.YEAR) == passedDate.get(Calendar.YEAR) && yesterday.get(Calendar.DAY_OF_YEAR) == passedDate.get(Calendar.DAY_OF_YEAR);
    }

    public int getStatusStringRes() {
        if (sendingStatus==STATUS_SENT) {
            return R.string.status_sent;
        } else if (sendingStatus==STATUS_SENDING) {
            return R.string.status_sending;
        } else {
            return R.string.status_sending_failed;
        }
    }

    public void setNow() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH); // 2014-06-30T16:31:57.878Z
        timestamp = formatter.format(new Date());
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isRead() {
        return read;
    }

    public boolean getRead() {
        return read;
    }
}
