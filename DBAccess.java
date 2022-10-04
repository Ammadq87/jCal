import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.*;

public class DBAccess {
    private Connection connection = null;
    private String tables[][] = { { "users", "uid" }, { "events", "eventID" } };
    private Messages msg = new Messages();
    private CommandUtil cu = new CommandUtil();

    public DBAccess() {
        connectToDB();

    }

    public void connectToDB() {
        String connect[] = { "jdbc:mysql://localhost:3306/sys", "root", "Ammadq87" };
        try {
            connection = DriverManager.getConnection(connect[0], connect[1], connect[2]);
        } catch (SQLException e) {
            System.out.println("Connection Failed");
            e.printStackTrace();
        }
    }

    // Create, Update, Delete
    public void ExecuteQuery(String sql, int CRUD) {
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(sql);
            msg.Print(msg.GetSuccessMessage("lblSuccess", null), 's');
        } catch (SQLIntegrityConstraintViolationException e) {
            msg.Print(msg.GetErrorMessage("lblAccountExists", null), 'e');
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
                        break;
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