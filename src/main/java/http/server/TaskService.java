package http.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskService {
    Connection connectionBD;

    public TaskService(Connection connectionBD) {
        this.connectionBD = connectionBD;
    }

    public void create_table_if_not_exists() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS `tasks`";
        sql += " (taskid INTEGER PRIMARY KEY AUTOINCREMENT, ";
        sql += " content TEXT, ";
        sql += " created_at DATETIME);";
        Statement statement = connectionBD.createStatement();
        statement.executeUpdate(sql);

        System.out.println("Table tasks created");
    }

    public List<WSTask> getAllTasks() throws SQLException {
        String sql = "SELECT * FROM tasks";
        sql += " ORDER BY created_at";
        Statement statement = connectionBD.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        List<WSTask> tasks = new ArrayList<>();
        while (resultSet.next()) {
            int taskid = resultSet.getInt("taskid");
            String content = resultSet.getString("content");
            Date created_at = resultSet.getDate("created_at");

            WSTask task = new WSTask(taskid, content, created_at);
            tasks.add(task);
        }

        return tasks;
    }

    public void insert_task(String content) throws SQLException {
        String sql = "INSERT INTO tasks (content, created_at) VALUES ";
        sql += "('" + content + "', ";
        sql += "datetime());";
        Statement statement = connectionBD.createStatement();
        int rows = statement.executeUpdate(sql);

        if (rows > 0) {
            System.out.println("A task inserted to database");
        }
    }

    public void update_task(int taskid, String content) throws SQLException {
        String sql = "UPDATE tasks SET ";
        sql += "content = '" + content + "' ";
        sql += "WHERE taskid = " + taskid;
        Statement statement = connectionBD.createStatement();
        int rows = statement.executeUpdate(sql);

        if (rows > 0) {
            System.out.println("A task updated");
        }
    }

    public void delete_task(int taskid) throws SQLException {
        String sql = "DELETE FROM tasks WHERE taskid = ";
        sql += taskid;
        Statement statement = connectionBD.createStatement();
        int rows = statement.executeUpdate(sql);

        if (rows > 0) {
            System.out.println("A task deleted");
        }
    }
}
