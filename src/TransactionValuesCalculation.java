import java.util.List;

public class TransactionValuesCalculation {
    public static Double getTotalIncomes(List<Transaction> transactions) {
        double totalIncome = 0.0;
        for (Transaction transaction : transactions) {
            if("Income".equals(transaction.getType())) {
                totalIncome += transaction.getAmount();
            }
        }

        return totalIncome;
    }

    public static Double getTotalExpenses(List<Transaction> transactions) {
        double totalExpenses = 0.0;
        for (Transaction transaction : transactions) {
            if("Expense".equals(transaction.getType())) {
                totalExpenses += transaction.getAmount();
            }
        }

        return totalExpenses;
    }

    public static Double getTotalValue(List<Transaction> transactions) {
        Double totalIncome = getTotalIncomes(transactions);
        Double totalExpense = getTotalExpenses(transactions);
        return totalIncome - totalExpense;
    }
}