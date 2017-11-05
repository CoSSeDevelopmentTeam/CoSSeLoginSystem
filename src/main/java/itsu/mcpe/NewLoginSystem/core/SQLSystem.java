package itsu.mcpe.NewLoginSystem.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.HashMap;
import java.util.Map;

import cn.nukkit.utils.TextFormat;

public class SQLSystem {

    private NewLoginSystem plugin;

    private Statement statement;
    private Statement statement1;
    
    private Encoder encoder = Base64.getEncoder();
    private Decoder decoder = Base64.getDecoder();

    public SQLSystem(NewLoginSystem plugin) {
        this.plugin = plugin;
        connectSQL();
        connectBANSQL();
    }

    public void connectSQL() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connection = null;
            connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder().toString() + "/LoginData.db");
            statement = connection.createStatement();
            statement.setQueryTimeout(30);
            statement.executeUpdate("CREATE table if not exists player (name text not null, password text not null, address text not null, autoLogin integer not null, mail text not null)");
        } catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void connectBANSQL() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connection = null;
            connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder().toString() + "/BANData.db");
            statement1 = connection.createStatement();
            statement1.setQueryTimeout(30);
            statement1.executeUpdate("CREATE table if not exists banplayer (name text not null, address text not null)");
        } catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public synchronized void createAccount(String name, String password, String address, int autoLogin, String mail) {
        try {
            statement.executeUpdate("INSERT INTO player(name, password, address, autoLogin, mail) VALUES('" + encoder.encodeToString(name.getBytes()) + "', '" + encoder.encodeToString(password.getBytes()) + "', '" + encoder.encodeToString(address.getBytes()) + "', " + autoLogin + ", '" + encoder.encodeToString(mail.getBytes()) + "')");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized void createBAN(String name, String address) {
    	if(existsAccount(name)) {
	        try {
	        	statement1.executeUpdate("INSERT INTO banplayer(name, address) VALUES('" + encoder.encodeToString(name.getBytes()) + "', '" + encoder.encodeToString(address.getBytes()) + "')");
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
    	}
    }

    public boolean existsAccount(String name) {
        try {
            ResultSet rs = statement.executeQuery("select name from player where name = '"+ encoder.encodeToString(name.getBytes()) +"'");
            if(rs.getString("name") != null) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    public boolean existsBAN(String name) {
        try {
            ResultSet rs = statement1.executeQuery("select name from banplayer where name = '"+ encoder.encodeToString(name.getBytes()) +"'");
            if(rs.getString("name") != null) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }
    
    public synchronized boolean deleteBAN(String name) {
        try {
            if(existsAccount(name) && existsBAN(name)) {
                statement.executeUpdate("DELETE from banplayer WHERE name = '"+ encoder.encodeToString(name.getBytes()) + "'");
            } else {
            	plugin.getServer().getPlayer(name).sendMessage(TextFormat.RED + "[NewLoginSystem] その人はBANされていません。");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public String getBANAddress(String name) {
        Map<String, Object> list = new HashMap<String, Object>();
        try {
            ResultSet rs = statement1.executeQuery("SELECT * from banplayer WHERE (name = '"+ encoder.encodeToString(name.getBytes()) +"')");
            while(rs.next()) {
                list.put("address", rs.getString("address"));
            }
            rs.close();
            return new String(decoder.decode(((String) list.get("address"))));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getPlayer(String name) {
        Map<String, Object> list = new HashMap<String, Object>();
        try {
            ResultSet rs = statement.executeQuery("SELECT * from player WHERE (name = '"+ encoder.encodeToString(name.getBytes()) +"')");
            while(rs.next()) {
                list.put("player", rs.getString("player"));
            }
            rs.close();
            return new String(decoder.decode(((String) list.get("player"))));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getPassword(String name) {
        Map<String, Object> list = new HashMap<String, Object>();
        try {
            ResultSet rs = statement.executeQuery("SELECT * from player WHERE (name = '"+ encoder.encodeToString(name.getBytes()) +"')");
            while(rs.next()) {
                list.put("password", rs.getString("password"));
            }
            rs.close();
            return new String(decoder.decode(((String) list.get("password"))));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getAddress(String name) {
        Map<String, Object> list = new HashMap<String, Object>();
        try {
            ResultSet rs = statement.executeQuery("SELECT * from player WHERE (name = '"+ encoder.encodeToString(name.getBytes()) +"')");
            while(rs.next()) {
                list.put("address", rs.getString("address"));
            }
            rs.close();
            return new String(decoder.decode(((String) list.get("address"))));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public int getAutoLogin(String name) {
        Map<String, Object> list = new HashMap<String, Object>();
        try {
            ResultSet rs = statement.executeQuery("SELECT * from player WHERE (name = '"+ encoder.encodeToString(name.getBytes()) +"')");
            while(rs.next()) {
                list.put("autoLogin", rs.getInt("autoLogin"));
            }
            rs.close();
            return (int) list.get("autoLogin");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    public String getMail(String name) {
        Map<String, Object> list = new HashMap<String, Object>();
        try {
            ResultSet rs = statement.executeQuery("SELECT * from player WHERE (name = '"+ encoder.encodeToString(name.getBytes()) +"')");
            while(rs.next()) {
                list.put("mail", rs.getString("mail"));
            }
            rs.close();
            return new String(decoder.decode(((String) list.get("mail"))));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public synchronized void updatePassword(String name, String password) {
        if(existsAccount(name)){
            try {
                statement.execute("update player set password = '" + encoder.encodeToString(password.getBytes()) + "' where name = '" + encoder.encodeToString(name.getBytes()) + "'");
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public synchronized void updateAddress(String name, String address) {
        if(existsAccount(name)){
            try {
                statement.execute("update player set address = '" + encoder.encodeToString(address.getBytes()) + "' where name = '" + encoder.encodeToString(name.getBytes()) + "'");
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public synchronized void updateAutoLogin(String name, int autoLogin) {
        if(existsAccount(name)){
            try {
                statement.execute("update player set autoLogin = " + autoLogin + " where name = '" + encoder.encodeToString(name.getBytes()) + "'");
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }

}
