import java.sql.*;
import java.util.Scanner;
import oracle.jdbc.*;


/**
 * Created by abrammetzgar on 11/24/14.
 */
public class Classlist {


    public static Connection connect()
    {
        Connection con = null;

        String url = "jdbc:oracle:oci:@ORCL";
        try
        {
            DriverManager.registerDriver(new OracleDriver());
            con = DriverManager.getConnection(url);
        }
        catch(SQLException e)
        {
            System.out.println("Error with drivers or getting a connection -- exiting");
            System.exit(99); // the number means nothing -- exits the program
        }

        if(con == null)
            return null;
        else
            return con;  /* can we say return conn ? */
    }// connect

    public static void main(String[] args) throws SQLException {

        String term;
        int sectionNum;

        //Create scanner to get user input
        Scanner sc = new Scanner(System.in);

        //Prompt user to get term
        System.out.print("Enter the Term: ");
        term = sc.next();
        System.out.println();

        //Prompt user to get section number
        System.out.print("Enter the Section number: ");
        sectionNum = sc.nextInt();
        System.out.println();

        //Create a list object
        Classlist list = new Classlist();

        //Call the printHeader method
        list.printHeader(term, sectionNum);

        //Call the printStuList method
        list.printStuList(term, sectionNum);
    }

    public void printHeader(String term, int sectionNum) throws SQLException {

        try {

            String dbTerm = term;
            int dbSecNum = sectionNum;
            String dbCnum = "";
            String dbTitle = "";
            String dbName = "";
            int n = 0;

            //Makes a new connection
            Connection conn = connect();

            conn.setAutoCommit(false);

            String queryString = "SELECT course.cnum, title, name\n" +
                    "FROM section, course, teacher\n" +
                    "WHERE term = ?" +
                    "AND secnum = ?" +
                    "AND section.cnum = course.cnum\n" +
                    "AND section.tid = teacher.tid";

            //get PreparedStatement
            PreparedStatement ps = conn.prepareStatement(queryString);

            //Fill in parameters
            ps.setString(1, term);
            ps.setInt(2, sectionNum);

            //Execute query
            ResultSet rs = ps.executeQuery();

            //Gets info from ResultSet
            if (rs.next()) {
                dbCnum = rs.getString(1);
                dbTitle = rs.getString(2);

                if (rs.wasNull()) {
                    dbName = "TBD";
                } else {
                    dbName = rs.getString(3);
                }
            }


            //Print out the Header
            System.out.println(dbTerm + " " + dbSecNum);
            System.out.println(dbCnum + " " + dbTitle);
            System.out.println(dbName);

            //Commit the operations
            conn.commit();

            //Close all connections
            rs.close();
            ps.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println("SQL Exception raised!");
            e.printStackTrace();

            while (e != null) {
                System.out.println("Error Message: " + e.getMessage());
                System.out.println("SQL State: " + e.getSQLState());
                System.out.println("Error Code: " + e.getErrorCode());
                e = e.getNextException();
            }
        }

    }

    public void printStuList(String term, int sectionNum) throws SQLException {

        try {

            int dbId = 0;
            String dbName = "";
            String dbGrade = "";


            //Makes a new connection
            Connection conn = connect();
            conn.setAutoCommit(false);

            String queryListString = "SELECT enrollment.id,student.name, grade\n" +
                    "from enrollment, student\n" +
                    "where term = ?\n" +
                    "AND secnum = ?\n" +
                    "AND enrollment.id = student.id";

            //Get prepared statement
            PreparedStatement ps = conn.prepareStatement(queryListString);

            //fill in parameters
            ps.setString(1, term);
            ps.setInt(2, sectionNum);

            //Execute query
            ResultSet rs = ps.executeQuery();

            //loop through ResultSet
            while (rs.next()){
                dbId = rs.getInt(1);
                dbName = rs.getString(2);

                if (rs.wasNull()) {
                    dbGrade = "-";
                } else {
                    dbGrade = rs.getString(3);
                }

                //Print out list
                System.out.println(dbId + " " + dbName + " " + dbGrade);
            }

            //Commit operations
            conn.commit();

            //Close all connections
            rs.close();
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
