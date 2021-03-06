package edu.depaul.tkumar.defensecommander;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ScoreDataBaseHandler implements Runnable {

    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

    private final MainActivity mainAcctivity;
    private static String dbURL;
    private Connection conn;
    private static final String APP_SCORES = "AppScores";
    private final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());
    private static final String TAG = "ScoreDataBaseHandler";

    private final String initials;
    private final int score;
    private final int level;
    private final String status;

    ScoreDataBaseHandler(MainActivity mainActivity, String initials, int score, int level, String status) {
        this.mainAcctivity = mainActivity;
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
            if(status.equals(MainActivity.checkLeaderboard)){
                getAll();
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
            Log.d(TAG, "run: " + response);
            getAll();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getAll() throws SQLException {

        Statement stmt = conn.createStatement();

        String sql = "select * from " + APP_SCORES + " order by Score DESC LIMIT 10";

        StringBuilder sb = new StringBuilder();
        sb.append(String.format(Locale.getDefault(), "%20s %20s %20s %20s %20s%n", "#", "INIT", "Level", "Score", "Date/Time"));
        ResultSet rs = stmt.executeQuery(sql);
        int min = Integer.MAX_VALUE;
        int i = 0;
        while (rs.next()) {
            long millis= rs.getLong(1);
            String init = rs.getString(2);
            int score1 = rs.getInt(3);
            int level1 = rs.getInt(4);
            if(score1<min)
                min = score1;
            sb.append(String.format(Locale.getDefault(), "%20s %20s %20s %20s %30s%n", i++, init, level1, score1, sdf.format(new Date(millis))));

            Log.d(TAG, "getAll: " + init + " " + score1 + " " + level1 + " " + min);
        }
        rs.close();
        stmt.close();

        if(status.equals(MainActivity.checkLeaderboard)){
            mainAcctivity.compareScore(min, sb.toString());
        }
        if(status.equals(MainActivity.updateLeaderborad)){
            mainAcctivity.setUpdatedLeaderborad(sb.toString());
        }

        return sb.toString();
    }
}
