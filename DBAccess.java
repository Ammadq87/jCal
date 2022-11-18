
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.*;

public class DBAccess {
    private static Connection connection = null;

    public DBAccess() {

    }

    public static int connectToDB() {
        String connect[] = { "jdbc:mysql://localhost:3306/sys", "root", "Ammadq87" };
        try {
            connection = DriverManager.getConnection(connect[0], connect[1], connect[2]);
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * Executes SQL Query based on SQL string and returns success message
     * 
     * ToDo: Created a sleep thread to verify login
     * 
     * @param sql  - SQL Query to be executed
     * @param CRUD - CRUD operation to be executed
     * @return returns CRUD if operation was successful
     */
    public static int ExecuteQuery(String sql, int CRUD) {
        if (connectToDB() == 0) {
            try {
                Statement s = connection.createStatement();
                s.executeUpdate(sql);
                return CRUD;

            } catch (SQLIntegrityConstraintViolationException e) {
                Messages.printMessage(Messages.getErrorMessage("lblAccountExists", sql), 'e');
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return -1;
        }
        return 1;

    }

    /**
     * Get a Mapping of the results based on SQL string and required columns
     * 
     * @param sql     - SQL Query to be executed (Select Statement)
     * @param columns - List of required columns
     * @return a Mapping between the Id and Object from SQL Query
     */
    public static Map<Integer, List<Object>> FetchResults(String sql, String... columns) {
        if (connectToDB() == 0) {
            try {
                String text[] = sql.split(" ");
                String primaryKey = "";
                for (int i = 0; i < text.length; i++) {
                    for (int j = 0; j < CommandUtil.getTables().length; j++) {
                        if (i != j && text[i].equals(CommandUtil.getTables()[j][0])) {
                            primaryKey = CommandUtil.getTables()[j][1];
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
        return null;

    }

    /**
     * Helper method to retrieve mapping between Id and Object
     * 
     * @param r          - Select query result set
     * @param primaryKey - Table to access
     * @param columns    - Required columns to select data from
     * @return a Mapping between the Id and Object from SQL Query
     */
    private static Map<Integer, List<Object>> FetchResultsHelper(ResultSet r, String primaryKey, String... columns) {
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
                        case 't': // TimeStamp
                            values.add(r.getDate(columnLabel));
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
