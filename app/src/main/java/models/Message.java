package models;

import com.google.type.DateTime;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Message {
    private String mText;
    private String mSender;

    private String mPhotoURL;
    private String mFileName;
    private String mFileURL;
    private Date mDate;

    public Message() {
    }

    public Message(String mText, String mSender, String mPhotoURL, String mFileName, String mFileURL, Date mDate) {
        this.mText = mText;
        this.mSender = mSender;
        this.mPhotoURL = mPhotoURL;
        this.mFileName = mFileName;
        this.mFileURL = mFileURL;
        this.mDate = mDate;
    }



    public String getText() {
        return mText;
    }

    public void setText(String mText) {
        this.mText = mText;
    }

    public String getSender() {
        return mSender;
    }

    public void setSender(String mSender) {
        this.mSender = mSender;
    }

    public String getmPhotoURL() {
        return mPhotoURL;
    }

    public void setmPhotoURL(String mPhotoURL) {
        this.mPhotoURL = mPhotoURL;
    }

    public String getmFileName() {
        return mFileName;
    }

    public void setmFileName(String mFileName) {
        this.mFileName = mFileName;
    }

    public String getmFileURL() {
        return mFileURL;
    }

    public void setmFileURL(String mFileURL) {
        this.mFileURL = mFileURL;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date mDate) {
        this.mDate = mDate;
    }

    public String getDateString(Date dateTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        // Set the time zone to Vietnam
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        return dateFormat.format(dateTime);
    }

}
