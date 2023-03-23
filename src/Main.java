import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.*;
import java.nio.file.*;
import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            try (Connection conn = getConnection()){

                System.out.println("Connection to lesson2 DB succesfull!");
                Statement statement = conn.createStatement();
                String sqlCommand;
                // создание таблицы
                sqlCommand = "CREATE TABLE  IF NOT EXISTS sales (" +
                        "  idSales INT NOT NULL AUTO_INCREMENT," +
                        "  product VARCHAR(45) NOT NULL," +
                        "  price DECIMAL(8,2) NOT NULL," +
                        "  quantity DECIMAL(8,2) NOT NULL," +
                        " PRIMARY KEY (idSales));";
                statement.executeUpdate(sqlCommand);
                // очистка таблицы sales
                sqlCommand = "TRUNCATE TABLE sales;";
                statement.executeUpdate(sqlCommand);
                // Заполнение таблицы
                sqlCommand = "INSERT INTO lesson2.sales (product, price, quantity) VALUES" +
                        " ('iPhone 8', 18, 6)," +
                        " ('iPhone X', 76, 5)," +
                        "('Galaxy S9', 56, 6)," +
                        "('Galaxy S8', 11, 8)," +
                        "('P20 Pro', 36, 2);";
                int rows = statement.executeUpdate(sqlCommand);
                System.out.printf("Добавлено %d строк", rows);

                // Получить результат выборки
                sqlCommand = "SELECT idSales, product, price, quantity, price*quantity as sum," +
                                " CASE"  +
                                "   WHEN price*quantity < 100 THEN '1. < 100'" +
                                "   WHEN price*quantity BETWEEN 100 and 300 THEN '2. 100-300'" +
                                "   WHEN price*quantity > 300 THEN '3. > 300'" +
                                " END AS qv" +
                                " FROM sales" +
                                " ORDER BY qv;";
                ResultSet resultSet = statement.executeQuery(sqlCommand);
                System.out.printf("\nId\tТовар\t\t\tСтоимость(т.р.)\t\tДиапазон\n" );
                while(resultSet.next()) {
                    int idSales = resultSet.getInt("idSales");
                    String product = resultSet.getString("product");
                    Double price = resultSet.getDouble("price");
                    Double quantity = resultSet.getDouble("quantity");
                    Double sum = resultSet.getDouble("sum");
                    String qv = resultSet.getString("qv");
                    System.out.printf("%d. %-10s \t %.2f*%.2f=%.2f \t %s\n", idSales, product, price, quantity, sum, qv);
                }
            }
        }
        catch(Exception ex){
            System.out.println("Connection failed...");
            System.out.println(ex);
        }
    }

    public static Connection getConnection() throws SQLException, IOException{

        Properties props = new Properties();
        try(InputStream in = Files.newInputStream(Paths.get("database.properties"))){
            props.load(in);
        }
        String url = props.getProperty("url");
        String username = props.getProperty("username");
        String password = props.getProperty("password");

        return DriverManager.getConnection(url, username, password);
    }
}
