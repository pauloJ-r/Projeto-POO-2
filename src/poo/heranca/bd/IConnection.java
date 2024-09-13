package poo.heranca.bd;

import java.sql.Connection;

public interface IConnection {
	Connection getConnection();
    void closeConnection();
}
