import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.*;

public class DBAccess {
    private String user = "root";
    private String password = "Ammadq87";
    private String url = "jdbc:mysql://localhost:3306/sys";
    private Connection connection = null;
    private String tables[][] = { { "users", "uid" }, { "events", "eventID" } };
    Messages msg = new Messages();
    CommandUtil cu = new CommandUtil();
    static User loggedInUser = new User();

    public DBAccess() {
        connectToDB();
    }

    public void connectToDB() {
        try {
            connection = DriverManager.getConnection(this.url, this.user, this.password);
        } catch (SQLException e) {
            System.out.println("Connection Failed");
            e.printStackTrace();
        }
    }

    public int getPrimaryKey(String table) {
        String column = "";
        for (int i = 0; i < this.tables.length; i++) {
            if (this.tables[i][0].equals(table)) {
                column = this.tables[i][1];
            }
        }

        if (column.isEmpty() || column.isBlank() || table.isEmpty() || table.isBlank())
            return -1;

        Map<Integer, List<Object>> results = FetchResults(
                "SELECT " + column + " FROM " + table + " ORDER BY " + column + " DESC", column + "-i");
        if (results.isEmpty())
            return -1;
        Integer max = 0;
        for (Integer id : results.keySet()) {
            if (id > max) {
                max = id;
            }
        }
        List<Object> eventID = results.get(max);
        int id = (eventID.isEmpty() || max == 0 ? -1 : (Integer) eventID.get(0));
        return id;
    }

    // Create, Update, Delete
    public void ExecuteQuery(String sql, int CRUD) {
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(sql);
            this.loggedInUser.accountCreated = true;
        } catch (SQLIntegrityConstraintViolationException e) {
            try {
                Statement s = connection.createStatement();
                s.executeUpdate(sql);
            } catch (SQLIntegrityConstraintViolationException ex) {
                msg.Print(msg.GetErrorMessage("lblAccountExists", null), 'e');
                this.loggedInUser.accountCreated = false;
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            msg.Print(msg.GetErrorMessage("lblAccountExists", null), 'e');
            this.loggedInUser.accountCreated = false;

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Read/Print
    public Map<Integer, List<Object>> FetchResults(String sql, String... columns) {
        try {
            String text[] = sql.split(" ");
            String primaryKey = "";
            for (int i = 0; i < text.length; i++) {
                for (int j = 0; j < this.tables.length; j++) {
                    if (i != j && text[i].equals(this.tables[j][0])) {
                        primaryKey = this.tables[j][1];
                    }
                }
            }

            if (primaryKey.isBlank() || primaryKey.isEmpty())
                return null;

            Statement s = connection.createStatement();
            ResultSet r = s.executeQuery(sql);
            return FetchResultsHelper(r, primaryKey, columns);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private Map<Integer, List<Object>> FetchResultsHelper(ResultSet r, String primaryKey, String... columns) {
        Map<Integer, List<Object>> results = new HashMap<Integer, List<Object>>();
        try {

            while (r.next()) {
                List<Object> values = new ArrayList<>();
                for (int j = 0; j < columns.length; j++) {
                    char type = columns[j].charAt(columns[j].lastIndexOf('-') + 1);
                    String columnLabel = columns[j].substring(0, columns[j].indexOf('-'));
                    switch (type) {
                        case 'i':
                            int i = r.getInt(columnLabel);
                            values.add(r.getInt(columnLabel));
                            break;
                        case 'b':
                            values.add(r.getBoolean(columnLabel));
                            break;
                        case 'd':
                            values.add(r.getDouble(columnLabel));
                            break;
                        case 's':
                            values.add(r.getString(columnLabel));
                            break;
                    }
                }

                results.put(r.getInt(primaryKey), values);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return results;
    }
}