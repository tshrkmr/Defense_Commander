package edu.depaul.tkumar.defensecommander;

import android.util.Log;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ScoreDataBaseHandler implements Runnable{

    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

    private MainActivity mainActivity;
    private static String dbURL;
    private Connection conn;
    private static final String APP_SCORES = "AppScores";
    private final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());
    private static final String TAG = "ScoreDataBaseHandler";

    private String initials;
    private int score;
    private int level;
    private String status;

    ScoreDataBaseHandler(MainActivity mainActivity, String initials, int score, int level, String status) {
        this.mainActivity = mainActivity;
        this.initials = initials;
        this.score = score;
        this.level = level;
        this.status = status;
        dbURL = "jdbc:mysql://christopherhield.com:3306/chri5558_missile_defense";
    }

    @Override
    public void run() {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(dbURL, "chri5558_student", "ABC.123");
            if(status == "compareScore"){
                getAll(status);
                return;
            }

            Statement stmt = conn.createStatement();

            String sql = "insert into " + APP_SCORES + " values (" +
                    System.currentTimeMillis() + ", '" + initials + "', " + score + ", " +
                    level +
                    ")";

            int result = stmt.executeUpdate(sql);

            stmt.close();

            String response = "Student " + initials + " added (" + result + " record)\n\n";
            StringBuilder sb = new StringBuilder();
            sb.append(response);
            sb.append(getAll(status));

            //mainActivity.setResults(sb.toString());
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getAll(String status) throws SQLException {

        Statement stmt = conn.createStatement();

        String sql = "select * from " + APP_SCORES + " order by Score DESC LIMIT 10";

        StringBuilder sb = new StringBuilder();

        ResultSet rs = stmt.executeQuery(sql);
        int min = Integer.MAX_VALUE;
//        if(status == "compareScore"){
//            while (rs.next()) {
//                int points = rs.getInt(3);
//                if(points<min)
//                    min =
//            }
//        }
        int position = 0;
        while (rs.next()) {
            long millis= rs.getLong(1);
            String name = rs.getString(2);
            int points = rs.getInt(3);
            long stage = rs.getInt(4);
            if(points<min)
                min = points;
//            sb.append(String.format(Locale.getDefault(),
//                    "%-10d %-12s %8.2f %12s%n", position, name, points, sdf.format(new Date(millis))));

            Log.d(TAG, "getAll: " + name + " " + points + " " + stage + " " + min);
        }
        rs.close();
        stmt.close();

        if(status == "compareScore"){
            mainActivity.compareScore(min, sb.toString());
        }

        return sb.toString();
    }
}
