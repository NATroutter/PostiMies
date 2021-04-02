package net.natroutter.postimies.Utilities;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.natroutter.postimies.Postimies;
import net.natroutter.postimies.Utilities.objects.Package;

import java.io.File;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.ArrayList;

public class Database {

    HikariConfig hikConfig;
    HikariDataSource hikData;
    String jarFolder;
    boolean Valid = false;


    public Database() {
        try {
            jarFolder = new File(Postimies.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().getPath().replace('\\', '/');

            hikConfig = new HikariConfig();
            hikConfig.setPoolName("SQLiteConnectionPool");
            hikConfig.setDriverClassName("org.sqlite.JDBC");
            hikConfig.setJdbcUrl("jdbc:sqlite:" + jarFolder + "/database.db");
            hikData = new HikariDataSource(hikConfig);

            Connection con = hikData.getConnection();
            PreparedStatement stmt = con.prepareStatement("CREATE TABLE IF NOT EXISTS 'Packages' ('Id' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, 'UserID' TEXT NOT NULL, 'UserName' TEXT NOT NULL, 'PackageName' TEXT NOT NULL, 'TrackingCode' TEXT NOT NULL, 'CarrierName' TEXT NOT NULL, 'CarrierCode' TEXT NOT NULL, 'LastUpdate' TEXT NOT NULL);");
            stmt.execute();
            stmt.close();
            con.close();
            Valid = true;
        } catch (URISyntaxException e) {
            Logger.Error("Failed to get jarFolder!");
            e.printStackTrace();

        } catch (SQLException e) {
            Logger.Error("Database connection error!");
            e.printStackTrace();
        }
    }

    public boolean insert(Package pack) {
        if (!Valid) {Logger.Error("Database is not valid!"); return false;}

        Connection con = null;
        PreparedStatement stmt = null;
        try {
            String sql = "INSERT INTO Packages (UserID, UserName, PackageName, TrackingCode, CarrierName, CarrierCode, LastUpdate) VALUES (?, ?, ?, ?, ?, ?, ?);";
            con = hikData.getConnection();
            stmt = con.prepareStatement(sql);
            stmt.setString(1, pack.getUserID());
            stmt.setString(2, pack.getUserName());
            stmt.setString(3, pack.getPackageName());
            stmt.setString(4, pack.getTrackingCode());
            stmt.setString(5, pack.getCourierName());
            stmt.setString(6, pack.getCourierCode());
            stmt.setString(7, pack.getLastUpdate());
            stmt.execute();
            con.close();
            return true;
        } catch (Exception e) {
            Logger.Error("Failed to insert field to database!");
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {stmt.close();}
                if (con != null) {con.close();}
            } catch (Exception ignored) {}
        }
        return false;
    }

