import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main
{
    private final static String url ="jdbc:postgresql://localhost:5432/mini-project";
    private final static String user="student";
    private final static String password="C0d3Cr3w";
    //function to make connection to database
    public static java.sql.Connection connect() {
        Scanner read= new Scanner(System.in);
        int choose;
        java.sql.Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            conn = java.sql.DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.\n");
            System.out.println("Welcome to the football channel");
            while (true) //never-ending while loop
            {
                System.out.println("Press 1 to a list of all the matches.");
                System.out.println("Press 2 to add a goal.");
                try {        //tries to read an integer from nextInt()
                    choose = read.nextInt();
                    if (choose==1) {allMatches(conn);choose=0;} //if the user selects 1, it will call the function allMatches to list all the matches
                    else if(choose==2){addGoals(conn); choose=0;} // if the user selects 2, it will call a function to add goals
                    else{System.out.println("Please enter 1 or 2.");} // if a user enters an integer that is neither 1 nor 2,
                }
                catch(InputMismatchException e){System.out.println("Invalid input");read.nextLine();} //this catch the error if the user does not input an integer
            }

        } catch (java.sql.SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }

    public static void allMatches(Connection conn) { //returns all matches
      String SQL="Select * from game";
        try {
            PreparedStatement pstmt = conn.prepareStatement(SQL);
            ResultSet rs=pstmt.executeQuery();
            while (rs.next()) { System.out.println(rs.getString(1)+","+rs.getString(2)+","+rs.getString(3)+","+rs.getString(4)+","+rs.getString(5));}
            System.out.println("\n");
        }
        catch(SQLException e) {System.out.println(e.getMessage());}

    }

    public static void addGoals(Connection conn) {
        Scanner scan= new Scanner(System.in);
        //flags to run an loop until a valid input
        boolean idFlag=true,teamFlag=true,scoreFlag=true,nameFlag=true;
        int matchid = 0, gametime = 0;
        String teamid="",player="";

        //test if user enters a valid game id
        while (idFlag) {

           System.out.println("Enter match id");

           if (scan.hasNextInt()) {
               matchid = scan.nextInt();
               if (matchid >= 1001 && matchid <= 1031) {System.out.println("Valid id");break; }
               else {System.out.println("Invalid id. The game ids range from 1001 to 1031");scan.nextLine();}
           }
           else {System.out.println("Invalid id. The game ids range from 1001 to 1031");scan.nextLine(); }
       }
        //test if user enters a valid team id that represent the teams that played in that match
       while(teamFlag)
       {
           System.out.println(matchid);
           System.out.println("Enter the team id of the team that scored");
           if (scan.hasNext()) {
               teamid = scan.next().toUpperCase();
               //runs a query search to ensure the game exist with that particular team
               String verifyteam = "select * from game where id=? and (team1=? or team2=?)";
               try {
                   PreparedStatement pstmt = conn.prepareStatement(verifyteam);
                   pstmt.setInt(1,matchid);
                   pstmt.setString(2,teamid);
                   pstmt.setString(3,teamid);
                   ResultSet rs = pstmt.executeQuery();
                   // if there is an entry for that game id with that team, it breaks out of the loop
                   if(rs.next()) {System.out.println(teamid+" is a valid input");break;}
                   else{System.out.println(teamid+" did not play in this game");}
               }
               catch (SQLException e) {System.out.println(e.getMessage());}
           }
           else{System.out.println("Invalid input for team id"); scan.nextLine();};
       }
       //Checks for a valid entry for a goal
       while(scoreFlag)
       {
        System.out.println("Enter the time goal was scored");
        if (scan.hasNextInt()) { // looks for an integer input
            gametime = scan.nextInt();
            String verifytime = "select * from goal where matchid=? and gtime=?";
            try {
                PreparedStatement pstmt = conn.prepareStatement(verifytime);
                pstmt.setInt(1, matchid);
                pstmt.setInt(2, gametime);
                ResultSet rs = pstmt.executeQuery();
                //Only 1 goal can be scored each minute. So if there is a match for that particular goal at that time, the loop continues.
                if (rs.next()) {System.out.println("A goal has already been scored at gtime: " + gametime);}
                //if no goal was scored at that time, the entry is valid
                else {System.out.println(gametime + " is a valid game time.");break;}
            }
            catch(SQLException e){ System.out.println(e.getMessage());}
        }
        else {System.out.println("Invalid input");scan.nextLine();}

       }

       while (nameFlag)//enters a name for the user who scored the goal
       {
           System.out.println("Enter the name of the player who scored");
           try
           {
               scan.nextLine();
               player=scan.nextLine();
               System.out.println(player);
               break;

           }
           catch(InputMismatchException e)
           {
               System.out.println("Invalid input");
           }
       }
       //Once all the flags have been passed, the goal can be added into the table
       String update="Insert Into goal values(?,?,?,? )";
       try
       {
           PreparedStatement pstmt= conn.prepareStatement(update);
           pstmt.setInt(1,matchid);
           pstmt.setString(2,teamid);
           pstmt.setString(3,player);
           pstmt.setInt(4,gametime);
           pstmt.executeUpdate();
       }

       catch (SQLException e){System.out.println(e.getMessage());}



    }

    public static void main(String[] args) {
//        while(true)
            connect();
            System.out.println("Hello World!");
    }
}
