package poo.heranca.bd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import poo.heranca.banco.util.ConfigLoader;

public class ConnectionMySQL implements IConnection {
	
	private final String DB_USER;
    private final String DB_PASSWORD;
    private final String DB_HOST;
    private final String DB_NAME;
    private final String DB_PORT;

    {
           DB_USER = ConfigLoader.getInstance("config.properties").getProperty("DB_USER");
           DB_PASSWORD = ConfigLoader.getInstance("config.properties").getProperty("DB_PASSWORD");
           DB_HOST = ConfigLoader.getInstance("config.properties").getProperty("DB_HOST");
           DB_NAME = ConfigLoader.getInstance("config.properties").getProperty("DB_NAME");
           DB_PORT = ConfigLoader.getInstance("config.properties").getProperty("DB_PORT");
       }


	@Override
	public Connection getConnection() {
		// TODO Auto-generated method stub
		 Connection c = null;
         try {
             c = DriverManager.getConnection("jdbc:mysql://"+DB_HOST+":"+DB_PORT+"/"+DB_NAME, DB_USER, DB_PASSWORD);
         } catch (SQLException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
         }
         return c;
     }

	@Override
	public void closeConnection() {
		// TODO Auto-generated method stub
		
	}

}