    public boolean updateDate(Integer id, String newDate) {
        if (!Valid) {Logger.Error("Database is not valid!"); return false;}

        Connection con = null;
        PreparedStatement stmt = null;

        try {
            String sql = "UPDATE Packages SET LastUpdate=? WHERE Id=?;";
            con = hikData.getConnection();
            stmt = con.prepareStatement(sql);
            stmt.setString(1, newDate);
            stmt.setInt(2, id);
            stmt.execute();
            con.close();
            return true;
        } catch (Exception e) {
            Logger.Error("Failed to insert field to database!");
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {stmt.close();}
                if (con != null) {con.close();}
            } catch (Exception ignored) {}
        }
        return false;
    }

    public Package get(Integer id) {
        if (!Valid) {Logger.Error("Database is not valid!"); return null;}

        Connection con = null;
        PreparedStatement stmt = null;

        try {
            String sql = "SELECT * FROM Packages WHERE Id=?;";
            con = hikData.getConnection();
            stmt = con.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet result = stmt.executeQuery();

            if (!result.next()) { return null; }

            Package pack = new Package(
                    result.getInt("Id"),
                    result.getString("UserID"),
                    result.getString("UserName"),
                    result.getString("PackageName"),
                    result.getString("TrackingCode"),
                    result.getString("CarrierName"),
                    result.getString("CarrierCode"),
                    result.getString("LastUpdate")
            );
            stmt.close();
            con.close();
            return pack;
        } catch (Exception e) {
            Logger.Error("Failed to get field from database!");
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {stmt.close();}
                if (con != null) {con.close();}
            } catch (Exception ignored) {}
        }
        return null;
    }

    public Package get(String trackingNumber) {
        if (!Valid) {Logger.Error("Database is not valid!"); return null;}

        Connection con = null;
        PreparedStatement stmt = null;

        try {
            String sql = "SELECT * FROM Packages WHERE TrackingCode=?;";
            con = hikData.getConnection();
            stmt = con.prepareStatement(sql);
            stmt.setString(1, trackingNumber);
            ResultSet result = stmt.executeQuery();

            if (!result.next()) { return null; }

            Package pack = new Package(
                    result.getInt("Id"),
                    result.getString("UserID"),
                    result.getString("UserName"),
                    result.getString("PackageName"),
                    result.getString("TrackingCode"),
                    result.getString("CarrierName"),
                    result.getString("CarrierCode"),
                    result.getString("LastUpdate")
            );
            stmt.close();
            con.close();
            return pack;
        } catch (Exception e) {
            Logger.Error("Failed to get field from database!");
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {stmt.close();}
                if (con != null) {con.close();}
            } catch (Exception ignored) {}
        }
        return null;
    }

    public ArrayList<Package> getAll() {
        if (!Valid) {Logger.Error("Database is not valid!"); return null;}
        ArrayList<Package> packages = new ArrayList<>();

        Connection con = null;
        PreparedStatement stmt = null;

        try {
            String sql = "SELECT * FROM Packages;";
            con = hikData.getConnection();
            stmt = con.prepareStatement(sql);
            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                packages.add(new Package(
                        result.getInt("Id"),
                        result.getString("UserID"),
                        result.getString("UserName"),
                        result.getString("PackageName"),
                        result.getString("TrackingCode"),
                        result.getString("CarrierName"),
                        result.getString("CarrierCode"),
                        result.getString("LastUpdate")
                ));
            }
            stmt.close();
            con.close();
            return packages;
        } catch (Exception e) {
            Logger.Error("Failed to get field from database!");
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {stmt.close();}
                if (con != null) {con.close();}
            } catch (Exception ignored) {}
        }
        return null;
    }

    public boolean rename(Integer id, String name) {
        if (!Valid) {Logger.Error("Database is not valid!"); return false;}

        Connection con = null;
        PreparedStatement stmt = null;

        try {
            String sql = "UPDATE Packages SET PackageName=? WHERE Id=?;";
            con = hikData.getConnection();
            stmt = con.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setInt(2, id);
            stmt.execute();
            stmt.close();
            con.close();
            return true;
        } catch (Exception e) {
            Logger.Error("Failed to reset database increment!");
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {stmt.close();}
                if (con != null) {con.close();}
            } catch (Exception ignored) {}
        }
        return false;
    }

    public boolean delete(Integer id) {
        if (!Valid) {Logger.Error("Database is not valid!"); return false;}

        Connection con = null;
        PreparedStatement stmt = null;

        try {
            String sql = "DELETE FROM Packages WHERE Id=?;";
            con = hikData.getConnection();
            stmt = con.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.execute();
            stmt.close();
            con.close();
            return resetIncrement();
        } catch (Exception e) {
            Logger.Error("Failed to delete field from database!");
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {stmt.close();}
                if (con != null) {con.close();}
            } catch (Exception ignored) {}
        }
        return false;
    }

    public boolean resetIncrement() {
        if (!Valid) {Logger.Error("Database is not valid!"); return false;}

        Connection con = null;
        PreparedStatement stmt = null;

        try {
            String sql = "UPDATE sqlite_sequence SET seq=0 WHERE name='Packages';";
            con = hikData.getConnection();
            stmt = con.prepareStatement(sql);
            stmt.execute();
            stmt.close();
            con.close();
            return true;
        } catch (Exception e) {
            Logger.Error("Failed to reset database increment!");
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {stmt.close();}
                if (con != null) {con.close();}
            } catch (Exception ignored) {}
        }
        return false;
    }



}



























