package dataAccessLayer;
 
import java.sql.Connection;
import java.sql.DriverManager;
import android.util.Log;

public class connectDB {

public Connection conect(){
	
    Connection con = null;
    String url = "jdbc:postgresql://104.236.202.116:5432/";
    String db = "PersonalSecurity";
    String driver = "org.postgresql.Driver";
    String user = "postgres";
    String pass = "Postg123$";
    try {
    	 
		Class.forName("org.postgresql.Driver");

	} catch (ClassNotFoundException e) {

		System.out.println("Where is your PostgreSQL JDBC Driver? "
				+ "Include in your library path!");
		e.printStackTrace();
		

	}
    try{
        Class.forName(driver);
        con = DriverManager.getConnection(url + db, user, pass);
        if(con==null){
            System.out.println("Connection cannot be established");
            return null;
        }
        else
        {Log.i("TAG", "connecting sucessfully");
        }
        //con.close();
        return con;
    } catch (Exception e) {
        System.out.println("fuck you!");
        e.printStackTrace();
        return null;
    }
}
}
