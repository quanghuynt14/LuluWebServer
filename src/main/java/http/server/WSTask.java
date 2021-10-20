package http.server;

import java.sql.Date;

public class WSTask {
    private int taskid;
    private String content;
    private Date created_at;

    public WSTask(int taskid, String content, Date created_at) {
        this.taskid = taskid;
        this.content = content;
        this.created_at = created_at;
    }

    public int getTaskid() {
        return taskid;
    }

    public void setTaskid(int taskid) {
        this.taskid = taskid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
}
