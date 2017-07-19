import java.sql.*;
import java.util.Scanner;
import oracle.jdbc.*;

/**
 * Created by abrammetzgar on 11/24/14.
 */
public class GetPayment {


    public static Connection connect()
    {
        Connection conn = null;

        String url = "jdbc:oracle:oci:@ORCL";
        try
        {
            DriverManager.registerDriver(new OracleDriver());
            conn = DriverManager.getConnection(url);
        }
        catch(SQLException e)
        {
            System.out.println("Error with drivers or getting a connection -- exiting");
            System.exit(99); // the number means nothing -- exits the program
        }

        if(conn == null)
            return null;
        else
            return conn;  /* can we say return conn ? */
    }// connect

    public static void main(String[] args) throws SQLException {

        int sid;
        double payment;

        //Create scanner for user input
        Scanner sc = new Scanner(System.in);

        //Prompt user for student id
        System.out.print("Enter the student id: ");
        sid = sc.nextInt();
        System.out.println();

        //Prompt for student payment
        System.out.print("Enter the payment amount: ");
        payment = sc.nextDouble();
        System.out.println();

        //Create new pmt object
        GetPayment pmt = new GetPayment();

        //Run the payment method
        pmt.recordPayment(sid, payment);

    }

    public void recordPayment(int sid, double payment) throws SQLException {

        try {

            String updateString = "UPDATE student\n" +
                    "SET amtdue = amtdue - ?\n" +
                    "WHERE id = ?";

            //Make connection
            Connection conn = connect();
            conn.setAutoCommit(false);

            //Get prepared statement
            PreparedStatement ps = conn.prepareStatement(updateString);

            //Fill in parameters
            ps.setDouble(1, payment);
            ps.setInt(2, sid);

            //Execute update
            int n = ps.executeUpdate();

            //Check to see if update was successful
            if (n > 0) {
                System.out.print("Record update SUCCESSFUL");
            } else {
                System.out.print("Record update UNSUCCESSFUL");
            }

            ps.close();
            conn.close();
        } catch (SQLException e) {
            System.out.print("SQL Exception raised!");

            while (e != null) {
                System.out.print("Error Message: " + e.getMessage());
                System.out.print("SQL State: " + e.getSQLState());
                System.out.print("Error Code: " + e.getErrorCode());
                e = e.getNextException();
            }
        }
    }
}
