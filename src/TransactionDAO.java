import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransactionDAO {
    public static List<Transaction> getAllTransaction() {
        List<Transaction> transactions = new ArrayList<>();
        Connection connection = DatabaseConnection.getConnection();
        if (connection == null) {
            return transactions;
        }

        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM `transaction_table`");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String type = rs.getString("transaction_type");
                String description = rs.getString("description");
                double amount = rs.getDouble("amount");

                Transaction transaction = new Transaction(id, type, description, amount);
                transactions.add(transaction);
            }
        } catch (SQLException ex) {
            Logger.getLogger(TransactionDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return transactions;
    }
}