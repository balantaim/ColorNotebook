package com.martinatanasov.colornotebook.model;

public class UserEvent {
    String txtEventId;
    String txtEventTitle;
    String txtEventLocation;
    String txtNode;
    int int_color_picker;
    int int_avatar_picker;
    int int_start_year;
    int int_end_year;
    int int_all_day;
    int int_sound_notifications;
    int int_silent_notifications;
    byte byte_start_month;
    byte byte_start_day;
    byte byte_start_hour;
    byte byte_start_minutes;
    byte byte_end_month;
    byte byte_end_day;
    byte byte_end_hour;
    byte byte_end_minutes;
    long long_created_date;
    long long_modified_date;

    public UserEvent(String txtEventId, String txtEventTitle, String txtEventLocation, String txtNode, int int_color_picker, int int_avatar_picker, int int_start_year, int int_end_year, int int_all_day, int int_sound_notifications, int int_silent_notifications, byte byte_start_month, byte byte_start_day, byte byte_start_hour, byte byte_start_minutes, byte byte_end_month, byte byte_end_day, byte byte_end_hour, byte byte_end_minutes, long long_created_date, long long_modified_date) {
        this.txtEventId = txtEventId;
        this.txtEventTitle = txtEventTitle;
        this.txtEventLocation = txtEventLocation;
        this.txtNode = txtNode;
        this.int_color_picker = int_color_picker;
        this.int_avatar_picker = int_avatar_picker;
        this.int_start_year = int_start_year;
        this.int_end_year = int_end_year;
        this.int_all_day = int_all_day;
        this.int_sound_notifications = int_sound_notifications;
        this.int_silent_notifications = int_silent_notifications;
        this.byte_start_month = byte_start_month;
        this.byte_start_day = byte_start_day;
        this.byte_start_hour = byte_start_hour;
        this.byte_start_minutes = byte_start_minutes;
        this.byte_end_month = byte_end_month;
        this.byte_end_day = byte_end_day;
        this.byte_end_hour = byte_end_hour;
        this.byte_end_minutes = byte_end_minutes;
        this.long_created_date = long_created_date;
        this.long_modified_date = long_modified_date;
    }

    public String getTxtEventId() {
        return txtEventId;
    }

    public void setTxtEventId(String txtEventId) {
        this.txtEventId = txtEventId;
    }

    public String getTxtEventTitle() {
        return txtEventTitle;
    }

    public void setTxtEventTitle(String txtEventTitle) {
        this.txtEventTitle = txtEventTitle;
    }

    public String getTxtEventLocation() {
        return txtEventLocation;
    }

    public void setTxtEventLocation(String txtEventLocation) {
        this.txtEventLocation = txtEventLocation;
    }

    public String getTxtNode() {
        return txtNode;
    }

    public void setTxtNode(String txtNode) {
        this.txtNode = txtNode;
    }

    public int getInt_color_picker() {
        return int_color_picker;
    }

    public void setInt_color_picker(int int_color_picker) {
        this.int_color_picker = int_color_picker;
    }

    public int getInt_avatar_picker() {
        return int_avatar_picker;
    }

    public void setInt_avatar_picker(int int_avatar_picker) {
        this.int_avatar_picker = int_avatar_picker;
    }

    public int getInt_start_year() {
        return int_start_year;
    }

    public void setInt_start_year(int int_start_year) {
        this.int_start_year = int_start_year;
    }

    public int getInt_end_year() {
        return int_end_year;
    }

    public void setInt_end_year(int int_end_year) {
        this.int_end_year = int_end_year;
    }

    public int getInt_all_day() {
        return int_all_day;
    }

    public void setInt_all_day(int int_all_day) {
        this.int_all_day = int_all_day;
    }

    public int getInt_sound_notifications() {
        return int_sound_notifications;
    }

    public void setInt_sound_notifications(int int_sound_notifications) {
        this.int_sound_notifications = int_sound_notifications;
    }

    public int getInt_silent_notifications() {
        return int_silent_notifications;
    }

    public void setInt_silent_notifications(int int_silent_notifications) {
        this.int_silent_notifications = int_silent_notifications;
    }

    public byte getByte_start_month() {
        return byte_start_month;
    }

    public void setByte_start_month(byte byte_start_month) {
        this.byte_start_month = byte_start_month;
    }

    public byte getByte_start_day() {
        return byte_start_day;
    }

    public void setByte_start_day(byte byte_start_day) {
        this.byte_start_day = byte_start_day;
    }

    public byte getByte_start_hour() {
        return byte_start_hour;
    }

    public void setByte_start_hour(byte byte_start_hour) {
        this.byte_start_hour = byte_start_hour;
    }

    public byte getByte_start_minutes() {
        return byte_start_minutes;
    }

    public void setByte_start_minutes(byte byte_start_minutes) {
        this.byte_start_minutes = byte_start_minutes;
    }

    public byte getByte_end_month() {
        return byte_end_month;
    }

    public void setByte_end_month(byte byte_end_month) {
        this.byte_end_month = byte_end_month;
    }

    public byte getByte_end_day() {
        return byte_end_day;
    }

    public void setByte_end_day(byte byte_end_day) {
        this.byte_end_day = byte_end_day;
    }

    public byte getByte_end_hour() {
        return byte_end_hour;
    }

    public void setByte_end_hour(byte byte_end_hour) {
        this.byte_end_hour = byte_end_hour;
    }

    public byte getByte_end_minutes() {
        return byte_end_minutes;
    }

    public void setByte_end_minutes(byte byte_end_minutes) {
        this.byte_end_minutes = byte_end_minutes;
    }

    public long getLong_created_date() {
        return long_created_date;
    }

    public void setLong_created_date(long long_created_date) {
        this.long_created_date = long_created_date;
    }

    public long getLong_modified_date() {
        return long_modified_date;
    }

    public void setLong_modified_date(long long_modified_date) {
        this.long_modified_date = long_modified_date;
    }
}
